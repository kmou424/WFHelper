package moe.kmou424.WorldFlipper.Helper.Widget;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.content.res.AppCompatResources;

import moe.kmou424.WorldFlipper.Helper.R;

public class FloatingWindow {
    private final static String LOG_TAG = "FloatingWindow";

    public WindowManager mWindowManager;
    public WindowManager.LayoutParams mLayoutParams;
    public LinearLayout mRootView;

    public FloatingWindow(Activity mActivity) {
        this.mWindowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.x = 0;
        mLayoutParams.y = 300;

        // Left Widgets: Main button
        ImageView mMainButtonImageView = new ImageView(mActivity.getApplicationContext());
        mMainButtonImageView.setImageDrawable(AppCompatResources.getDrawable((Context) mActivity, R.mipmap.ic_launcher));
        mMainButtonImageView.setClickable(true);
        mMainButtonImageView.setId(R.id.floatingMainButton);

        // Left View: MainView
        FrameLayout.LayoutParams mMainViewParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        );
        FrameLayout mMainView = new FrameLayout((Context) mActivity);
        mMainView.setId(R.id.floatingMainView);
        mMainView.setLayoutParams(mMainViewParams);
        mMainView.addView(mMainButtonImageView);

        // Right Widgets: Sub buttons
        // Button1
        ImageView mSubButtonImageView1 = new ImageView(mActivity.getApplicationContext());
        mSubButtonImageView1.setImageDrawable(AppCompatResources.getDrawable((Context) mActivity, R.mipmap.ic_screenshot));
        mSubButtonImageView1.setClickable(true);
        mSubButtonImageView1.setId(R.id.floatingSubButton1);
        // Button2
        ImageView mSubButtonImageView2 = new ImageView(mActivity.getApplicationContext());
        mSubButtonImageView2.setImageDrawable(AppCompatResources.getDrawable((Context) mActivity, R.mipmap.ic_settings));
        mSubButtonImageView2.setClickable(true);
        mSubButtonImageView2.setId(R.id.floatingSubButton2);
        // Button3
        ImageView mSubButtonImageView3 = new ImageView(mActivity.getApplicationContext());
        mSubButtonImageView3.setImageDrawable(AppCompatResources.getDrawable((Context) mActivity, R.mipmap.ic_quit));
        mSubButtonImageView3.setClickable(true);
        mSubButtonImageView3.setId(R.id.floatingSubButton3);

        // Left View: SubView
        LinearLayout.LayoutParams mSubViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout mSubView = new LinearLayout((Context) mActivity);
        mSubView.setId(R.id.floatingSubView);
        mSubView.setLayoutParams(mSubViewParams);
        mSubView.setOrientation(LinearLayout.VERTICAL);
        mSubView.addView(mSubButtonImageView1);
        mSubView.addView(mSubButtonImageView2);
        mSubView.addView(mSubButtonImageView3);

        mSubView.setVisibility(View.GONE);

        // Parent View
        LinearLayout.LayoutParams mRootViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        this.mRootView = new LinearLayout((Context) mActivity);
        LayoutTransition mRootViewAnim = new LayoutTransition();
        mRootViewAnim.addTransitionListener(new HideAfterTransitionListener());
        mRootView.setLayoutTransition(mRootViewAnim);
        mRootView.setClickable(false);
        mRootView.setLayoutParams(mRootViewParams);
        mRootView.setOrientation(LinearLayout.VERTICAL);
        mRootView.addView(mMainView, 0);
        mRootView.addView(mSubView, 1);
    }

    public void show() {
        mWindowManager.addView(mRootView, mLayoutParams);
    }

    public void hide() {
        mWindowManager.removeView(mRootView);
    }
}

class HideAfterTransitionListener implements LayoutTransition.TransitionListener {
    @Override
    public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {}

    @Override
    public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
        if (view.getVisibility() == View.INVISIBLE) view.setVisibility(View.GONE);
    }
}
