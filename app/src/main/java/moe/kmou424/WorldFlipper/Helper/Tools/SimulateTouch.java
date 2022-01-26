package moe.kmou424.WorldFlipper.Helper.Tools;

import android.annotation.SuppressLint;

import java.io.OutputStream;

@SuppressLint("DefaultLocale")
public class SimulateTouch {
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

    public static void click(int x, int y) {
        exec(String.format("input tap %d %d &\n", x, y));
    }
}
