package moe.kmou424.WorldFlipper.Helper.Thread;

import android.content.Context;
import android.os.Handler;

import moe.kmou424.WorldFlipper.Helper.Tools.FileUtils;
import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;

public class PreLoader {
    private final String LOG_TAG = "PreLoader";
    private final FileUtils mFileUtils;
    private final Handler mHandler;

    protected final Context mContext;

    public PreLoader(Context mContext, FileUtils mFileUtils, Handler mHandler) {
        this.mContext = mContext;
        this.mFileUtils = mFileUtils;
        this.mHandler = mHandler;
    }

    /*
     * Get a thread to check integrity of prepared files
     * return a WFTread, it can run anywhere
     */
    public WFThread<Boolean> getPreLoadFilesChecker(Thread mLastThread) {
        final String mThreadName = "PreLoadFilesChecker";
        WFThread<Boolean> ret = new WFThread<>(this.mHandler, mLastThread) {
            @Override
            public void run() {
                super.run();
                for (String mTRAINED_DATA : TesseractOCR.TRAINED_DATA_RES_LIST) {
                    if (FileUtils.isDirExist(mTRAINED_DATA))
                        mFileUtils.copyAssets(TesseractOCR.getTessDataDir(), mTRAINED_DATA);
                }
                this.Result = true;
            }
        };
        ret.setName(mThreadName);
        return ret;
    }

    /*
     * Get a thread to initial OCR
     * return a WFTread, it can run anywhere
     */
    public WFThread<TesseractOCR> getOCRLoader(Thread mLastThread) {
        final String mThreadName = "OCRLoader";
        WFThread<TesseractOCR> ret = new WFThread<>(this.mHandler, mLastThread) {
            @Override
            public void run() {
                super.run();
                this.Result = new TesseractOCR("chi_sim");
            }
        };
        ret.setName(mThreadName);
        return ret;
    }
}
