package moe.kmou424.WorldFlipper.Helper.HandlerMsg;

import android.os.Message;

public class HandlerMessage<T> {
    // Action 1-100
    public final static int MOVE_TASK_TO_BACK = 1;
    public final static int SHOW_TOAST = 2;
    public final static int SHOW_FLOATING_WINDOW = 3;
    public final static int HIDE_FLOATING_WINDOW = -SHOW_FLOATING_WINDOW;
    public final static int START_TRACKER_SERVICE = 4;
    public final static int STOP_TRACKER_SERVICE = -START_TRACKER_SERVICE;

    // PUSH 101-200

    // UI 201-300
    public final static int SHOW_PROGRESS_DIALOG = 201;
    public final static int HIDE_PROGRESS_DIALOG = -SHOW_PROGRESS_DIALOG;
    public final static int SHOW_WF_PANEL = 202;
    public final static int HIDE_WF_PANEL = -SHOW_WF_PANEL;

    // Tracker Action 301+
    public final static int GO_RE_LOGIN = 301;
    public final static int GO_RE_LOGIN_DELAY = 302;
    public final static int GO_CHECK_BITMAP = 303;
    public final static int GO_BELL = 304;
    public final static int GO_PREPARE_AS_GUEST = 305;
    public final static int GO_WAITING_FOR_FINISH = 306;
    public final static int GO_MAIN_PAGE = 307;

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
