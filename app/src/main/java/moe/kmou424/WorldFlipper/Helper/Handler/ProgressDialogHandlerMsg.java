package moe.kmou424.WorldFlipper.Helper.Handler;

import android.app.ProgressDialog;

public class ProgressDialogHandlerMsg {
    private final static String LOG_TAG = "ProgressDialogHandlerMsg";

    public String mTitle, mMessage;
    public int mProgress, maxProgress;
    public int mProgressStyle;
    public boolean mCancelable;

    public final static int STYLE_HORIZONTAL = ProgressDialog.STYLE_HORIZONTAL;
    public final static int STYLE_SPINNER = ProgressDialog.STYLE_SPINNER;

    public ProgressDialogHandlerMsg(String mTitle, String mMessage, int mProgress, int maxProgress, int mProgressStyle,
                                    boolean mCancelable) {
        this.mTitle = mTitle;
        this.mMessage = mMessage;
        this.mProgress = mProgress;
        this.maxProgress = maxProgress;
        this.mProgressStyle = mProgressStyle;
        this.mCancelable = mCancelable;
    }
}
