package moe.kmou424.WorldFlipper.Helper.HandlerMsg;

import android.view.View;
import android.view.WindowManager;

public class FloatingWindowHandlerMsg {
    private final static String LOG_TAG = "FloatingWindowHandlerMsg";

    public WindowManager mWindowManager;
    public WindowManager.LayoutParams mLayoutParams;
    public View mView;

    public FloatingWindowHandlerMsg(WindowManager mWindowManager, WindowManager.LayoutParams mLayoutParams, View mView) {
        this.mWindowManager = mWindowManager;
        this.mLayoutParams = mLayoutParams;
        this.mView = mView;
    }
}
