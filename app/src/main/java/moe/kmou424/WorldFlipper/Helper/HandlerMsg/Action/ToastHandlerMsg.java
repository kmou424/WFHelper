package moe.kmou424.WorldFlipper.Helper.HandlerMsg.Action;

import android.widget.Toast;

public class ToastHandlerMsg {
    public final static int LENGTH_LONG = Toast.LENGTH_LONG;
    public final static int LENGTH_SHORT = Toast.LENGTH_SHORT;

    private final String mToastMessage;
    public final int mDuration;

    public ToastHandlerMsg(String mToastMessage, int mDuration) {
        this.mToastMessage = mToastMessage;
        this.mDuration = mDuration;
    }

    public String getToastMessage() {
        return this.mToastMessage;
    }
}
