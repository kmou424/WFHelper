package moe.kmou424.WorldFlipper.Helper.Tools;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private final Context mContext;

    public FileUtils(Context mContext) {
        this.mContext = mContext;
    }

    /*
     * Get external storage root dir of application
     */
    public static String getExternalRoot() {
        return getExternalSubPath("WFHelper");
    }

    /*
     * Copy assets file to external storage
     * mOutputDir: Target directory on external storage
     * mFileName: Relative path of asset file in apk
     */
    public void copyAssets(String mOutputDir, String mFileName) {
        if (isDirExist(mOutputDir)) {
            if (!createDir(mOutputDir)) return;
        }
        InputStream myInput;
        OutputStream myOutput;
        try {
            myOutput = new FileOutputStream(mOutputDir + "/" + mFileName);
            myInput = mContext.getAssets().open(mFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while(length > 0){
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Detect a file if it is accessed
     * mPath: Target file path
     */
    public static boolean deleteFile(String mPath) {
        File file = new File(mPath);
        return file.delete();
    }

    /*
     * Create a directory (if root directory is not exist)
     * mDirPath: Target directory path
     */
    public static boolean createDir(String mDirPath) {
        File folder = new File(mDirPath);
        return folder.mkdirs();
    }

    /*
     * Detect directory is exist or not
     * mDirPath: Target directory path
     */
    public static boolean isDirExist(String mDirPath) {
        File folder = new File(mDirPath);
        return !folder.exists() || !folder.isDirectory();
    }

    /*
    * Detect file is exist or not
    * mFilePath: Target filepath
    */
    public static boolean isFileExist(String mFilePath) {
        File file = new File(mFilePath);
        return !file.exists() || !file.isFile();
    }

    /*
    * Get sub directory in external storage
    * mSubPath: Sub directory name
    */
    public static String getExternalSubPath(String mSubPath) {
        return new File(Environment.getExternalStorageDirectory(), mSubPath).getAbsolutePath();
    }
}
