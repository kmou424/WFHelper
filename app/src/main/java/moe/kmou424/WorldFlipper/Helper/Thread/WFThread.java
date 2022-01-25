package moe.kmou424.WorldFlipper.Helper.Thread;

import android.os.Handler;
import android.os.Looper;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.ProgressDialogHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMsg;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

public class WFThread extends Thread {
    private final static String LOG_TAG = "WFThread";

    private final Handler mHandler;
    private final Thread mLastThread;
    private final ProgressDialogHandlerMsg mProgressDialogHandlerMsg;

    public WFThread(Handler mHandler, Thread mLastThread) {
        this.mHandler = mHandler;
        this.mLastThread = mLastThread;
        this.mProgressDialogHandlerMsg = new ProgressDialogHandlerMsg(null, null, 0, 0, ProgressDialogHandlerMsg.STYLE_SPINNER, false);
    }

    /*
     * Run this thread
     */
    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        Looper.prepare();
        if (mLastThread != null) {
            if (mHandler != null) {
                mProgressDialogHandlerMsg.mMessage = "等待线程 " + "\"" + mLastThread.getName() + "\"";
                mHandler.sendMessage(new HandlerMsg<ProgressDialogHandlerMsg>().makeMessage(mProgressDialogHandlerMsg, HandlerMsg.SHOW_PROGRESS_DIALOG));
            }
            while (mLastThread.isAlive()) {
                Logger.out(Logger.INFO, LOG_TAG,  "run",
                        "Last thread \"" + mLastThread.getName() + "\" is running, waiting...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mHandler != null) {
                mHandler.sendMessage(new HandlerMsg<ProgressDialogHandlerMsg>().makeMessage(null, HandlerMsg.HIDE_PROGRESS_DIALOG));
            }
        }
    }

    /*
     * Waiting for thread to finished running
     */
    public void waitFor() {
        while (true) if (!this.isAlive()) break;
    }
}
