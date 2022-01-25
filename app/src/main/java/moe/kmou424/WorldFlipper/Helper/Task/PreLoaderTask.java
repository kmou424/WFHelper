package moe.kmou424.WorldFlipper.Helper.Task;

import android.content.Context;
import android.os.Handler;

import moe.kmou424.WorldFlipper.Helper.HandlerMsg.HandlerMsg;
import moe.kmou424.WorldFlipper.Helper.HandlerMsg.TesseractOCRHandlerMsg;
import moe.kmou424.WorldFlipper.Helper.Thread.WFThread;
import moe.kmou424.WorldFlipper.Helper.Tools.FileUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;

public class PreLoaderTask {
    private final String LOG_TAG = "PreLoader";
    private final FileUtils mFileUtils;
    private final Handler mHandler;

    protected final Context mContext;

    public PreLoaderTask(Context mContext, FileUtils mFileUtils, Handler mHandler) {
        this.mContext = mContext;
        this.mFileUtils = mFileUtils;
        this.mHandler = mHandler;
    }

    /*
     * Get a thread to check integrity of prepared files
     * return a WFTread, it can run anywhere
     */
    public WFThread getPreLoadFilesChecker(Thread mLastThread) {
        WFThread ret = new WFThread(this.mHandler, mLastThread) {
            @Override
            public void run() {
                super.run();
                for (String mTRAINED_DATA : TesseractOCR.TRAINED_DATA_RES_LIST) {
                    if (FileUtils.isDirExist(mTRAINED_DATA))
                        mFileUtils.copyAssets(TesseractOCR.getTessDataDir(), mTRAINED_DATA);
                }
            }
        };
        ret.setName(new Exception().getStackTrace()[0].getMethodName());
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
                        new HandlerMsg<TesseractOCRHandlerMsg>()
                                .makeMessage(
                                        new TesseractOCRHandlerMsg(new TesseractOCR("chi_sim")),
                                        HandlerMsg.PUSH_TESS_OCR
                                ));
            }
        };
        ret.setName(new Exception().getStackTrace()[0].getMethodName());
        return ret;
    }
}
