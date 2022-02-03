package moe.kmou424.WorldFlipper.Helper.Widget;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import moe.kmou424.WorldFlipper.Helper.Constants.CheckPoints;
import moe.kmou424.WorldFlipper.Helper.Constants.Global;
import moe.kmou424.WorldFlipper.Helper.Constants.SharedPreferencesConfigs;
import moe.kmou424.WorldFlipper.Helper.Constants.Tags;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage;
import moe.kmou424.WorldFlipper.Helper.Info.BossInfoWrapper;
import moe.kmou424.WorldFlipper.Helper.MainActivity;
import moe.kmou424.WorldFlipper.Helper.R;

public class WFPanel {
    private final static String LOG_TAG = "WFPanel";

    private final Context mContext;
    public Handler mHandler;
    public static AlertDialog mAlertDialog;

    public WFPanel(Context mContext) {
        Log.d(LOG_TAG, "Initial");
        this.mContext = mContext;
        this.mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };

        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(mContext);
        mAlertDialogBuilder.setCancelable(false);
        // SubView0: Top notice
        MaterialCardView mSubView0 = getSubView0();
        // SubView1: WorldFlipper Bell Tracker View
        MaterialCardView mSubView1 = getSubView1();
        // SubView2: Boss settings
        MaterialCardView mSubView2 = getSubView2();
        // SubView3: Common settings
        MaterialCardView mSubView3 = getSubView3();

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
        mRootLayout.addView(mSubView3);

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
        LinearLayout mSubViewChildView = ViewGenerator.getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                20, 12, LinearLayout.VERTICAL, mContext);
        mSubViewChildView.addView(mNoticeHeader);
        mSubViewChildView.addView(mNoticeContent);

        // SubView: Root View
        MaterialCardView mSubView = ViewGenerator.getMaterialCardContainer(mContext);
        mSubView.addView(mSubViewChildView);

        return mSubView;
    }

    // Private: Get a custom view
    private MaterialCardView getSubView1() {
        // Widget1: Bell Tracker Switch
        SwitchMaterial mBellTrackerSwitch = new SwitchMaterial(mContext);
        mBellTrackerSwitch.setText(R.string.wf_bell_tracker_switch_text);
        mBellTrackerSwitch.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.BELL_TRACKER_SWITCH, false));
        mBellTrackerSwitch.setId(R.id.wf_bell_tracker_switch);

        // SubView: Root -> Sub
        LinearLayout mSubViewChildView = ViewGenerator.getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                20, 12, LinearLayout.VERTICAL, mContext);
        mSubViewChildView.addView(mBellTrackerSwitch);

        // SubView: Root View
        MaterialCardView mSubView = ViewGenerator.getMaterialCardContainer(mContext);
        mSubView.addView(mSubViewChildView);

        return mSubView;
    }

    // Private: Get a custom view
    private MaterialCardView getSubView2() {
        LinearLayout mBossFilterManager = ViewGenerator.getBossFilterManager(mContext);
        mBossFilterManager.setId(R.id.wf_boss_filter_layout_root);

        // SubView: Root -> Sub -> Sub1
        LinearLayout mSubViewChildView = ViewGenerator.getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                0, 0, LinearLayout.VERTICAL, mContext
        );
        LinearLayout mSubViewChildViewTitle = ViewGenerator.getFeaturesTitle(R.string.wf_boss_level_filter_title, mContext);
        mSubViewChildView.addView(mSubViewChildViewTitle);
        mSubViewChildView.addView(mBossFilterManager);

        //Root -> Sub
        LinearLayout mSubViewChildRoot = ViewGenerator.getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                12, 12, LinearLayout.VERTICAL, mContext);
        mSubViewChildRoot.addView(mSubViewChildView);

        // SubView: Root View
        MaterialCardView mSubView = ViewGenerator.getMaterialCardContainer(mContext);
        mSubView.addView(mSubViewChildRoot);

        return mSubView;
    }

    // Private: Get a custom view
    private MaterialCardView getSubView3() {
        // Sub1: Widget1: Wait for others ready
        MaterialCheckBox mWaitOthersReadyCheckBox = ViewGenerator.getMaterialCheckBox(R.string.wf_wait_for_others_ready_checkbox_text, mContext);
        mWaitOthersReadyCheckBox.setId(R.id.wf_wait_others_ready_checkbox);
        mWaitOthersReadyCheckBox.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.WAIT_OTHERS_READY_CHECKBOX, false));
        // Sub1: Widget2: Re-login delay
        MaterialCheckBox mReLoginDelayCheckBox = ViewGenerator.getMaterialCheckBox(R.string.wf_re_login_delay_checkbox, mContext);
        mReLoginDelayCheckBox.setId(R.id.wf_re_login_delay_checkbox);
        mReLoginDelayCheckBox.setChecked(MainActivity.mSharedPreferences.getBoolean(SharedPreferencesConfigs.RE_LOGIN_DELAY_ENABLED, false));
        TextInputEditText mReLoginDelayEditText = new TextInputEditText(mContext);
        mReLoginDelayEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mReLoginDelayEditText.setText(String.valueOf(MainActivity.mSharedPreferences.getInt(SharedPreferencesConfigs.RE_LOGIN_DELAY_VALUE, 0)));
        mReLoginDelayEditText.setId(R.id.wf_re_login_delay_edittext);
        // Sub1: Widget2: Container
        LinearLayout mReLoginDelayContainer = ViewGenerator.getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                0, 0, LinearLayout.HORIZONTAL, mContext
        );
        mReLoginDelayContainer.addView(mReLoginDelayCheckBox);
        mReLoginDelayContainer.addView(mReLoginDelayEditText);

        // SubView: Root -> Sub -> Sub1
        LinearLayout mSubViewChildView = ViewGenerator.getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                0, 0, LinearLayout.VERTICAL, mContext
        );
        LinearLayout mSubViewChildViewTitle = ViewGenerator.getFeaturesTitle(R.string.wf_features_common_title, mContext);
        LinearLayout mSubViewChildViewContent = ViewGenerator.getFeaturesArea(mContext);
        mSubViewChildView.addView(mSubViewChildViewTitle);
        mSubViewChildViewContent.addView(mWaitOthersReadyCheckBox);
        mSubViewChildViewContent.addView(mReLoginDelayContainer);
        mSubViewChildView.addView(mSubViewChildViewContent);

        //Root -> Sub
        LinearLayout mSubViewChildRoot = ViewGenerator.getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                12, 12, LinearLayout.VERTICAL, mContext);
        mSubViewChildRoot.addView(mSubViewChildView);

        // SubView: Root View
        MaterialCardView mSubView = ViewGenerator.getMaterialCardContainer(mContext);
        mSubView.addView(mSubViewChildRoot);

        return mSubView;
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
        // EditText
        int mReLoginDelayEditTextNum = 0;
        if (((MaterialCheckBox)WFPanel.mAlertDialog.findViewById(R.id.wf_re_login_delay_checkbox)).isChecked()) {
            Editable mReLoginDelayEditable = ((TextInputEditText) WFPanel.mAlertDialog.findViewById(R.id.wf_re_login_delay_edittext)).getText();
            if (mReLoginDelayEditable != null) {
                String mReLoginDelayEditTextString = mReLoginDelayEditable.toString();
                if (!mReLoginDelayEditTextString.equals("") && !mReLoginDelayEditTextString.isEmpty()) {
                    mReLoginDelayEditTextNum = Integer.parseInt(mReLoginDelayEditTextString);
                } else {
                    mReLoginDelayEditTextNum = 0;
                }
            } else {
                mReLoginDelayEditTextNum = 0;
            }
        }
        // Save boss info
        for (BossInfoWrapper mBossInfoWrapper : Global.BOSS_INFO_WRAPPER_LIST) {
            LinearLayout mRoot = WFPanel.mAlertDialog.findViewById(R.id.wf_boss_filter_layout_root).findViewWithTag(mBossInfoWrapper.BossTag);
            // 拿到目标队伍名(不符合A-J中的任意一个就设置为"null")
            String mBossTeam = "null";
            Editable mBossTeamEditable = ((TextInputEditText)mRoot.findViewWithTag(Tags.WF_BOSS_ITEM_TEAM_EDITTEXT)).getText();
            if (mBossTeamEditable != null) {
                mBossTeam = mBossTeamEditable.toString().toUpperCase();
                if (mBossTeam.equals("") || mBossTeam.isEmpty()) {
                    mBossTeam = "null";
                }
                boolean found = false;
                for (String mAvailableTeam : Global.AvailableTeam) {
                    if (mAvailableTeam.equals(mBossTeam)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    mBossTeam = "null";
                }
            }
            // 进行一个Boss等级集合的构建(用:分隔保存为一个字符串)
            String mBossLevels = "";
            if (((MaterialCheckBox)mRoot.findViewWithTag(Tags.WF_BOSS_LEVEL_PRIMARY_CHECKBOX)).isChecked())
                mBossLevels += CheckPoints.BOSS_LEVEL_PRIMARY + ":";
            if (((MaterialCheckBox)mRoot.findViewWithTag(Tags.WF_BOSS_LEVEL_MIDDLE_CHECKBOX)).isChecked())
                mBossLevels += CheckPoints.BOSS_LEVEL_MIDDLE + ":";
            if (((MaterialCheckBox)mRoot.findViewWithTag(Tags.WF_BOSS_LEVEL_HIGH_CHECKBOX)).isChecked())
                mBossLevels += CheckPoints.BOSS_LEVEL_HIGH + ":";
            if (((MaterialCheckBox)mRoot.findViewWithTag(Tags.WF_BOSS_LEVEL_HIGH_PLUS_CHECKBOX)).isChecked())
                mBossLevels += CheckPoints.BOSS_LEVEL_HIGH_PLUS + ":";
            if (((MaterialCheckBox)mRoot.findViewWithTag(Tags.WF_BOSS_LEVEL_SUPER_CHECKBOX)).isChecked())
                mBossLevels += CheckPoints.BOSS_LEVEL_SUPER + ":";
            MainActivity.mSharedPreferences.edit()
                    .putBoolean(mBossInfoWrapper.BossEnabledConf, ((SwitchMaterial)mRoot.findViewWithTag(Tags.WF_BOSS_ITEM_SWITCH)).isChecked())
                    .putString(mBossInfoWrapper.BossLevelsConf, mBossLevels)
                    .putString(mBossInfoWrapper.BossTeamConf, mBossTeam)
                    .apply();
        }
        // Bell Tracker
        MainActivity.mSharedPreferences.edit()
                .putBoolean(SharedPreferencesConfigs.RE_LOGIN_DELAY_ENABLED, ((MaterialCheckBox)WFPanel.mAlertDialog.findViewById(R.id.wf_re_login_delay_checkbox)).isChecked())
                .putInt(SharedPreferencesConfigs.RE_LOGIN_DELAY_VALUE, mReLoginDelayEditTextNum)
                .putBoolean(SharedPreferencesConfigs.WAIT_OTHERS_READY_CHECKBOX, ((MaterialCheckBox)WFPanel.mAlertDialog.findViewById(R.id.wf_wait_others_ready_checkbox)).isChecked())
                .putBoolean(SharedPreferencesConfigs.BELL_TRACKER_SWITCH, ((SwitchMaterial)WFPanel.mAlertDialog.findViewById(R.id.wf_bell_tracker_switch)).isChecked())
                .apply();
        MainActivity.mHandler.sendEmptyMessage(HandlerMessage.START_TRACKER_SERVICE);
    }
}

class ViewGenerator {
    public static LinearLayout getBossFilterManager(Context mContext) {
        LinearLayout mRootLayout = getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                0, 0, LinearLayout.VERTICAL, mContext
        );
        for (BossInfoWrapper i : Global.BOSS_INFO_WRAPPER_LIST) {
            if (i.mEndDateInfo != null) {
                if (!i.mEndDateInfo.isAvailable) continue;
            }
            mRootLayout.addView(getBossItem(i, mContext));
        }
        return mRootLayout;
    }

    @SuppressLint("SetTextI18n")
    private static LinearLayout getBossItem(BossInfoWrapper mBossInfoWrapper, Context mContext) {
        LinearLayout mRootLayout = getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                0, 6, LinearLayout.VERTICAL, mContext
        );
        LayoutTransition mAnim = new LayoutTransition();
        mAnim.addTransitionListener(new HideAfterTransitionListener());
        mRootLayout.setLayoutTransition(mAnim);
        mRootLayout.setBackgroundResource(R.drawable.corner_background);
        mRootLayout.setPadding(12, 6, 12, 10);
        mRootLayout.setTag(mBossInfoWrapper.BossTag);

        SwitchMaterial mTitleSwitch = getMaterialSwitch(mBossInfoWrapper.BossNameResId, mContext);
        mTitleSwitch.setChecked(true);
        mTitleSwitch.setTag(Tags.WF_BOSS_ITEM_SWITCH);

        TextView mBossEndTime = new TextView(mContext);
        String mEndTime = (mBossInfoWrapper.mEndDateInfo == null ? "长期有效" : mBossInfoWrapper.mEndDateInfo.time);
        mBossEndTime.setText("结束于 " + mEndTime);
        mBossEndTime.setTextSize(10);

        FrameLayout.LayoutParams mHorizontalScrollViewParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
        );
        HorizontalScrollView mHorizontalScrollView = new HorizontalScrollView(mContext);
        mHorizontalScrollView.setLayoutParams(mHorizontalScrollViewParams);

        LinearLayout mBossSelection1 = getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                0, 0, LinearLayout.HORIZONTAL, mContext
        );
        LinearLayout mBossSelection2 = getLinearLayoutContainer(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                0, 0, LinearLayout.HORIZONTAL, mContext
        );

        MaterialCheckBox mBossPrimary = getMaterialCheckBox(R.string.wf_boss_level_primary, mContext);
        mBossPrimary.setTag(Tags.WF_BOSS_LEVEL_PRIMARY_CHECKBOX);
        MaterialCheckBox mBossMiddle = getMaterialCheckBox(R.string.wf_boss_level_middle, mContext);
        mBossMiddle.setTag(Tags.WF_BOSS_LEVEL_MIDDLE_CHECKBOX);
        MaterialCheckBox mBossHigh = getMaterialCheckBox(R.string.wf_boss_level_high, mContext);
        mBossHigh.setTag(Tags.WF_BOSS_LEVEL_HIGH_CHECKBOX);
        MaterialCheckBox mBossHighPlus = getMaterialCheckBox(R.string.wf_boss_level_high_plus, mContext);
        mBossHighPlus.setTag(Tags.WF_BOSS_LEVEL_HIGH_PLUS_CHECKBOX);
        MaterialCheckBox mBossSuper = getMaterialCheckBox(R.string.wf_boss_level_super, mContext);
        mBossSuper.setTag(Tags.WF_BOSS_LEVEL_SUPER_CHECKBOX);

        TextView mBossTeamSummary = new TextView(mContext);
        mBossTeamSummary.setText(R.string.wf_boss_level_filter_team_summary);
        mBossTeamSummary.setTextSize(13);
        TextInputEditText mBossTeamEditText = new TextInputEditText(mContext);
        mBossTeamEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        // 从配置文件中获取指定队伍名，若为"null"则等价于empty()
        String mBossTeam = MainActivity.mSharedPreferences.getString(mBossInfoWrapper.BossTeamConf, "");
        mBossTeamEditText.setText((mBossTeam.equals("null") ? "" : mBossTeam));
        mBossTeamEditText.setTextSize(13);
        mBossTeamEditText.setHint("默认为保持当前队伍");
        mBossTeamEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        mBossTeamEditText.setTag(Tags.WF_BOSS_ITEM_TEAM_EDITTEXT);

        mBossSelection1.addView(mBossPrimary);
        mBossSelection1.addView(mBossMiddle);
        mBossSelection1.addView(mBossHigh);
        mBossSelection1.addView(mBossHighPlus);
        mBossSelection1.addView(mBossSuper);
        mBossSelection2.addView(mBossTeamSummary);
        mBossSelection2.addView(mBossTeamEditText);

        mHorizontalScrollView.addView(mBossSelection1);

        mRootLayout.addView(mTitleSwitch);
        mRootLayout.addView(mHorizontalScrollView);
        mRootLayout.addView(mBossSelection2);
        mRootLayout.addView(mBossEndTime);

        mTitleSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mHorizontalScrollView.setVisibility(View.VISIBLE);
                mBossSelection2.setVisibility(View.VISIBLE);
            } else {
                mBossSelection2.setVisibility(View.GONE);
                mHorizontalScrollView.setVisibility(View.GONE);
            }
        });

        mTitleSwitch.setChecked(MainActivity.mSharedPreferences.getBoolean(mBossInfoWrapper.BossEnabledConf, true));

        if (!mBossInfoWrapper.mBossAvailableLevels.contains(CheckPoints.BOSS_LEVEL_PRIMARY))
            mBossPrimary.setVisibility(View.GONE);
        if (!mBossInfoWrapper.mBossAvailableLevels.contains(CheckPoints.BOSS_LEVEL_MIDDLE))
            mBossMiddle.setVisibility(View.GONE);
        if (!mBossInfoWrapper.mBossAvailableLevels.contains(CheckPoints.BOSS_LEVEL_HIGH))
            mBossHigh.setVisibility(View.GONE);
        if (!mBossInfoWrapper.mBossAvailableLevels.contains(CheckPoints.BOSS_LEVEL_HIGH_PLUS))
            mBossHighPlus.setVisibility(View.GONE);
        if (!mBossInfoWrapper.mBossAvailableLevels.contains(CheckPoints.BOSS_LEVEL_SUPER))
            mBossSuper.setVisibility(View.GONE);

        for (String i : BossInfoWrapper.getBossLevels(mBossInfoWrapper.BossLevelsConf)) {
            if (i.equals(CheckPoints.BOSS_LEVEL_PRIMARY)) ((MaterialCheckBox)mRootLayout.findViewWithTag(Tags.WF_BOSS_LEVEL_PRIMARY_CHECKBOX)).setChecked(true);
            if (i.equals(CheckPoints.BOSS_LEVEL_MIDDLE)) ((MaterialCheckBox)mRootLayout.findViewWithTag(Tags.WF_BOSS_LEVEL_MIDDLE_CHECKBOX)).setChecked(true);
            if (i.equals(CheckPoints.BOSS_LEVEL_HIGH)) ((MaterialCheckBox)mRootLayout.findViewWithTag(Tags.WF_BOSS_LEVEL_HIGH_CHECKBOX)).setChecked(true);
            if (i.equals(CheckPoints.BOSS_LEVEL_HIGH_PLUS)) ((MaterialCheckBox)mRootLayout.findViewWithTag(Tags.WF_BOSS_LEVEL_HIGH_PLUS_CHECKBOX)).setChecked(true);
            if (i.equals(CheckPoints.BOSS_LEVEL_SUPER)) ((MaterialCheckBox)mRootLayout.findViewWithTag(Tags.WF_BOSS_LEVEL_SUPER_CHECKBOX)).setChecked(true);
        }

        return mRootLayout;
    }

    public static MaterialCheckBox getMaterialCheckBox(@StringRes int mText, Context mContext) {
        MaterialCheckBox mMaterialCheckBox = new MaterialCheckBox(mContext);
        mMaterialCheckBox.setText(mText);
        return mMaterialCheckBox;
    }

    public static SwitchMaterial getMaterialSwitch(@StringRes int mText, Context mContext) {
        SwitchMaterial mMaterialSwitch = new SwitchMaterial(mContext);
        mMaterialSwitch.setText(mText);
        return mMaterialSwitch;
    }

    public static LinearLayout getFeaturesTitle(@StringRes int mTitleText, Context mContext) {
        TextView mTitle = new TextView(mContext);
        mTitle.setText(mTitleText);
        mTitle.setTextSize(18);
        mTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout mLinearLayout = getLinearLayoutContainer(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0, 6, LinearLayout.VERTICAL, mContext);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.addView(mTitle);
        return mLinearLayout;
    }

    public static LinearLayout getFeaturesArea(Context mContext) {
        LinearLayout mLinearLayout = getLinearLayoutContainer(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0, 0, LinearLayout.VERTICAL, mContext);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setBackgroundResource(R.drawable.corner_background);
        mLinearLayout.setPadding(6, 6, 6, 6);
        return mLinearLayout;
    }

    // Common get a container: LinearLayout
    public static LinearLayout getLinearLayoutContainer(int width, int height, int leftAndRightMargin, int topAndBottomMargin, int mOrientation, Context mContext) {
        LinearLayout.LayoutParams mLinearLayoutContainerParams = new LinearLayout.LayoutParams(
                width, height
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

    public static FrameLayout getFrameLayoutContainer(int leftAndRightMargin, int topAndBottomMargin, Context mContext) {
        FrameLayout.LayoutParams mFrameLayoutContainerParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        );
        mFrameLayoutContainerParams.leftMargin = leftAndRightMargin;
        mFrameLayoutContainerParams.rightMargin = leftAndRightMargin;
        mFrameLayoutContainerParams.topMargin = topAndBottomMargin;
        mFrameLayoutContainerParams.bottomMargin = topAndBottomMargin;
        FrameLayout mFrameLayoutContainer = new FrameLayout(mContext);
        mFrameLayoutContainer.setLayoutParams(mFrameLayoutContainerParams);
        return mFrameLayoutContainer;
    }

    // Common get a container: Card
    public static MaterialCardView getMaterialCardContainer(Context mContext) {
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
}

class StopTrackerListener implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        MainActivity.mHandler.sendEmptyMessage(HandlerMessage.STOP_TRACKER_SERVICE);
    }
}