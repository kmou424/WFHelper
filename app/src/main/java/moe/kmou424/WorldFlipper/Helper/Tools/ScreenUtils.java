package moe.kmou424.WorldFlipper.Helper.Tools;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.PrintStream;

public class ScreenUtils {
    private final static String LOG_TAG = "ScreenUtils";

    public ScreenUtils() {
        Log.d(LOG_TAG, "Initial");
    }

    public static String takeScreenShot(){
        String mDir = FileUtils.getExternalRoot() + "/screenshot";
        if (!FileUtils.isDirExist(mDir))
            FileUtils.createDir(mDir);
        Process process = null;
        String filePath = mDir + "/" + System.currentTimeMillis() + ".png";
        try {
            process = Runtime.getRuntime().exec("su");
            PrintStream outputStream = null;
            outputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
            outputStream.println("screencap -p " + filePath);
            outputStream.flush();
            outputStream.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(process != null){
                process.destroy();
            }
        }
        return filePath;
    }
}
