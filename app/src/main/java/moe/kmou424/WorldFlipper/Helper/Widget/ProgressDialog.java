package moe.kmou424.WorldFlipper.Helper.Widget;

import android.content.Context;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.UI.ProgressDialogHandlerMsg;

public class ProgressDialog {
    private final static String LOG_TAG = "ProgressDialog";

    private final android.app.ProgressDialog mProgressDialog;

    public ProgressDialog(Context mContext) {
        mProgressDialog = new android.app.ProgressDialog(mContext);
    }

    public ProgressDialog setAttribute(ProgressDialogHandlerMsg mProgressDialogHandlerMsg) {
        mProgressDialog.setTitle(mProgressDialogHandlerMsg.mTitle);
        mProgressDialog.setMessage(mProgressDialogHandlerMsg.mMessage);
        mProgressDialog.setMax(mProgressDialogHandlerMsg.maxProgress);
        mProgressDialog.setProgress(mProgressDialogHandlerMsg.mProgress);
        mProgressDialog.setProgressStyle(mProgressDialogHandlerMsg.mProgressStyle);
        mProgressDialog.setCancelable(mProgressDialogHandlerMsg.mCancelable);
        return this;
    }

    public void show() {
        mProgressDialog.show();
    }

    public void hide() {
        mProgressDialog.dismiss();
    }
}
