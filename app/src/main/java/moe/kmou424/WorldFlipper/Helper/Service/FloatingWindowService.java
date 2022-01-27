package moe.kmou424.WorldFlipper.Helper.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import moe.kmou424.WorldFlipper.Helper.Constant;
import moe.kmou424.WorldFlipper.Helper.Listener.FloatingWindowListener;
import moe.kmou424.WorldFlipper.Helper.R;
import moe.kmou424.WorldFlipper.Helper.Widget.FloatingWindow;

public class FloatingWindowService extends Service {
    private final static String LOG_TAG = "FloatingWindowService";
    private final static int NOTIFICATION_ID = 101;

    private FloatingWindow mFloatingWindow;
    private FloatingWindowListener mFloatingWindowListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (Constant.DEBUG) Log.d(LOG_TAG, "onDestroy");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        if (Constant.DEBUG) Log.d(LOG_TAG, "onCreate");
        super.onCreate();
    }

    void buildDaemon() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText("DaemonService is running...");
        startForeground(NOTIFICATION_ID, builder.build());
    }

    void restartDaemon() {
        NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mManager.cancel(NOTIFICATION_ID);
        Intent intent = new Intent(getApplicationContext(), FloatingWindowService.class);
        startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Constant.DEBUG) Log.d(LOG_TAG, "onStartCommand");
        buildDaemon();
        mFloatingWindow = new FloatingWindow(this);
        mFloatingWindow.show();
        mFloatingWindowListener = new FloatingWindowListener(this, mFloatingWindow);
        mFloatingWindowListener.bind();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (Constant.DEBUG) Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
        // restartDaemon();
        mFloatingWindow.hide();
        mFloatingWindow = null;
        mFloatingWindowListener = null;
        System.gc();
    }
}
