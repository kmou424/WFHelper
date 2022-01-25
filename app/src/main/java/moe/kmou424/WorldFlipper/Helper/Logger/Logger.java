package moe.kmou424.WorldFlipper.Helper.Logger;

import android.annotation.SuppressLint;

import moe.kmou424.WorldFlipper.Helper.Tools.FileUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public final static int DEBUG = 0;
    public final static int INFO = 1;
    public final static int WARNING = 2;
    public final static int ERROR = 3;

    private final static String[] LoggerLevel = {
            "D", "I", "W", "E"
    };

    private static final String LOG_FILE = "log.txt";
    private static PrintStream mTargetOut;

    private static void redirectOutStream() {
        try {
            mTargetOut = new PrintStream(new FileOutputStream(FileUtils.getExternalRoot() + "/" + LOG_FILE, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(mTargetOut);
    }

    private static void restoreOutStream() {
        System.setOut(System.out);
    }

    private static String getLoggerTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter= new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static void out(int mLoggerLevel, String mLogClass, String mLogMethod, String mLogText) {
        redirectOutStream();
        System.out.print(getLoggerTime() +
                " [" + LoggerLevel[mLoggerLevel] + "] " +
                mLogClass + "." + mLogMethod + "(): " +
                mLogText + "\n");
        restoreOutStream();
    }
}
