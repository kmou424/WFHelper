package moe.kmou424.WorldFlipper.Helper;

import android.content.Context;
import android.os.Looper;

import moe.kmou424.WorldFlipper.Helper.Task.PreLoaderTask;
import moe.kmou424.WorldFlipper.Helper.Thread.WFThread;

class MainThread extends Thread {
    private final static String LOG_TAG = "MainThread";
    private final Context mContext;
    PreLoaderTask mPreLoaderTask;
    WFThread mPreLoadFilesChecker;

    public MainThread(Context mContext) {
        this.mContext = mContext;
    }

    private void init() {
        // TaskClass
        mPreLoaderTask = new PreLoaderTask(mContext);
        // Initial task thread
        mPreLoadFilesChecker = mPreLoaderTask.getPreLoadFilesChecker(null);
    }

    @Override
    public void run() {
        Looper.prepare();
        init();
        mPreLoadFilesChecker.start();
        mPreLoadFilesChecker.waitFor();
        //mHandler.sendMessage(new HandlerMessage<>().make(null, MOVE_TASK_TO_BACK));
    }
}