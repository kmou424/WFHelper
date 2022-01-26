package moe.kmou424.WorldFlipper.Helper.HandlerMsg;

import android.os.Message;

public class HandlerMessage<T> {
    // Action 1-100
    public final static int MOVE_TASK_TO_BACK = 1;
    public final static int SHOW_TOAST = 2;

    // PUSH 101-200
    public final static int PUSH_TESS_OCR = 101;

    // UI 201-300
    public final static int SHOW_PROGRESS_DIALOG = 201;
    public final static int HIDE_PROGRESS_DIALOG = -SHOW_PROGRESS_DIALOG;
    public final static int SHOW_FLOATING_WINDOW = 202;
    public final static int HIDE_FLOATING_WINDOW = -SHOW_FLOATING_WINDOW;

    /*
    * Convert obj to handler message
    * <T>: Type
    * obj: Message.obj
    * what: Message.what
    */
    public Message make(T obj, int what) {
        Message mMessage = new Message();
        mMessage.obj = obj;
        mMessage.what = what;
        return mMessage;
    }
}
