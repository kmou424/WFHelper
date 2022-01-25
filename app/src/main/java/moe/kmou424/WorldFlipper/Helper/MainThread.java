package moe.kmou424.WorldFlipper.Helper;

import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMsg.MOVE_TASK_TO_BACK;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMsg;
import moe.kmou424.WorldFlipper.Helper.Task.PreLoaderTask;
import moe.kmou424.WorldFlipper.Helper.Task.WidgetTask;
import moe.kmou424.WorldFlipper.Helper.Thread.WFThread;
import moe.kmou424.WorldFlipper.Helper.Tools.FileUtils;

class MainThread extends Thread {
    private final static String LOG_TAG = "MainThread";
    private final Context mContext;
    private final Handler mHandler;
    FileUtils mFileUtils;
    PreLoaderTask mPreLoaderTask;
    WFThread mPreLoadFilesChecker, mOCRLoader;

    WidgetTask mWidgetTask;
    WFThread mFloatingWindow;

    public MainThread(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    private void init() {
        mFileUtils = new FileUtils(mContext);
        mPreLoaderTask = new PreLoaderTask(mContext, mFileUtils, mHandler);
        mPreLoadFilesChecker = mPreLoaderTask.getPreLoadFilesChecker(null);
        mOCRLoader = mPreLoaderTask.getOCRLoader(mPreLoadFilesChecker);
        mWidgetTask = new WidgetTask(mContext, mHandler);
        mFloatingWindow = mWidgetTask.getFloatingWindow(null, (Activity) mContext);
    }

    @Override
    public void run() {
        init();
        mPreLoadFilesChecker.start();
        mOCRLoader.start();
        mOCRLoader.waitFor();
        mFloatingWindow.start();
        mHandler.sendMessage(new HandlerMsg<>().makeMessage(null, MOVE_TASK_TO_BACK));
    }
}