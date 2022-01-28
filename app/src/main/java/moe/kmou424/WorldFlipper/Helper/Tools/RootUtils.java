package moe.kmou424.WorldFlipper.Helper.Tools;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import moe.kmou424.WorldFlipper.Helper.Constants.Global;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

public class RootUtils {
    private final static String LOG_TAG = "RootUtils";

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
        if (Global.DEBUG) Log.d(LOG_TAG, "Screenshot was save at " + filePath);
        Logger.out(Logger.INFO, LOG_TAG, "takeScreenShot", "Screenshot was save at " + filePath);
        return filePath;
    }

    public static String getTopProcess() {
        StringBuilder result = new StringBuilder();
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes("dumpsys activity top | grep ACTIVITY\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line;
            while ((line = dis.readLine()) != null) {
                result.append(line);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }
}
