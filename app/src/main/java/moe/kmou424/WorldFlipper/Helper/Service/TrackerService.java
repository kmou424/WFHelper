package moe.kmou424.WorldFlipper.Helper.Service;

import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.Action.ToastHandlerMsg.LENGTH_LONG;
import static moe.kmou424.WorldFlipper.Helper.Constants.Global.*;
import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import moe.kmou424.WorldFlipper.Helper.Constants.CheckPoints;
import moe.kmou424.WorldFlipper.Helper.Constants.SharedPreferencesConfigs;
import moe.kmou424.WorldFlipper.Helper.Constants.Global;
import moe.kmou424.WorldFlipper.Helper.Constants.CoordinatePoints;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.Action.ToastHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;
import moe.kmou424.WorldFlipper.Helper.MainActivity;
import moe.kmou424.WorldFlipper.Helper.R;
import moe.kmou424.WorldFlipper.Helper.Tools.FileUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.BitmapUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.RootUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.SimulateTouch;
import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;

public class TrackerService extends Service {
    private final static String LOG_TAG = "TrackerService";
    private final static int NOTIFICATION_ID = 102;

    private Bitmap mBitmap;
    private Handler mHandler;
    private SharedPreferences mSharedPreferences;
    private String mImagePath;
    private String mTopProcess;
    private TesseractOCR mTesseractOCR;
    private Thread mScreenShotThread;
    private int mNowTask = NO_TASK;

    private boolean isWaitOthersReadyEnabled;
    private boolean isBellTrackerEnabled;

    private boolean isEnteredRoom = false;
    private boolean isContinueClicked = false;
    private boolean stopScreenShotThread = false;

    public TrackerService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (Global.DEBUG) Log.d(LOG_TAG, "onDestroy");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        if (Global.DEBUG) Log.d(LOG_TAG, "onCreate");
        super.onCreate();
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mTesseractOCR = new TesseractOCR("chi_sim");
        initConfig();
        buildDaemon();
        initHandler();
        mScreenShotThread = initTrackerThread();
        mScreenShotThread.start();
    }

    void buildDaemon() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(LOG_TAG + " is running...");
        startForeground(NOTIFICATION_ID, builder.build());
    }

    void initConfig() {
        isWaitOthersReadyEnabled = mSharedPreferences.getBoolean(SharedPreferencesConfigs.WAIT_OTHERS_READY_CHECKBOX, false);
        isBellTrackerEnabled = mSharedPreferences.getBoolean(SharedPreferencesConfigs.BELL_TRACKER_SWITCH, false);
    }

    void initHandler() {
        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (DEBUG) Log.d(LOG_TAG, "NowTask: " + TASKS.get(msg.what));
                Logger.out(Logger.INFO, LOG_TAG, "NowTask", TASKS.get(msg.what));
                switch (msg.what) {
                    case GO_CHECK_BITMAP:
                        if (checkRoomDialog()) break;
                        if (isBellTrackerEnabled && BitmapUtils.getHexRGB(mBitmap, CoordinatePoints.BELL_POINT_IN)
                                > BitmapUtils.getHexRGB(mBitmap, CoordinatePoints.BELL_POINT_OUT)) {
                            SimulateTouch.click(CoordinatePoints.BELL_POINT_IN);
                            mNowTask = GO_BELL;
                        }
                        break;
                    case GO_BELL:
                        if (checkRoomDialog() || checkIsHomePage()) break;
                        if (!mTesseractOCR.getTextFromBitmap(BitmapUtils.crop(mBitmap, CoordinatePoints.BELL_DIALOG_TITLE)).contains(CheckPoints.BELL_DIALOG_TITLE))
                            break;
                        SimulateTouch.click(CoordinatePoints.BELL_JOIN);
                        mNowTask = GO_PREPARE_AS_GUEST;
                        break;
                    case GO_PREPARE_AS_GUEST:
                        if (checkRoomDialog() || checkIsHomePage()) break;
                        if (isEnteredRoom) {
                            if (mTesseractOCR.getTextFromBitmap(BitmapUtils.crop(mBitmap, CoordinatePoints.LOADING_TEXT)).contains(CheckPoints.LOADING_TEXT)) {
                                mNowTask = GO_WAITING_FOR_FINISH;
                                break;
                            }
                        }
                        if (CheckPoints.PREPARE_AS_GUEST_CHECKBOX_INACTIVATE_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.PREPARE_AS_GUEST_CHECKBOX))) {
                            isEnteredRoom = true;
                            if (isWaitOthersReadyEnabled) {
                                int cnt = 0;
                                if (CheckPoints.ROOM_PREPARE_READY_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.ROOM_PREPARE_STATUS_LEFT))) cnt++;
                                if (CheckPoints.ROOM_PREPARE_READY_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.ROOM_PREPARE_STATUS_CENTER))) cnt++;
                                if (CheckPoints.ROOM_PREPARE_READY_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.ROOM_PREPARE_STATUS_RIGHT))) cnt++;
                                if (cnt < 2) {
                                    break;
                                }
                            }
                            SimulateTouch.click(CoordinatePoints.PREPARE_AS_GUEST_CHECKBOX);
                            mNowTask = GO_WAITING_FOR_FINISH;
                        }
                        break;
                    case GO_WAITING_FOR_FINISH:
                        if (isEnteredRoom) isEnteredRoom = false;
                        if (checkRoomDialog() || checkIsHomePage()) break;
                        // 战败判定
                        if (mTesseractOCR.getTextFromBitmap(BitmapUtils.cropWhiteFont(mBitmap, CoordinatePoints.BATTLE_FAILED_RESURRECTION_BUTTON_TITLE)).contains(CheckPoints.BATTLE_FAILED_RESURRECTION_BUTTON_TITLE)) {
                            mNowTask = GO_MAIN_PAGE;
                            break;
                        }
                        // 继续按钮判定
                        if (mTesseractOCR.getTextFromBitmap(BitmapUtils.cropWhiteFont(mBitmap, CoordinatePoints.BOTTOM_CONTINUE_BUTTON_TITLE)).contains(CheckPoints.BOTTOM_CONTINUE_BUTTON_TITLE)) {
                            SimulateTouch.click(CoordinatePoints.BOTTOM_CONTINUE);
                            isContinueClicked = true;
                        }
                        // 离开房间按钮判定
                        if (isContinueClicked) {
                            if (mTesseractOCR.getTextFromBitmap(BitmapUtils.cropWhiteFont(mBitmap, CoordinatePoints.BOTTOM_QUIT_ROOM_BUTTON_TITLE)).contains(CheckPoints.BOTTOM_QUIT_ROOM_BUTTON_TITLE)) {
                                SimulateTouch.click(CoordinatePoints.BOTTOM_QUIT_ROOM);
                                mNowTask = GO_MAIN_PAGE;
                                isContinueClicked = false;
                                break;
                            }
                            // 无脑暴力点击，解决所有特殊事件XD
                            SimulateTouch.click(CoordinatePoints.BOTTOM_CONTINUE);
                        }
                        break;
                    case GO_MAIN_PAGE:
                        if (!checkIsHomePage()) {
                            if (CheckPoints.BOTTOM_NAV_HOME_INACTIVATE_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.BOTTOM_NAV_HOME_COLOR))) {
                                SimulateTouch.click(CoordinatePoints.BOTTOM_NAV_HOME_COLOR);
                                mNowTask = NO_TASK;
                            }
                        }
                        break;
                }
            }
        };
    }

    private boolean checkRoomDialog() {
        if (DEBUG) Log.d(LOG_TAG, "checkRoomDialog");
        if (CheckPoints.ROOM_ENTER_FAILED_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.ENTER_ROOM_FAILED))) {
            SimulateTouch.click(CoordinatePoints.ENTER_ROOM_FAILED);
            mNowTask = NO_TASK;
            return true;
        }
        return false;
    }

    private boolean checkIsHomePage() {
        if (DEBUG) Log.d(LOG_TAG, "checkIsHomePage");
        if (CheckPoints.BOTTOM_NAV_HOME_ACTIVATE_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.BOTTOM_NAV_HOME_COLOR))) {
            mNowTask = NO_TASK;
            return true;
        }
        return false;
    }

    Thread initTrackerThread() {
        return new Thread() {
            @SuppressWarnings("BusyWait")
            @Override
            public void run() {
                super.run();
                while (!stopScreenShotThread) {
                    // 获取前台进程包名
                    mTopProcess = RootUtils.getTopProcess();
                    // 若前台进程是WF才开始截图
                    if (mTopProcess.contains(WORLD_FLIPPER_PACKAGE_NAME_LT_SERVER)) {
                        mImagePath = RootUtils.takeScreenShot();
                        if (mImagePath != null) {
                            mBitmap = BitmapUtils.read(mImagePath);
                            if (mNowTask == NO_TASK) mHandler.sendEmptyMessage(GO_CHECK_BITMAP);
                            else mHandler.sendEmptyMessage(mNowTask);
                        } else {
                            MainActivity.mHandler.sendMessage(new HandlerMessage<>()
                                    .make(new ToastHandlerMsg("截图时发生错误", LENGTH_LONG), SHOW_TOAST));
                            MainActivity.mHandler.sendEmptyMessage(STOP_TRACKER_SERVICE);
                            return;
                        }
                        FileUtils.deleteFile(mImagePath);
                    }
                    // 每次执行后休眠0.8秒
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Global.DEBUG) Log.d(LOG_TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (Global.DEBUG) Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
        stopScreenShotThread = true;
        while (true) {
            if (!mScreenShotThread.isAlive()) break;
        }
        mTesseractOCR.recycle();
        NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mManager.cancel(NOTIFICATION_ID);
        System.gc();
    }
}