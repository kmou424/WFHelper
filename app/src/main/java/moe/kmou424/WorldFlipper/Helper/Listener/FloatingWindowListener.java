package moe.kmou424.WorldFlipper.Helper.Listener;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import moe.kmou424.WorldFlipper.Helper.Constants.CoordinatePoints;
import moe.kmou424.WorldFlipper.Helper.Constants.Global;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.Action.ToastHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage;
import moe.kmou424.WorldFlipper.Helper.MainActivity;
import moe.kmou424.WorldFlipper.Helper.R;
import moe.kmou424.WorldFlipper.Helper.Tools.RootUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;
import moe.kmou424.WorldFlipper.Helper.Tools.TestUtils;
import moe.kmou424.WorldFlipper.Helper.Widget.FloatingWindow;

public class FloatingWindowListener {
    private final FloatingWindow mFloatingWindow;
    private final Handler mHandler;

    public FloatingWindowListener(FloatingWindow mFloatingWindow) {
        this.mFloatingWindow = mFloatingWindow;
        this.mHandler = MainActivity.mHandler;
    }

    public void bind() {
        bindFloatingMainButton();
        bindFloatingSubButton();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void bindFloatingMainButton() {
        ImageView mFloatingMainButton = mFloatingWindow.mRootView
                .findViewById(R.id.floatingMainButton);
        mFloatingMainButton.setOnTouchListener(new FloatingOnTouchListener(mFloatingWindow));
        mFloatingMainButton.setOnClickListener(view -> {
            LinearLayout mSubView = mFloatingWindow.mRootView.findViewById(R.id.floatingSubView);
            if (mSubView.getVisibility() != View.VISIBLE) {
                mSubView.setVisibility(View.VISIBLE);
            } else {
                mSubView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void bindFloatingSubButton() {
        mFloatingWindow.mRootView.findViewById(R.id.floatingSubButton1)
                .setOnClickListener(view -> mHandler.sendMessage(new HandlerMessage<>().make(
                        new ToastHandlerMsg("Screenshot was save at " + RootUtils.takeScreenShot(), ToastHandlerMsg.LENGTH_LONG),
                        HandlerMessage.SHOW_TOAST)
                ));
        mFloatingWindow.mRootView.findViewById(R.id.floatingSubButton2)
                .setOnClickListener(view -> MainActivity.mHandler.sendEmptyMessage(HandlerMessage.SHOW_WF_PANEL));
        mFloatingWindow.mRootView.findViewById(R.id.floatingSubButton3)
                .setOnClickListener(view -> {
                    mHandler.sendEmptyMessage(HandlerMessage.STOP_TRACKER_SERVICE);
                    mHandler.sendEmptyMessage(HandlerMessage.HIDE_FLOATING_WINDOW);
                });
    }
}

class FloatingOnTouchListener implements View.OnTouchListener {
    private int x, y;
    private final int x_left, x_right;
    private int mDiffX = 0, mDiffY = 0;

    private final WindowManager mWindowManager;
    private final WindowManager.LayoutParams mLayoutParams;
    private final View mView;

    public FloatingOnTouchListener(FloatingWindow mFloatingWindow) {
        this.mWindowManager = mFloatingWindow.mWindowManager;
        this.mLayoutParams = mFloatingWindow.mLayoutParams;
        this.mView = mFloatingWindow.mRootView;
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
                mDiffX = 0;
                mDiffY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) event.getRawX();
                int nowY = (int) event.getRawY();
                int movedX = nowX - x;
                int movedY = nowY - y;
                x = nowX;
                y = nowY;
                mDiffX += Math.abs(movedX);
                mDiffY += Math.abs(movedY);
                mLayoutParams.x = mLayoutParams.x + movedX;
                mLayoutParams.y = mLayoutParams.y + movedY;
                mWindowManager.updateViewLayout(mView, mLayoutParams);
                break;
            case MotionEvent.ACTION_UP:
                mLayoutParams.x = (x >= mWindowManager.getDefaultDisplay().getWidth() / 2 ? x_right : x_left);
                x = mLayoutParams.x;
                mWindowManager.updateViewLayout(mView, mLayoutParams);
                if (mDiffX + mDiffY > Global.MOVE_FLOATING_WINDOW_SENSITIVITY) return true;
            default:
                break;
        }
        return false;
    }
}