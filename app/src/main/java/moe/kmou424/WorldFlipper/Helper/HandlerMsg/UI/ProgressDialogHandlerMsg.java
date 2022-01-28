package moe.kmou424.WorldFlipper.Helper.HandlerMsg.UI;

import android.app.ProgressDialog;

public class ProgressDialogHandlerMsg {
    public final String mTitle;
    public String mMessage;
    public final int mProgress;
    public final int maxProgress;
    public final int mProgressStyle;
    public final boolean mCancelable;

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
