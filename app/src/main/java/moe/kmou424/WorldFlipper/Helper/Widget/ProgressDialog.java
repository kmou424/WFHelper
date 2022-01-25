package moe.kmou424.WorldFlipper.Helper.Widget;

import android.content.Context;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.ProgressDialogHandlerMsg;

public class ProgressDialog {
    private final static String LOG_TAG = "ProgressDialog";

    private android.app.ProgressDialog mProgressDialog;

    public ProgressDialog(Context mContext) {
        mProgressDialog = new android.app.ProgressDialog(mContext);
    }

    public void setAttribute(ProgressDialogHandlerMsg mProgressDialogHandlerMsg) {
        mProgressDialog.setTitle(mProgressDialogHandlerMsg.mTitle);
        mProgressDialog.setMessage(mProgressDialogHandlerMsg.mMessage);
        mProgressDialog.setMax(mProgressDialogHandlerMsg.maxProgress);
        mProgressDialog.setProgress(mProgressDialogHandlerMsg.mProgress);
        mProgressDialog.setProgressStyle(mProgressDialogHandlerMsg.mProgressStyle);
        mProgressDialog.setCancelable(mProgressDialogHandlerMsg.mCancelable);
    }

    public void show() {
        mProgressDialog.show();
    }

    public void hide() {
        mProgressDialog.dismiss();
    }
}
