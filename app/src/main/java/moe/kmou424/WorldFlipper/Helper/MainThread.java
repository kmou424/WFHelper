package moe.kmou424.WorldFlipper.Helper;

import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage.MOVE_TASK_TO_BACK;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage;
import moe.kmou424.WorldFlipper.Helper.Task.PreLoaderTask;
import moe.kmou424.WorldFlipper.Helper.Thread.WFThread;

class MainThread extends Thread {
    private final static String LOG_TAG = "MainThread";
    private final Context mContext;
    private final Handler mHandler;
    PreLoaderTask mPreLoaderTask;
    WFThread mPreLoadFilesChecker, mOCRLoader;

    public MainThread(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    private void init() {
        // TaskClass
        mPreLoaderTask = new PreLoaderTask(mContext, mHandler);
        // Initial task thread
        mPreLoadFilesChecker = mPreLoaderTask.getPreLoadFilesChecker(null);
        mOCRLoader = mPreLoaderTask.getOCRLoader(mPreLoadFilesChecker);
    }

    @Override
    public void run() {
        Looper.prepare();
        init();
        mPreLoadFilesChecker.start();
        mOCRLoader.start();
        mPreLoadFilesChecker.waitFor();
        //mHandler.sendMessage(new HandlerMessage<>().make(null, MOVE_TASK_TO_BACK));
    }
}