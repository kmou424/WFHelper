package moe.kmou424.WorldFlipper.Helper.Widget;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

public class WFPanel {
    private final static String LOG_TAG = "WFPanel";

    private final AlertDialog mAlertDialog;

    public WFPanel(Context mContext) {
        Log.d(LOG_TAG, "Initial");
        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(mContext);
//        NestedScrollView mScrollView;
//        LinearLayout.LayoutParams mRootLayoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        LinearLayout mRootLayout = new LinearLayout(mContext);
//        mRootLayout.setOrientation(LinearLayout.VERTICAL);
//        mAlertDialogBuilder.setView();
        mAlertDialogBuilder.setMessage("test");
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    public void show() {
        mAlertDialog.show();
    }

    public void hide() {
        mAlertDialog.dismiss();
    }
}
