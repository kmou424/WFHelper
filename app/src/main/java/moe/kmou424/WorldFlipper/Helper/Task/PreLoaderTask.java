package moe.kmou424.WorldFlipper.Helper.Task;

import android.content.Context;
import android.os.Handler;

import moe.kmou424.WorldFlipper.Helper.Logger.Logger;
import moe.kmou424.WorldFlipper.Helper.MainActivity;
import moe.kmou424.WorldFlipper.Helper.Thread.WFThread;
import moe.kmou424.WorldFlipper.Helper.Tools.FileUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;

public class PreLoaderTask {
    private final String LOG_TAG = "PreLoader";

    protected final Context mContext;

    public PreLoaderTask(Context mContext) {
        this.mContext = mContext;
        Handler mHandler = MainActivity.mHandler;
    }

    /*
     * Get a thread to check integrity of prepared files
     * return a WFTread, it can run anywhere
     */
    public WFThread getPreLoadFilesChecker(Thread mLastThread) {
        String mThreadName = new Exception().getStackTrace()[0].getMethodName();
        WFThread ret = new WFThread(mLastThread) {
            @Override
            public void run() {
                super.run();
                for (String mTRAINED_DATA : TesseractOCR.TRAINED_DATA_RES_LIST) {
                    String targetFile = TesseractOCR.getTessDataDir() + "/" + mTRAINED_DATA;
                    Logger.outWithSysStream(Logger.INFO, LOG_TAG, mThreadName, "Checking file \"" + targetFile + "\"");
                    if (!FileUtils.isFileExist(targetFile)) {
                        Logger.outWithSysStream(Logger.INFO, LOG_TAG, mThreadName, "\"" + targetFile + "\" not found, copying...");
                        FileUtils.copyAssets(mContext, TesseractOCR.getTessDataDir(), mTRAINED_DATA);
                    }
                }
            }
        };
        ret.setName(mThreadName);
        return ret;
    }
}
