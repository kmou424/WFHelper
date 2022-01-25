package moe.kmou424.WorldFlipper.Helper;

import static moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMsg.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.FloatingWindowHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.ProgressDialogHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.TesseractOCRHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.Listener.FloatingOnTouchListener;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;
import moe.kmou424.WorldFlipper.Helper.Widget.ProgressDialog;

public class MainActivity extends AppCompatActivity {

    //private TextView mMainLog;
    private Handler mHandler;
    private Toast mToast;

    // Custom Widget
    private moe.kmou424.WorldFlipper.Helper.Widget.ProgressDialog mProgressDialog;

    protected TesseractOCR mTesseractOCR;


    void initHandler() {
        mHandler = new Handler(getMainLooper()) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (Math.abs(msg.what)) {
                    case MOVE_TASK_TO_BACK:
                        moveTaskToBack(true);
                        break;
                    case PUSH_TESS_OCR:
                        TesseractOCRHandlerMsg tesseractOCRHandlerMsg = (TesseractOCRHandlerMsg) msg.obj;
                        mTesseractOCR = tesseractOCRHandlerMsg.mTesseractOCR;
                        break;
                    case SHOW_PROGRESS_DIALOG:
                        if (msg.what == HIDE_PROGRESS_DIALOG) mProgressDialog.hide();
                        if (msg.what == SHOW_PROGRESS_DIALOG) {
                            mProgressDialog.setAttribute((ProgressDialogHandlerMsg) msg.obj);
                            mProgressDialog.show();
                        }
                        break;
                    case SHOW_FLOATING_WINDOW:
                        FloatingWindowHandlerMsg fwh_msg = (FloatingWindowHandlerMsg) msg.obj;
                        fwh_msg.mWindowManager.addView(fwh_msg.mView, fwh_msg.mLayoutParams);
                        fwh_msg.mView.setOnTouchListener(new FloatingOnTouchListener(fwh_msg));
                        fwh_msg.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                        break;
                }
            }
        };
    }

    void initObjects() {
        //mMainLog = findViewById(R.id.main_log);
        mToast = Toast.makeText(MainActivity.this, null, Toast.LENGTH_LONG);

        // Custom Widget
        mProgressDialog = new ProgressDialog(MainActivity.this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.out(Logger.INFO, getLocalClassName(), "onCreate", "Application launched");
        checkPermission();
        initHandler();
        initObjects();
        new moe.kmou424.WorldFlipper.Helper.MainThread(MainActivity.this, mHandler).start();
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
}