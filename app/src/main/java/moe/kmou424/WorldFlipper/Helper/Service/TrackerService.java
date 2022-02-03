package moe.kmou424.WorldFlipper.Helper.Service;

import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.Action.ToastHandlerMsg.LENGTH_LONG;
import static moe.kmou424.WorldFlipper.Helper.Constants.Global.*;
import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage.*;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

import moe.kmou424.WorldFlipper.Helper.Constants.CheckPoints;
import moe.kmou424.WorldFlipper.Helper.Constants.SharedPreferencesConfigs;
import moe.kmou424.WorldFlipper.Helper.Constants.Global;
import moe.kmou424.WorldFlipper.Helper.Constants.CoordinatePoints;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.Action.ToastHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage;
import moe.kmou424.WorldFlipper.Helper.Info.BossInfo;
import moe.kmou424.WorldFlipper.Helper.Info.BossInfoWrapper;
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
    private String mImageFilePath;
    private String mTopProcess;
    private TesseractOCR mTesseractOCRChi;
    private Thread mScreenShotThread;
    private int mNowTask = NO_TASK;

    private HashMap<String, BossInfo> mBossInfoMap;
    private boolean mHaveTargetTeam;
    private int mTargetTeamIdx;
    private boolean isReLoginDelayEnabled;
    private int reLoginDelay;
    private int reLoginDelayCnt;
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
        mTesseractOCRChi = new TesseractOCR("chi_sim");
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
        mBossInfoMap = new HashMap<>();
        for (BossInfoWrapper mBossInfoWrapper : BOSS_INFO_WRAPPER_LIST) {
            if (mBossInfoWrapper.mEndDateInfo != null) {
                if (!mBossInfoWrapper.mEndDateInfo.isAvailable) continue;
            }
            String mBossName = getString(mBossInfoWrapper.BossNameResId);
            BossInfo mBossInfo = new BossInfo(mBossName,
                    mSharedPreferences.getBoolean(mBossInfoWrapper.BossEnabledConf, true),
                    BossInfoWrapper.getBossLevels(mBossInfoWrapper.BossLevelsConf),
                    mSharedPreferences.getString(mBossInfoWrapper.BossTeamConf, "null"));
            Log.d(LOG_TAG, String.format("%s %b", mBossName, mBossInfo.isEnabled));
            Logger.out(Logger.INFO, LOG_TAG, "initConfig", String.format("%s %b", mBossName, mBossInfo.isEnabled));
            for (String mLevel : mBossInfo.mLevels) {
                Log.d(LOG_TAG, mLevel);
                Logger.out(Logger.INFO, LOG_TAG, "init", mLevel);
            }
            mBossInfoMap.put(mBossName, mBossInfo);
        }
        isReLoginDelayEnabled = mSharedPreferences.getBoolean(SharedPreferencesConfigs.RE_LOGIN_DELAY_ENABLED, false);
        reLoginDelay = mSharedPreferences.getInt(SharedPreferencesConfigs.RE_LOGIN_DELAY_VALUE, 0) * 80; // 60 / 0.75
        isWaitOthersReadyEnabled = mSharedPreferences.getBoolean(SharedPreferencesConfigs.WAIT_OTHERS_READY_CHECKBOX, false);
        isBellTrackerEnabled = mSharedPreferences.getBoolean(SharedPreferencesConfigs.BELL_TRACKER_SWITCH, false);
    }

    void initHandler() {
        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                mainChecker(msg);
            }
        };
    }

    @SuppressLint("DefaultLocale")
    private void mainChecker(@NonNull Message msg) {
        if (DEBUG) Log.d(LOG_TAG, "NowTask: " + TASKS.get(msg.what));
        Logger.out(Logger.INFO, LOG_TAG, "NowTask", TASKS.get(msg.what));
        // 检查对话框，有对话框的话就给他点了
        if (checkDialog()) {
            mNowTask = NO_TASK;
            SimulateTouch.click(CoordinatePoints.DIALOG_BUTTON);
            String mDialogMessage = mTesseractOCRChi.getTextFromBitmap(BitmapUtils.crop(mBitmap, CoordinatePoints.DIALOG_MESSAGE));
            // 判断对话框内容是否为日期变了
            if (mDialogMessage.contains(CheckPoints.DIALOG_MESSAGE_UPDATE_TIME)) {
                // 将任务设置为前往重新登录
                mNowTask = GO_RE_LOGIN;
                // 将本次Handler任务设置为重新登录
                msg.what = GO_RE_LOGIN;
            }
            // 判断对话框内容是否为被挤下线
            if (mDialogMessage.contains(CheckPoints.DIALOG_MESSAGE_POP_OFF_LOGIN)) {
                // 将任务设置为前往延时重新登录
                if (isReLoginDelayEnabled) mNowTask = GO_RE_LOGIN_DELAY;
                else mNowTask = GO_RE_LOGIN;
                // 将本次Handler任务设置为延时重新登录
                if (isReLoginDelayEnabled) msg.what = GO_RE_LOGIN_DELAY;
                else msg.what = GO_RE_LOGIN;
                // 重置计数器
                reLoginDelayCnt = 0;
            }
        }
        if (isLoading()) {
            Log.d(LOG_TAG, "NowTask: 加载中");
            Logger.out(Logger.INFO, LOG_TAG, "NowTask", "加载中");
            if (mNowTask == GO_PREPARE_AS_GUEST && isEnteredRoom) {
                mNowTask = GO_WAITING_FOR_FINISH;
            }
            return;
        }
        switch (msg.what) {
            case GO_RE_LOGIN:
                if (!checkIsHomePage()) {
                    // 点击公告关闭按钮位置来登录&跳过签到/公告
                    SimulateTouch.click(CoordinatePoints.NOTICE_CLOSE_BUTTON);
                }
                break;
            case GO_RE_LOGIN_DELAY:
                if (reLoginDelayCnt >= reLoginDelay) {
                    mNowTask = GO_RE_LOGIN;
                } else {
                    reLoginDelayCnt++;
                    Log.d(LOG_TAG, String.format("正在等待重新登录...还剩%d秒", reLoginDelay - reLoginDelayCnt));
                    Logger.out(Logger.INFO, LOG_TAG, "reLoginDelay", String.format("正在等待重新登录...还剩%d秒", reLoginDelay - reLoginDelayCnt));
                }
                break;
            case GO_CHECK_BITMAP:
                if (isBellTrackerEnabled && BitmapUtils.getHexRGB(mBitmap, CoordinatePoints.BELL_POINT_IN)
                        > BitmapUtils.getHexRGB(mBitmap, CoordinatePoints.BELL_POINT_OUT)) {
                    SimulateTouch.click(CoordinatePoints.BELL_POINT_IN);
                    mNowTask = GO_BELL;
                }
                break;
            case GO_BELL:
                if (checkIsHomePage()) break;
                if (!mTesseractOCRChi.getTextFromBitmap(BitmapUtils.crop(mBitmap, CoordinatePoints.BELL_DIALOG_TITLE)).contains(CheckPoints.BELL_DIALOG_TITLE))
                    break;
                if (checkBossEnabled(mTesseractOCRChi.getTextFromBitmap(BitmapUtils.crop(mBitmap, CoordinatePoints.BELL_DIALOG_BOSS_INFO)))) {
                    SimulateTouch.click(CoordinatePoints.BELL_JOIN);
                    mNowTask = GO_PREPARE_AS_GUEST;
                } else {
                    SimulateTouch.click(CoordinatePoints.BELL_QUIT);
                    mNowTask = NO_TASK;
                }
                break;
            case GO_PREPARE_AS_GUEST:
                if (checkIsHomePage()) break;
                // 换队
                if (!switchTeam()) break;
                if (CheckPoints.PREPARE_AS_GUEST_CHECKBOX_INACTIVATE_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.PREPARE_AS_GUEST_CHECKBOX))) {
                    isEnteredRoom = true;
                    if (isWaitOthersReadyEnabled) {
                        int cnt = countPrepared();
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
                if (checkDialog() || checkIsHomePage()) break;
                // 战败判定
                if (mTesseractOCRChi.getTextFromBitmap(BitmapUtils.cropWhiteFont(mBitmap, CoordinatePoints.BATTLE_FAILED_RESURRECTION_BUTTON_TITLE)).contains(CheckPoints.BATTLE_FAILED_RESURRECTION_BUTTON_TITLE)) {
                    mNowTask = GO_MAIN_PAGE;
                    break;
                }
                // 继续按钮判定
                if (mTesseractOCRChi.getTextFromBitmap(BitmapUtils.cropWhiteFont(mBitmap, CoordinatePoints.BOTTOM_CONTINUE_BUTTON_TITLE)).contains(CheckPoints.BOTTOM_CONTINUE_BUTTON_TITLE)) {
                    SimulateTouch.click(CoordinatePoints.BOTTOM_CONTINUE);
                    isContinueClicked = true;
                }
                // 离开房间按钮判定
                if (isContinueClicked) {
                    if (mTesseractOCRChi.getTextFromBitmap(BitmapUtils.cropWhiteFont(mBitmap, CoordinatePoints.BOTTOM_QUIT_ROOM_BUTTON_TITLE)).contains(CheckPoints.BOTTOM_QUIT_ROOM_BUTTON_TITLE)) {
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

    private int countPrepared() {
        int cnt = 0;
        if (CheckPoints.ROOM_PREPARE_READY_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.ROOM_PREPARE_STATUS_LEFT))) cnt++;
        if (CheckPoints.ROOM_PREPARE_READY_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.ROOM_PREPARE_STATUS_CENTER))) cnt++;
        if (CheckPoints.ROOM_PREPARE_READY_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.ROOM_PREPARE_STATUS_RIGHT))) cnt++;
        return cnt;
    }

    private boolean isLoading() {
        return mTesseractOCRChi.getTextFromBitmap(BitmapUtils.crop(mBitmap, CoordinatePoints.LOADING_TEXT)).contains(CheckPoints.LOADING_TEXT);
    }

    private boolean checkDialog() {
        if (DEBUG) Log.d(LOG_TAG, "checkDialog");
        return CheckPoints.DIALOG_BUTTON_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.DIALOG_BUTTON));
    }

    private boolean checkIsHomePage() {
        if (DEBUG) Log.d(LOG_TAG, "checkIsHomePage");
        if (CheckPoints.BOTTOM_NAV_HOME_ACTIVATE_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.BOTTOM_NAV_HOME_COLOR))) {
            mNowTask = NO_TASK;
            return true;
        }
        return false;
    }

    private boolean checkBossEnabled(String mBossStr) {
        if (mBossStr == null) return false;
        // 去除OCR难以准确识别的字符
        mBossStr = mBossStr.replaceAll("\\.", "");
        mBossStr = mBossStr.replaceAll("·", "");
        // 遍历Map的所有key
        for (String mBossName : mBossInfoMap.keySet()) {
            // 对于每个key去除OCR难以准确识别的字符
            String mBossNameTemp = mBossName;
            mBossNameTemp = mBossNameTemp.replaceAll("\\.", "");
            mBossNameTemp = mBossNameTemp.replaceAll("·", "");
            // 判断是否为此boss
            if (mBossStr.contains(mBossNameTemp)) {
                // 获取key对应的BossInfo对象
                BossInfo mBossInfo = mBossInfoMap.get(mBossName);
                // 空指针判断
                if (mBossInfo != null) {
                    // 如果此Boss状态为未开启，直接返回false
                    if (!mBossInfo.isEnabled) return false;
                    Log.d(LOG_TAG, mBossStr);
                    // 去除掉字符串中的boss名字，只留下难度
                    mBossStr = mBossStr.replace(mBossNameTemp, "");
                    Log.d(LOG_TAG, mBossStr);
                    // 遍历判断此难度是否已勾选
                    for (String mLevel : mBossInfo.mLevels) {
                        if (mLevel.equals(mBossStr)) {
                            mHaveTargetTeam = false;
                            if (!mBossInfo.mTeam.equals("null")) {
                                for (int i = 0; i < AvailableTeam.length; ++i) {
                                    if (AvailableTeam[i].equals(mBossInfo.mTeam)) {
                                        // 找到就更新队伍idx
                                        mTargetTeamIdx = i;
                                        // 设置需要更换队伍为true
                                        mHaveTargetTeam = true;
                                        break;
                                    }
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean switchTeam() {
        // 是否指定过队伍
        if (mHaveTargetTeam) {
            // 获取当前队伍idx
            int cur = getCurTeamIdx();
            if (cur == -1) {
                // 没找到就是没在编队界面，点进去
                SimulateTouch.click(CoordinatePoints.PREPARE_EDIT_TEAM_BUTTON);
                return false;
            }
            // 开始判断小黄点
            if (cur == mTargetTeamIdx) {
                // 找到了就保存
                SimulateTouch.click(CoordinatePoints.PREPARE_EDIT_TEAM_OK);
                // 恢复状态，以免下次循环又进来了
                mHaveTargetTeam = false;
                return true;
            } else {
                // 如果目标队伍idx大于当前队伍idx，则往后翻，反之则往前
                if (mTargetTeamIdx > cur) {
                    swipeNext();
                } else {
                    swipeLast();
                }
                return false;
            }
        }
        return true;
    }

    private int getCurTeamIdx() {
        int ret = -1;
        for (int i = 0; i < AvailableTeam.length; ++i) {
            if (CheckPoints.EDIT_TEAM_INDICATOR_DOT_ACTIVE_COLOR.check(BitmapUtils.getPixelRgbInfo(mBitmap, CoordinatePoints.EDIT_TEAM_INDICATOR_DOTS[i]))) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    private void swipeNext() {
        SimulateTouch.swipe(new Point(500, 665), new Point(218, 665), 150);
    }

    private void swipeLast() {
        SimulateTouch.swipe(new Point(218, 665), new Point(500, 665), 150);
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
                        mImageFilePath = RootUtils.takeScreenShot();
                        if (mImageFilePath != null) {
                            mBitmap = BitmapUtils.read(mImageFilePath);
                            if (mNowTask == NO_TASK) mHandler.sendEmptyMessage(GO_CHECK_BITMAP);
                            else mHandler.sendEmptyMessage(mNowTask);
                        } else {
                            MainActivity.mHandler.sendMessage(new HandlerMessage<>()
                                    .make(new ToastHandlerMsg("截图时发生错误", LENGTH_LONG), SHOW_TOAST));
                            MainActivity.mHandler.sendEmptyMessage(STOP_TRACKER_SERVICE);
                            return;
                        }
                        FileUtils.deleteFile(mImageFilePath);
                    }
                    // 每次执行后休眠0.75秒
                    try {
                        Thread.sleep(750);
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
        mTesseractOCRChi.recycle();
        NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mManager.cancel(NOTIFICATION_ID);
        System.gc();
    }
}