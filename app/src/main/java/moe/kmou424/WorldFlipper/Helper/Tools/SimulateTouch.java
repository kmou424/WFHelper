package moe.kmou424.WorldFlipper.Helper.Tools;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.util.Log;

import java.io.OutputStream;

import moe.kmou424.WorldFlipper.Helper.Constants.Global;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

@SuppressLint("DefaultLocale")
public class SimulateTouch {
    private final static String LOG_TAG = "SimulateTouch";
    private static Process process;
    private static OutputStream os;

    private static void exec(String cmd) {
        try {
            if (process == null) {
                process = Runtime.getRuntime().exec("su");
                os = process.getOutputStream();
            }
            os.write(cmd.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void key(int keyCode) {
        exec(String.format("input keyevent %d &\n", keyCode));
    }

    public static void swipe(Point mStart, Point mEnd, int duration) {
        exec(String.format("input swipe %d %d %d %d %d\n", mStart.x, mStart.y, mEnd.x, mEnd.y, duration));
        if (Global.DEBUG) Log.d(LOG_TAG, String.format("swipe: (%d, %d) -> (%d, %d), Used %d ms", mStart.x, mStart.y, mEnd.x, mEnd.y, duration));
        Logger.out(Logger.INFO, LOG_TAG, "swipe", String.format("(%d, %d) -> (%d, %d), Used %d ms", mStart.x, mStart.y, mEnd.x, mEnd.y, duration));
    }

    public static void click(Point mPoint) {
        exec(String.format("input tap %d %d &\n", mPoint.x, mPoint.y));
        if (Global.DEBUG) Log.d(LOG_TAG, String.format("click: (%d, %d)", mPoint.x, mPoint.y));
        Logger.out(Logger.INFO, LOG_TAG, "click", String.format("(%d, %d)", mPoint.x, mPoint.y));
    }
}
