package moe.kmou424.WorldFlipper.Helper;

import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.scottyab.rootbeer.RootBeer;

import moe.kmou424.WorldFlipper.Helper.Constants.Global;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.Action.ToastHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.UI.ProgressDialogHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

import moe.kmou424.WorldFlipper.Helper.Service.FloatingWindowService;
import moe.kmou424.WorldFlipper.Helper.Tools.Constructor;
import moe.kmou424.WorldFlipper.Helper.Tools.FileUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.RootManager;
import moe.kmou424.WorldFlipper.Helper.Service.TrackerService;
import moe.kmou424.WorldFlipper.Helper.Widget.ProgressDialog;
import moe.kmou424.WorldFlipper.Helper.Widget.WFPanel;

public class MainActivity extends AppCompatActivity {
    public static Handler mHandler;
    public static SharedPreferences mSharedPreferences;

    private final RootBeer mRootBeer = new RootBeer(MainActivity.this);

    private TextView mRootStatus, mRootLib, mRootAccess;
    private TextView mFloatingStatus;
    private MaterialButton mFloatingSwitch;
    private Toast mToast;

    private boolean isFloatingWindowShown = false;
    private boolean isTrackerServiceRunning = false;
    private boolean isRootAvailable = false;

    // Custom Widget
    private moe.kmou424.WorldFlipper.Helper.Widget.ProgressDialog mProgressDialog;
    private WFPanel mWFPanel;

    private Intent mFloatingWindowServiceIntent;
    private Intent mTrackerServiceIntent;

    void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (Math.abs(msg.what)) {
                    case MOVE_TASK_TO_BACK:
                        moveTaskToBack(true);
                        break;
                    case SHOW_TOAST:
                        ToastHandlerMsg toastHandlerMsg = new Constructor<>(
                                (ToastHandlerMsg) msg.obj
                        ).make();
                        mToast.setText(toastHandlerMsg.getToastMessage());
                        mToast.setDuration(toastHandlerMsg.mDuration);
                        mToast.show();
                        break;
                    case SHOW_PROGRESS_DIALOG:
                        if (msg.what == HIDE_PROGRESS_DIALOG) {
                            mProgressDialog.hide();
                            mProgressDialog = null;
                        }
                        if (msg.what == SHOW_PROGRESS_DIALOG) {
                            mProgressDialog = new ProgressDialog(MainActivity.this);
                            mProgressDialog.setAttribute((ProgressDialogHandlerMsg) msg.obj).show();
                        }
                        break;
                    case SHOW_FLOATING_WINDOW:
                        if (msg.what == SHOW_FLOATING_WINDOW && !isFloatingWindowShown) {
                            startService(mFloatingWindowServiceIntent);
                            mFloatingStatus.setText(getString(R.string.status_shown));
                            mFloatingSwitch.setText(getString(R.string.hide_floating));
                            isFloatingWindowShown = true;
                        }
                        if (msg.what == HIDE_FLOATING_WINDOW && isFloatingWindowShown) {
                            stopService(mFloatingWindowServiceIntent);
                            mFloatingStatus.setText(getString(R.string.status_hidden));
                            mFloatingSwitch.setText(getString(R.string.show_floating));
                            isFloatingWindowShown = false;
                        }
                        break;
                    case SHOW_WF_PANEL:
                        if (msg.what == SHOW_WF_PANEL) {
                            mWFPanel = new WFPanel(MainActivity.this);
                            mWFPanel.show();
                        } else {
                            mWFPanel.hide();
                            mWFPanel = null;
                        }
                        break;
                    case START_TRACKER_SERVICE:
                        if (msg.what == START_TRACKER_SERVICE) {
                            if (isTrackerServiceRunning) stopService(mTrackerServiceIntent);
                            startService(mTrackerServiceIntent);
                            isTrackerServiceRunning = true;
                        }
                        if (msg.what == STOP_TRACKER_SERVICE) {
                            if (isTrackerServiceRunning) {
                                stopService(mTrackerServiceIntent);
                                isTrackerServiceRunning = false;
                            }
                        }
                }
                System.gc();
            }
        };
    }

    void initObjects() {
        mSharedPreferences = getSharedPreferences(Global.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        mRootStatus = findViewById(R.id.root_status);
        mRootLib = findViewById(R.id.root_lib);
        mRootAccess = findViewById(R.id.root_access);
        mFloatingStatus = findViewById(R.id.floating_status);
        mFloatingSwitch = findViewById(R.id.floating_switch);
        mToast = Toast.makeText(MainActivity.this, null, Toast.LENGTH_LONG);

        mFloatingWindowServiceIntent = new Intent(MainActivity.this, FloatingWindowService.class);
        mTrackerServiceIntent = new Intent(MainActivity.this, TrackerService.class);
    }

    void initRoot() {
        if (mRootBeer.isRooted()) {
            mRootStatus.setText(String.format("%s %s", getString(R.string.root_status_text), getString(R.string.yes)));
            if (mRootBeer.checkForSuBinary())
                mRootLib.setText(String.format("%s %s", getString(R.string.root_lib_text), getString(R.string.root_lib_super_su)));
            if (mRootBeer.checkForMagiskBinary())
                mRootLib.setText(String.format("%s %s", getString(R.string.root_lib_text), getString(R.string.root_lib_magisk_su)));
            if (!mRootBeer.checkForMagiskBinary() && !mRootBeer.checkForSuBinary())
                mRootLib.setText(String.format("%s %s", getString(R.string.root_lib_text), getString(R.string.unknown)));
        } else {
            mRootStatus.setText(String.format("%s %s", getString(R.string.root_status_text), getString(R.string.no)));
            mRootLib.setText(String.format("%s %s", getString(R.string.root_lib_text), getString(R.string.unknown)));
        }
        isRootAvailable = RootManager.requestRoot();
        if (isRootAvailable) {
            mRootAccess.setText(String.format("%s %s", getString(R.string.root_access), getString(R.string.yes)));
        } else {
            mRootAccess.setText(String.format("%s %s", getString(R.string.root_access), getString(R.string.no)));
        }
    }

    private void bindListeners() {
        mFloatingSwitch.setOnClickListener(view -> {
            if (isRootAvailable) {
                if (isFloatingWindowShown) {
                    mHandler.sendEmptyMessage(HIDE_FLOATING_WINDOW);
                } else {
                    mHandler.sendEmptyMessage(SHOW_FLOATING_WINDOW);
                }
            } else {
                mToast.setText("????????????Root??????????????????");
                mToast.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        if (!FileUtils.isDirExist(FileUtils.getExternalRoot())) FileUtils.createDir(FileUtils.getExternalRoot());
        FileUtils.deleteFile(FileUtils.getExternalRoot() + "/log.txt");
        Logger.outWithSysStream(Logger.INFO, getLocalClassName(), "onCreate", "Application launched");
        initHandler();
        initObjects();
        initRoot();
        bindListeners();
        new MainThread(MainActivity.this).start();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 121);
        }
        if (!Settings.canDrawOverlays(MainActivity.this)) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 122);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mFloatingWindowServiceIntent);
    }
}