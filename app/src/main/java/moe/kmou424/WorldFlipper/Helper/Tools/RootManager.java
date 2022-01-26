package moe.kmou424.WorldFlipper.Helper.Tools;

public class RootManager {
    public static boolean requestRoot(){
        boolean ret = false;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su -c echo 0");
            process.waitFor();
            ret = (process.exitValue() == 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(process != null){
                process.destroy();
            }
        }
        return ret;
    }
}
