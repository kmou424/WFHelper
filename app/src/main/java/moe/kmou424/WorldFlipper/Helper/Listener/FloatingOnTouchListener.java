package moe.kmou424.WorldFlipper.Helper.Listener;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.FloatingWindowHandlerMsg;

public class FloatingOnTouchListener implements View.OnTouchListener {
    private final static String LOG_TAG = "FloatingOnTouchListener";

    private int x, y;
    private final int x_left;
    private final int x_right;

    private final WindowManager mWindowManager;
    private final WindowManager.LayoutParams mLayoutParams;
    private final View mView;

    public FloatingOnTouchListener(FloatingWindowHandlerMsg mFloatingWindowHandlerMsg) {
        this.mWindowManager = mFloatingWindowHandlerMsg.mWindowManager;
        this.mLayoutParams = mFloatingWindowHandlerMsg.mLayoutParams;
        this.mView = mFloatingWindowHandlerMsg.mView;
        x_left = 0;
        x_right = mWindowManager.getDefaultDisplay().getWidth() - mLayoutParams.width;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) event.getRawX();
                int nowY = (int) event.getRawY();
                int movedX = nowX - x;
                int movedY = nowY - y;
                x = nowX;
                y = nowY;
                mLayoutParams.x = mLayoutParams.x + movedX;
                mLayoutParams.y = mLayoutParams.y + movedY;
                mWindowManager.updateViewLayout(view, mLayoutParams);
                break;
            case MotionEvent.ACTION_UP:
                mLayoutParams.x = (x >= mWindowManager.getDefaultDisplay().getWidth() / 2 ? x_right : x_left);
                x = mLayoutParams.x;
                mWindowManager.updateViewLayout(view, mLayoutParams);
            default:
                break;
        }
        return false;
    }
}
