package moe.kmou424.WorldFlipper.Helper.Widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.switchmaterial.SwitchMaterial;

import moe.kmou424.WorldFlipper.Helper.Constants.SharedPreferencesConfigs;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage;
import moe.kmou424.WorldFlipper.Helper.MainActivity;
import moe.kmou424.WorldFlipper.Helper.R;

public class WFPanel {
    private final static String LOG_TAG = "WFPanel";

    private final Context mContext;
    public static AlertDialog mAlertDialog;

    public WFPanel(Context mContext) {
        Log.d(LOG_TAG, "Initial");
        this.mContext = mContext;

        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(mContext);
        mAlertDialogBuilder.setCancelable(false);
        // SubView0: Top notice
        MaterialCardView mSubView0 = getSubView0();
        // SubView1: Settings at prepare
        MaterialCardView mSubView1 = getSubView1();
        // SubView2: WorldFlipper Bell Tracker View
        MaterialCardView mSubView2 = getSubView2();

        // Root:
        LinearLayout.LayoutParams mRootLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout mRootLayout = new LinearLayout(mContext);
        mRootLayout.setLayoutParams(mRootLayoutParams);
        mRootLayout.setOrientation(LinearLayout.VERTICAL);

        // Add SubViews to Root
        mRootLayout.addView(mSubView0);
        mRootLayout.addView(mSubView1);
        mRootLayout.addView(mSubView2);

        // Parent: for scroll
        FrameLayout.LayoutParams mScrollViewParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        NestedScrollView mScrollView = new NestedScrollView(mContext);
        mScrollView.setLayoutParams(mScrollViewParams);
        mScrollView.addView(mRootLayout);
        mAlertDialogBuilder.setView(mScrollView);
        mAlertDialogBuilder.setPositiveButton(R.string.start_tracker, new StartTrackerListener());
        mAlertDialogBuilder.setNegativeButton(R.string.stop_tracker, new StopTrackerListener());
        mAlertDialogBuilder.setNeutralButton(R.string.cancel, null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mAlertDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Private: Get a custom view
    private MaterialCardView getSubView0() {
        // Widget1: NoticeHeader
        TextView mNoticeHeader = new TextView(mContext);
        mNoticeHeader.setText(R.string.wf_notice_header);
        mNoticeHeader.setTextSize(21);
        mNoticeHeader.setTextColor(Color.RED);
        mNoticeHeader.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mNoticeHeader.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView mNoticeContent = new TextView(mContext);
        mNoticeContent.setText(String.format("%s", mContext.getString(R.string.wf_notice_content)));

        // SubView: Root -> Sub
        LinearLayout mSubViewChildView = getLinearLayoutContainer(20, 12, LinearLayout.VERTICAL);
        mSubViewChildView.addView(mNoticeHeader);
        mSubViewChildView.addView(mNoticeContent);

        // SubView: Root View
        MaterialCardView mSubView = getMaterialCardContainer();
        mSubView.addView(mSubViewChildView);

        return mSubView;
    }

    // Private: Get a custom view
    private MaterialCardView getSubView1() {
        // Sub1: Widget1: Title
        TextView mSub1Title = new TextView(mContext);
        mSub1Title.setText(R.string.wf_boss_level_title);
        mSub1Title.setTextSize(18);
        mSub1Title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mSub1Title.setGravity(Gravity.CENTER_HORIZONTAL);
        // Sub1: Widget2: Boss level selection: primary
        MaterialCheckBox mBossLevelPrimaryCheckBox = getMaterialCheckBox(R.string.wf_boss_level_primary, R.id.wf_boss_level_primary);
        mBossLevelPrimaryCheckBox.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.BOSS_LEVEL_PRIMARY, true));
        // Sub1: Widget3: Boss level selection: middle
        MaterialCheckBox mBossLevelMiddleCheckBox = getMaterialCheckBox(R.string.wf_boss_level_middle, R.id.wf_boss_level_middle);
        mBossLevelMiddleCheckBox.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.BOSS_LEVEL_MIDDLE, true));
        // Sub1: Widget4: Boss level selection: high
        MaterialCheckBox mBossLevelHighCheckBox = getMaterialCheckBox(R.string.wf_boss_level_high, R.id.wf_boss_level_high);
        mBossLevelHighCheckBox.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.BOSS_LEVEL_HIGH, true));
        // Sub1: Widget5: Boss level selection: high plus
        MaterialCheckBox mBossLevelHighPlusCheckBox = getMaterialCheckBox(R.string.wf_boss_level_high_plus, R.id.wf_boss_level_high_plus);
        mBossLevelHighPlusCheckBox.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.BOSS_LEVEL_HIGH_PLUS, true));
        // Sub1: Widget6: Boss level selection: super
        MaterialCheckBox mBossLevelSuperCheckBox = getMaterialCheckBox(R.string.wf_boss_level_super, R.id.wf_boss_level_super);
        mBossLevelSuperCheckBox.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.BOSS_LEVEL_SUPER, true));

        // SubView: Root -> Sub -> Sub1
        LinearLayout mSubViewChildView1 = getLinearLayoutContainer(0, 6, LinearLayout.VERTICAL);
        mSubViewChildView1.setBackgroundResource(R.drawable.corner_background);
        mSubViewChildView1.addView(mSub1Title);
        mSubViewChildView1.addView(mBossLevelPrimaryCheckBox);
        mSubViewChildView1.addView(mBossLevelMiddleCheckBox);
        mSubViewChildView1.addView(mBossLevelHighCheckBox);
        mSubViewChildView1.addView(mBossLevelHighPlusCheckBox);
        mSubViewChildView1.addView(mBossLevelSuperCheckBox);

        // Sub2: Widget1: Wait for others ready
        MaterialCheckBox mWaitOthersReadyCheckBox = getMaterialCheckBox(R.string.wf_wait_for_others_ready_checkbox_text, R.id.wf_wait_others_ready_checkbox);
        mWaitOthersReadyCheckBox.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.WAIT_OTHERS_READY_CHECKBOX, false));

        // SubView: Root -> Sub -> Sub2
        LinearLayout mSubViewChildView2 = getLinearLayoutContainer(0, 6, LinearLayout.VERTICAL);
        mSubViewChildView2.setBackgroundResource(R.drawable.corner_background);
        mSubViewChildView2.addView(mWaitOthersReadyCheckBox);

        //Root -> Sub
        LinearLayout mSubViewChildRoot = getLinearLayoutContainer(20, 12, LinearLayout.VERTICAL);
        mSubViewChildRoot.addView(mSubViewChildView1);
        mSubViewChildRoot.addView(mSubViewChildView2);

        // SubView: Root View
        MaterialCardView mSubView = getMaterialCardContainer();
        mSubView.addView(mSubViewChildRoot);

        return mSubView;
    }

    // Private: Get a custom view
    private MaterialCardView getSubView2() {
        // Widget1: Bell Tracker Switch
        SwitchMaterial mBellTrackerSwitch = new SwitchMaterial(mContext);
        mBellTrackerSwitch.setText(R.string.wf_bell_tracker_switch_text);
        mBellTrackerSwitch.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.BELL_TRACKER_SWITCH, false));
        mBellTrackerSwitch.setId(R.id.wf_bell_tracker_switch);

        // SubView: Root -> Sub
        LinearLayout mSubViewChildView = getLinearLayoutContainer(20, 12, LinearLayout.VERTICAL);
        mSubViewChildView.addView(mBellTrackerSwitch);

        // SubView: Root View
        MaterialCardView mSubView = getMaterialCardContainer();
        mSubView.addView(mSubViewChildView);

        return mSubView;
    }

    private MaterialCheckBox getMaterialCheckBox(@StringRes int mText, @IdRes int mId) {
        MaterialCheckBox mMaterialCheckBox = new MaterialCheckBox(mContext);
        mMaterialCheckBox.setText(mText);
        mMaterialCheckBox.setId(mId);
        return mMaterialCheckBox;
    }

    // Common get a container: LinearLayout
    private LinearLayout getLinearLayoutContainer(int leftAndRightMargin, int topAndBottomMargin, int mOrientation) {
        LinearLayout.LayoutParams mLinearLayoutContainerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        mLinearLayoutContainerParams.leftMargin = leftAndRightMargin;
        mLinearLayoutContainerParams.rightMargin = leftAndRightMargin;
        mLinearLayoutContainerParams.topMargin = topAndBottomMargin;
        mLinearLayoutContainerParams.bottomMargin = topAndBottomMargin;
        LinearLayout mLinearLayoutContainer = new LinearLayout(mContext);
        mLinearLayoutContainer.setLayoutParams(mLinearLayoutContainerParams);
        mLinearLayoutContainer.setOrientation(mOrientation);
        return mLinearLayoutContainer;
    }

    // Common get a container: Card
    private MaterialCardView getMaterialCardContainer() {
        MaterialCardView mMaterialCardView = new MaterialCardView(mContext);
        FrameLayout.LayoutParams mMaterialCardViewParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        );
        mMaterialCardViewParams.leftMargin = 30;
        mMaterialCardViewParams.rightMargin = 30;
        mMaterialCardViewParams.topMargin = 15;
        mMaterialCardViewParams.bottomMargin = 15;
        mMaterialCardView.setLayoutParams(mMaterialCardViewParams);
        return mMaterialCardView;
    }

    public void show() {
        mAlertDialog.show();
    }

    public void hide() {
        mAlertDialog.dismiss();
    }
}

class StartTrackerListener implements DialogInterface.OnClickListener {
    @SuppressLint("CommitPrefEdits")
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        // Bell Tracker
        MainActivity.mSharedPreferences.edit()
                .putBoolean(SharedPreferencesConfigs.BOSS_LEVEL_PRIMARY, ((MaterialCheckBox)WFPanel.mAlertDialog.findViewById(R.id.wf_boss_level_primary)).isChecked())
                .putBoolean(SharedPreferencesConfigs.BOSS_LEVEL_MIDDLE, ((MaterialCheckBox)WFPanel.mAlertDialog.findViewById(R.id.wf_boss_level_middle)).isChecked())
                .putBoolean(SharedPreferencesConfigs.BOSS_LEVEL_HIGH, ((MaterialCheckBox)WFPanel.mAlertDialog.findViewById(R.id.wf_boss_level_high)).isChecked())
                .putBoolean(SharedPreferencesConfigs.BOSS_LEVEL_HIGH_PLUS, ((MaterialCheckBox)WFPanel.mAlertDialog.findViewById(R.id.wf_boss_level_high_plus)).isChecked())
                .putBoolean(SharedPreferencesConfigs.BOSS_LEVEL_SUPER, ((MaterialCheckBox)WFPanel.mAlertDialog.findViewById(R.id.wf_boss_level_super)).isChecked())
                .putBoolean(SharedPreferencesConfigs.WAIT_OTHERS_READY_CHECKBOX, ((MaterialCheckBox)WFPanel.mAlertDialog.findViewById(R.id.wf_wait_others_ready_checkbox)).isChecked())
                .putBoolean(SharedPreferencesConfigs.BELL_TRACKER_SWITCH, ((SwitchMaterial)WFPanel.mAlertDialog.findViewById(R.id.wf_bell_tracker_switch)).isChecked())
        .apply();
        MainActivity.mHandler.sendEmptyMessage(HandlerMessage.START_TRACKER_SERVICE);
    }
}

class StopTrackerListener implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        MainActivity.mHandler.sendEmptyMessage(HandlerMessage.STOP_TRACKER_SERVICE);
    }
}