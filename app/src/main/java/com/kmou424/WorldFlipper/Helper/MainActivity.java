package com.kmou424.WorldFlipper.Helper;

import static com.kmou424.WorldFlipper.Helper.Handler.HandlerMsg.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.kmou424.WorldFlipper.Helper.Handler.HandlerMsg;
import com.kmou424.WorldFlipper.Helper.Handler.ProgressDialogHandlerMsg;
import com.kmou424.WorldFlipper.Helper.Logger.Logger;
import com.kmou424.WorldFlipper.Helper.Thread.WFThread;
import com.kmou424.WorldFlipper.Helper.Thread.PreLoader;
import com.kmou424.WorldFlipper.Helper.Tools.FileUtils;
import com.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;

public class MainActivity extends AppCompatActivity {

    //private TextView mMainLog;
    private Handler mHandler;

    private ProgressDialog mProgressDialog;

    protected TesseractOCR mTesseractOCR;

    void init() {
        //mMainLog = findViewById(R.id.main_log);
        mProgressDialog = new ProgressDialog(MainActivity.this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.out(Logger.INFO, getLocalClassName(), "onCreate", "Application launched");
        init();
        checkPermission();

        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (Math.abs(msg.what)) {
                    case MOVE_TASK_TO_BACK:
                        moveTaskToBack(true);
                        break;
                    case PUSH_TESS_OCR:
                        mTesseractOCR = (TesseractOCR) msg.obj;
                        break;
                    case SHOW_PROGRESS_DIALOG:
                        if (msg.what == HIDE_PROGRESS_DIALOG) mProgressDialog.dismiss();
                        if (msg.what == SHOW_PROGRESS_DIALOG) {
                            ProgressDialogHandlerMsg progressDialogHandlerMsg = (ProgressDialogHandlerMsg) msg.obj;
                            mProgressDialog.setTitle(progressDialogHandlerMsg.mTitle);
                            mProgressDialog.setMessage(progressDialogHandlerMsg.mMessage);
                            mProgressDialog.setMax(progressDialogHandlerMsg.maxProgress);
                            mProgressDialog.setProgress(progressDialogHandlerMsg.mProgress);
                            mProgressDialog.setProgressStyle(progressDialogHandlerMsg.mProgressStyle);
                            mProgressDialog.setCancelable(progressDialogHandlerMsg.mCancelable);
                            mProgressDialog.show();
                        }
                        break;
                }
            }
        };
        new MainThread(MainActivity.this, mHandler).start();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 121);
        }
    }
}

class MainThread extends Thread {
    private final Context mContext;
    private final Handler mHandler;
    FileUtils mFileUtils;
    PreLoader mPreLoader;
    WFThread<Boolean> mPreLoadFilesChecker;
    WFThread<TesseractOCR> mOCRLoader;

    public MainThread(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    private void init() {
        mFileUtils = new FileUtils(mContext);
        mPreLoader = new PreLoader(mContext, mFileUtils, mHandler);
        mPreLoadFilesChecker = mPreLoader.getPreLoadFilesChecker(null);
        mOCRLoader = mPreLoader.getOCRLoader(mPreLoadFilesChecker);
    }

    @Override
    public void run() {
        init();
        mPreLoadFilesChecker.start();
        mOCRLoader.start();
        mOCRLoader.waitFor();
        mHandler.sendMessage(new HandlerMsg<TesseractOCR>().makeMessage(mOCRLoader.getResult(), PUSH_TESS_OCR));
        mHandler.sendMessage(new HandlerMsg<>().makeMessage(null, MOVE_TASK_TO_BACK));
    }
}