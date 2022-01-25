package moe.kmou424.WorldFlipper.Helper.Task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.FloatingWindowHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMsg;
import moe.kmou424.WorldFlipper.Helper.R;
import moe.kmou424.WorldFlipper.Helper.Thread.WFThread;

public class WidgetTask {
    private final static String LOG_TAG = "WidgetTask";
    private final Handler mHandler;

    protected final Context mContext;

    public WidgetTask(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    /*
     * Get a thread to generate a floating window and push to MainActivity
     * return a WFTread, it can run anywhere
     */
    public WFThread getFloatingWindow(Thread mLastThread, Activity mActivity) {
        WFThread ret = new WFThread(this.mHandler, mLastThread) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                super.run();
                WindowManager windowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                layoutParams.format = PixelFormat.RGBA_8888;
                layoutParams.gravity = Gravity.START | Gravity.TOP;
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                layoutParams.width = 100;
                layoutParams.height = 100;
                layoutParams.x = 0;
                layoutParams.y = 300;
                ImageView imageView = new ImageView(mActivity.getApplicationContext());
                imageView.setImageDrawable(mActivity.getDrawable(R.mipmap.ic_launcher));
                imageView.setClickable(true);
                mHandler.sendMessage(
                        new HandlerMsg<FloatingWindowHandlerMsg>()
                                .makeMessage(new FloatingWindowHandlerMsg(windowManager, layoutParams, imageView),
                                        HandlerMsg.SHOW_FLOATING_WINDOW
                                ));
            }
        };
        ret.setName(new Exception().getStackTrace()[0].getMethodName());
        return ret;
    }
}
