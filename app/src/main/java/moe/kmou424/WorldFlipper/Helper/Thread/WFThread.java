package moe.kmou424.WorldFlipper.Helper.Thread;

import android.os.Handler;
import android.os.Looper;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.UI.ProgressDialogHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;
import moe.kmou424.WorldFlipper.Helper.MainActivity;

public class WFThread extends Thread {
    private final static String LOG_TAG = "WFThread";

    private final Handler mHandler;
    private final Thread mLastThread;
    private final ProgressDialogHandlerMsg mProgressDialogHandlerMsg;

    public WFThread(Thread mLastThread) {
        this.mHandler = MainActivity.mHandler;
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
                mHandler.sendMessage(new HandlerMessage<ProgressDialogHandlerMsg>().make(mProgressDialogHandlerMsg, HandlerMessage.SHOW_PROGRESS_DIALOG));
            }
            while (mLastThread.isAlive()) {
                Logger.outWithSysStream(Logger.INFO, LOG_TAG,  "run",
                        "Last thread \"" + mLastThread.getName() + "\" is running, waiting...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mHandler != null) {
                mHandler.sendMessage(new HandlerMessage<ProgressDialogHandlerMsg>().make(null, HandlerMessage.HIDE_PROGRESS_DIALOG));
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
