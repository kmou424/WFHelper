package moe.kmou424.WorldFlipper.Helper.Thread;

import android.os.Handler;

import moe.kmou424.WorldFlipper.Helper.Handler.ProgressDialogHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.Handler.HandlerMsg;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

public class WFThread<T> extends Thread {
    private final static String LOG_TAG = "WFThread";

    private final Handler mHandler;
    private final Thread mLastThread;
    private final ProgressDialogHandlerMsg mProgressDialogHandlerMsg;

    protected T Result;

    public WFThread(Handler mHandler, Thread mLastThread) {
        this.mHandler = mHandler;
        this.mLastThread = mLastThread;
        this.mProgressDialogHandlerMsg = new ProgressDialogHandlerMsg(LOG_TAG, "", 0, 0, ProgressDialogHandlerMsg.STYLE_SPINNER, false);
    }

    /*
     * Get result of thread
     * Type is depends on <T>
     */
    @SuppressWarnings("BusyWait")
    public T getResult() {
        while (this.isAlive()) {
            mProgressDialogHandlerMsg.mMessage = "正在加载";
            mHandler.sendMessage(new HandlerMsg<ProgressDialogHandlerMsg>().makeMessage(mProgressDialogHandlerMsg, HandlerMsg.SHOW_PROGRESS_DIALOG));
            Logger.out(Logger.INFO, LOG_TAG, "getResult",
                    "This thread \"" + this.getName() + "\" is running, waiting...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mHandler.sendMessage(new HandlerMsg<ProgressDialogHandlerMsg>().makeMessage(null, HandlerMsg.HIDE_PROGRESS_DIALOG));
        return this.Result;
    }

    /*
     * Run this thread
     */
    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        mProgressDialogHandlerMsg.mMessage = "等待上一个线程运行结束";
        mHandler.sendMessage(new HandlerMsg<ProgressDialogHandlerMsg>().makeMessage(mProgressDialogHandlerMsg, HandlerMsg.SHOW_PROGRESS_DIALOG));
        if (mLastThread != null) {
            while (mLastThread.isAlive()) {
                Logger.out(Logger.INFO, LOG_TAG,  "run",
                        "Last thread \"" + mLastThread.getName() + "\" is running, waiting...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mHandler.sendMessage(new HandlerMsg<ProgressDialogHandlerMsg>().makeMessage(null, HandlerMsg.HIDE_PROGRESS_DIALOG));
    }

    /*
     * Waiting for thread to finished running
     */
    public void waitFor() {
        while (true) if (!this.isAlive()) break;
    }
}
