package moe.kmou424.WorldFlipper.Helper.Task;

import android.content.Context;
import android.os.Handler;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMessage;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.Push.TesseractOCRHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;
import moe.kmou424.WorldFlipper.Helper.Thread.WFThread;
import moe.kmou424.WorldFlipper.Helper.Tools.FileUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;

public class PreLoaderTask {
    private final String LOG_TAG = "PreLoader";
    private final Handler mHandler;

    protected final Context mContext;

    public PreLoaderTask(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    /*
     * Get a thread to check integrity of prepared files
     * return a WFTread, it can run anywhere
     */
    public WFThread getPreLoadFilesChecker(Thread mLastThread) {
        String mThreadName = new Exception().getStackTrace()[0].getMethodName();
        WFThread ret = new WFThread(this.mHandler, mLastThread) {
            @Override
            public void run() {
                super.run();
                for (String mTRAINED_DATA : TesseractOCR.TRAINED_DATA_RES_LIST) {
                    String targetFile = TesseractOCR.getTessDataDir() + "/" + mTRAINED_DATA;
                    Logger.out(Logger.INFO, LOG_TAG, mThreadName, "Checking file \"" + targetFile + "\"");
                    if (!FileUtils.isFileExist(targetFile)) {
                        Logger.out(Logger.INFO, LOG_TAG, mThreadName, "\"" + targetFile + "\" not found, copying...");
                        FileUtils.copyAssets(mContext, TesseractOCR.getTessDataDir(), mTRAINED_DATA);
                    }
                }
            }
        };
        ret.setName(mThreadName);
        return ret;
    }

    /*
     * Get a thread to initial OCR
     * return a WFTread, it can run anywhere
     */
    public WFThread getOCRLoader(Thread mLastThread) {
        WFThread ret = new WFThread(this.mHandler, mLastThread) {
            @Override
            public void run() {
                super.run();
                mHandler.sendMessage(
                        new HandlerMessage<TesseractOCRHandlerMsg>()
                                .make(
                                        new TesseractOCRHandlerMsg(new TesseractOCR("chi_sim")),
                                        HandlerMessage.PUSH_TESS_OCR
                                ));
            }
        };
        ret.setName(new Exception().getStackTrace()[0].getMethodName());
        return ret;
    }
}
