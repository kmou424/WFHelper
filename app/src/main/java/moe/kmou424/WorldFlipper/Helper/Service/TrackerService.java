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
import moe.kmou424.WorldFlipper.Helper.Tools.BitmapUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.RootUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.ScreenUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.SimulateTouch;
import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;

public class TrackerService extends Service {
    private final static String LOG_TAG = "TrackerService";
    private final static int NOTIFICATION_ID = 102;

    private Bitmap mBitmap;
    private Handler mHandler;
    private SharedPreferences mSharedPreferences;
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
    private boolean isSwitchTeam = false;
    private boolean stopScreenShotThread = false;

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
            Logger.outWithSysStream(Logger.INFO, LOG_TAG, "initConfig", String.format("%s %b", mBossName, mBossInfo.isEnabled));
            for (String mLevel : mBossInfo.mLevels) {
                Logger.outWithSysStream(Logger.INFO, LOG_TAG, "init", mLevel);
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
        Logger.outWithSysStream(Logger.INFO, LOG_TAG, "NowTask", TASKS.get(msg.what));
        // ???????????????????????????????????????????????????
        if (checkDialog()) {
            mNowTask = NO_TASK;
            SimulateTouch.click(CoordinatePoints.DIALOG_BUTTON);
            String mDialogMessage = mTesseractOCRChi.getTextFromBitmap(BitmapUtils.crop(mBitmap, CoordinatePoints.DIALOG_MESSAGE));
            // ??????????????????????????????????????????
            if (mDialogMessage.contains(CheckPoints.DIALOG_MESSAGE_UPDATE_TIME)) {
                // ????????????????????????????????????
                mNowTask = GO_RE_LOGIN;
                // ?????????Handler???????????????????????????
                msg.what = GO_RE_LOGIN;
            }
            // ??????????????????????????????????????????
            if (mDialogMessage.contains(CheckPoints.DIALOG_MESSAGE_POP_OFF_LOGIN)) {
                // ??????????????????????????????????????????
                if (isReLoginDelayEnabled) mNowTask = GO_RE_LOGIN_DELAY;
                else mNowTask = GO_RE_LOGIN;
                // ?????????Handler?????????????????????????????????
                if (isReLoginDelayEnabled) msg.what = GO_RE_LOGIN_DELAY;
                else msg.what = GO_RE_LOGIN;
                // ???????????????
                reLoginDelayCnt = 0;
            }
        }
        if (isLoading()) {
            Logger.outWithSysStream(Logger.INFO, LOG_TAG, "NowTask", "?????????");
            if (mNowTask == GO_PREPARE_AS_GUEST && isEnteredRoom) {
                mNowTask = GO_WAITING_FOR_FINISH;
            }
            return;
        }
        switch (msg.what) {
            case GO_RE_LOGIN:
                if (!checkIsHomePage()) {
                    // ???????????????????????????????????????&????????????/??????
                    SimulateTouch.click(CoordinatePoints.NOTICE_CLOSE_BUTTON);
                }
                break;
            case GO_RE_LOGIN_DELAY:
                if (reLoginDelayCnt >= reLoginDelay) {
                    mNowTask = GO_RE_LOGIN;
                } else {
                    reLoginDelayCnt++;
                    Logger.outWithSysStream(Logger.INFO, LOG_TAG, "reLoginDelay", String.format("????????????????????????...??????%d???", reLoginDelay - reLoginDelayCnt));
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
                // ??????
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
                // ????????????
                if (mTesseractOCRChi.getTextFromBitmap(BitmapUtils.cropWhiteFont(mBitmap, CoordinatePoints.BATTLE_FAILED_RESURRECTION_BUTTON_TITLE)).contains(CheckPoints.BATTLE_FAILED_RESURRECTION_BUTTON_TITLE)) {
                    mNowTask = GO_MAIN_PAGE;
                    break;
                }
                // ??????????????????
                if (mTesseractOCRChi.getTextFromBitmap(BitmapUtils.cropWhiteFont(mBitmap, CoordinatePoints.BOTTOM_CONTINUE_BUTTON_TITLE)).contains(CheckPoints.BOTTOM_CONTINUE_BUTTON_TITLE)) {
                    SimulateTouch.click(CoordinatePoints.BOTTOM_CONTINUE);
                    isContinueClicked = true;
                }
                // ????????????????????????
                if (isContinueClicked) {
                    if (mTesseractOCRChi.getTextFromBitmap(BitmapUtils.cropWhiteFont(mBitmap, CoordinatePoints.BOTTOM_QUIT_ROOM_BUTTON_TITLE)).contains(CheckPoints.BOTTOM_QUIT_ROOM_BUTTON_TITLE)) {
                        SimulateTouch.click(CoordinatePoints.BOTTOM_QUIT_ROOM);
                        mNowTask = GO_MAIN_PAGE;
                        isContinueClicked = false;
                        break;
                    }
                    // ?????????????????????????????????????????????XD
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
        // ??????Map?????????key
        for (String mBossName : mBossInfoMap.keySet()) {
            // ??????????????????boss
            if (checkNameSimilarity(mBossStr, mBossName, 3)) {
                // ??????key?????????BossInfo??????
                BossInfo mBossInfo = mBossInfoMap.get(mBossName);
                // ???????????????
                if (mBossInfo != null) {
                    // ?????????Boss?????????????????????????????????false
                    if (!mBossInfo.isEnabled) return false;
                    // ????????????????????????????????????
                    for (String mLevel : mBossInfo.mLevels) {
                        if (mBossStr.endsWith(mLevel)) {
                            mHaveTargetTeam = false;
                            if (!mBossInfo.mTeam.equals("null")) {
                                for (int i = 0; i < AvailableTeam.length; ++i) {
                                    if (AvailableTeam[i].equals(mBossInfo.mTeam)) {
                                        // ?????????????????????idx
                                        mTargetTeamIdx = i;
                                        // ???????????????????????????true
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

    private boolean checkNameSimilarity(String mName, String mTargetName, int mAccuracy) {
        int cnt = 0;
        for(int i = 0; i < mName.length(); i++){
            String ch = String.valueOf(mName.charAt(i));
            if (mTargetName.contains(ch)) cnt++;
        }
        return cnt >= mAccuracy;
    }

    private boolean switchTeam() {
        // ?????????????????????
        if (mHaveTargetTeam) {
            // ??????????????????idx
            int cur = getCurTeamIdx();
            if (cur == -1) {
                // ?????????????????????????????????????????????
                SimulateTouch.click(CoordinatePoints.PREPARE_EDIT_TEAM_BUTTON);
                return false;
            }
            // ?????????????????????
            if (cur == mTargetTeamIdx) {
                // ??????????????????
                SimulateTouch.click(CoordinatePoints.PREPARE_EDIT_TEAM_OK);
                // ?????????????????????????????????????????????
                mHaveTargetTeam = false;
                isSwitchTeam = false;
                return true;
            } else {
                // ??????????????????idx??????????????????idx?????????????????????????????????
                isSwitchTeam = true;
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
                if (isSwitchTeam) {
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (!stopScreenShotThread) {
                    // ????????????????????????
                    mTopProcess = RootUtils.getTopProcess();
                    // ??????????????????WF???????????????
                    if (mTopProcess.contains(WORLD_FLIPPER_PACKAGE_NAME_LT_SERVER)) {
                        mBitmap = ScreenUtils.takeScreenShotAsBitmap();
                        if (mBitmap != null) {
                            if (mNowTask == NO_TASK) mHandler.sendEmptyMessage(GO_CHECK_BITMAP);
                            else mHandler.sendEmptyMessage(mNowTask);
                        } else {
                            MainActivity.mHandler.sendMessage(new HandlerMessage<>()
                                    .make(new ToastHandlerMsg("?????????????????????", LENGTH_LONG), SHOW_TOAST));
                            MainActivity.mHandler.sendEmptyMessage(STOP_TRACKER_SERVICE);
                            return;
                        }
                    }
                    // ?????????????????????0.75???
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