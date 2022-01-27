package moe.kmou424.WorldFlipper.Helper;

import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.scottyab.rootbeer.RootBeer;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.Action.ToastHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.UI.ProgressDialogHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.Push.TesseractOCRHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

import moe.kmou424.WorldFlipper.Helper.Service.FloatingWindowService;
import moe.kmou424.WorldFlipper.Helper.Tools.Constructor;
import moe.kmou424.WorldFlipper.Helper.Tools.FileUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.RootManager;
import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;
import moe.kmou424.WorldFlipper.Helper.Widget.ProgressDialog;

public class MainActivity extends AppCompatActivity {
    public static Handler mHandler;

    private final RootBeer mRootBeer = new RootBeer(MainActivity.this);

    private TextView mRootStatus, mRootLib, mRootAccess;
    private TextView mFloatingStatus;
    private MaterialButton mFloatingSwitch;
    private Toast mToast;

    private boolean isFloatingWindowShown = false;
    private boolean isRootAvailable = false;

    // Custom Widget
    private moe.kmou424.WorldFlipper.Helper.Widget.ProgressDialog mProgressDialog;

    private Intent mFloatingWindowServiceIntent;

    protected TesseractOCR mTesseractOCR;

    void initHandler() {
        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
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
                    case PUSH_TESS_OCR:
                        TesseractOCRHandlerMsg tesseractOCRHandlerMsg = new Constructor<>((TesseractOCRHandlerMsg) msg.obj).make();
                        mTesseractOCR = tesseractOCRHandlerMsg.getTesseractOCR();
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
                            isFloatingWindowShown = true;
                        }
                        if (msg.what == HIDE_FLOATING_WINDOW && isFloatingWindowShown) {
                            stopService(mFloatingWindowServiceIntent);
                            isFloatingWindowShown = false;
                        }
                        break;
                }
                System.gc();
            }
        };
    }

    void initObjects() {
        mRootStatus = findViewById(R.id.root_status);
        mRootLib = findViewById(R.id.root_lib);
        mRootAccess = findViewById(R.id.root_access);
        mFloatingStatus = findViewById(R.id.floating_status);
        mFloatingSwitch = findViewById(R.id.floating_switch);
        mToast = Toast.makeText(MainActivity.this, null, Toast.LENGTH_LONG);

        mFloatingWindowServiceIntent = new Intent(MainActivity.this, FloatingWindowService.class);
    }

    void initRoot() {
        if (mRootBeer.isRooted()) {
            mRootStatus.setText(String.format("%s%s", getString(R.string.root_status_text), getString(R.string.yes)));
            if (mRootBeer.checkForMagiskBinary())
                mRootLib.setText(String.format("%s%s", getString(R.string.root_lib_text), getString(R.string.root_lib_magisk_su)));
            if (mRootBeer.checkForSuBinary())
                mRootLib.setText(String.format("%s%s", getString(R.string.root_lib_text), getString(R.string.root_lib_super_su)));
            if (!mRootBeer.checkForMagiskBinary() && !mRootBeer.checkForSuBinary())
                mRootLib.setText(String.format("%s%s", getString(R.string.root_lib_text), getString(R.string.unknown)));
        } else {
            mRootStatus.setText(String.format("%s%s", getString(R.string.root_status_text), getString(R.string.no)));
            mRootLib.setText(String.format("%s%s", getString(R.string.root_lib_text), getString(R.string.unknown)));
        }
        isRootAvailable = RootManager.requestRoot();
        if (isRootAvailable) {
            mRootAccess.setText(String.format("%s%s", getString(R.string.root_access), getString(R.string.yes)));
        } else {
            mRootAccess.setText(String.format("%s%s", getString(R.string.root_access), getString(R.string.no)));
        }
    }

    private void bindListeners() {
        mFloatingSwitch.setOnClickListener(view -> {
            if (isRootAvailable) {
                if (isFloatingWindowShown) {
                    mFloatingStatus.setText(getString(R.string.status_hidden));
                    mFloatingSwitch.setText(getString(R.string.show_floating));
                    mHandler.sendEmptyMessage(HIDE_FLOATING_WINDOW);
                } else {
                    mFloatingStatus.setText(getString(R.string.status_shown));
                    mFloatingSwitch.setText(getString(R.string.hide_floating));
                    mHandler.sendEmptyMessage(SHOW_FLOATING_WINDOW);
                }
            } else {
                mToast.setText("请先授予Root再启动悬浮窗");
                mToast.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!FileUtils.isDirExist(FileUtils.getExternalRoot())) FileUtils.createDir(FileUtils.getExternalRoot());
        FileUtils.deleteFile(FileUtils.getExternalRoot() + "/log.txt");
        Logger.out(Logger.INFO, getLocalClassName(), "onCreate", "Application launched");
        checkPermission();
        initHandler();
        initObjects();
        initRoot();
        bindListeners();
        new MainThread(MainActivity.this, mHandler).start();
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