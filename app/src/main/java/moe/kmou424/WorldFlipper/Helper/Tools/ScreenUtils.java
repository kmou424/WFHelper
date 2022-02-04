package moe.kmou424.WorldFlipper.Helper.Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.PrintStream;

import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

public class ScreenUtils {
    private final static String LOG_TAG = "ScreenUtils";
    public static String takeScreenShot(){
        String mDir = FileUtils.getExternalRoot() + "/screenshot";
        if (!FileUtils.isDirExist(mDir))
            FileUtils.createDir(mDir);
        Process process = null;
        String filePath = mDir + "/" + System.currentTimeMillis() + ".wfg";
        try {
            process = Runtime.getRuntime().exec("su");
            PrintStream outputStream;
            outputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
            outputStream.println("screencap -p " + filePath);
            outputStream.flush();
            outputStream.close();
            process.waitFor();
            if (process.exitValue() != 0) return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(process != null){
                process.destroy();
            }
        }
        Logger.outWithSysStream(Logger.INFO, LOG_TAG, "takeScreenShot", "Screenshot was save at " + filePath);
        return filePath;
    }

    public static Bitmap takeScreenShotAsBitmap() {
        Bitmap ret = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintStream outputStream;
            outputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
            outputStream.println("screencap -p");
            outputStream.flush();
            outputStream.close();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(process.getInputStream());
            ret = BitmapFactory.decodeStream(bufferedInputStream);
            bufferedInputStream.close();
            process.waitFor();
            if (process.exitValue() != 0) return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(process != null){
                process.destroy();
            }
        }
        Logger.outWithSysStream(Logger.INFO, LOG_TAG, "takeScreenShotAsBitmap", "Shouted");
        return ret;
    }
}
