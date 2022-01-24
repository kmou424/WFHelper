package com.kmou424.WorldFlipper.Helper.Handler;

import android.os.Message;

public class HandlerMsg<T> {
    private final static String LOG_TAG = "HandlerMsg";

    // Action 1-100
    public final static int MOVE_TASK_TO_BACK = 1;

    // PUSH 101-200
    public final static int PUSH_TESS_OCR = 101;

    // UI 201-300
    public final static int SHOW_PROGRESS_DIALOG = 201;
    public final static int HIDE_PROGRESS_DIALOG = -SHOW_PROGRESS_DIALOG;

    /*
    * Convert obj to handler message
    * <T>: Type
    * obj: Message.obj
    * what: Message.what
    */
    public Message makeMessage(T obj, int what) {
        Message mMessage = new Message();
        mMessage.obj = obj;
        mMessage.what = what;
        return mMessage;
    }
}
