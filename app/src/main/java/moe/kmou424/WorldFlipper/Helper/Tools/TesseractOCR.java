package moe.kmou424.WorldFlipper.Helper.Tools;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import moe.kmou424.WorldFlipper.Helper.Constants.Global;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

public class TesseractOCR {
    private final static String LOG_TAG = "TesseractOCR";
    private final TessBaseAPI mTessBaseAPI;
    public static final String[] TRAINED_DATA_RES_LIST = {"chi_sim.traineddata", "eng.traineddata"};
    private boolean isRecycled;

    public TesseractOCR(String language) {
        mTessBaseAPI = new TessBaseAPI();
        Logger.out(Logger.INFO, LOG_TAG, LOG_TAG,
                "Init details: Location: \"" + getTessDataDir() + "\", Language: \"" + language + "\"");
        mTessBaseAPI.init(FileUtils.getExternalRoot() + "/", language);
        isRecycled = false;
    }

    /*
     * Get main OCR object
     */
    public TessBaseAPI getTessBaseAPI() {
        return mTessBaseAPI;
    }

    /*
     * Reset OCR language
     */
    public void resetTessBaseAPI(String language) {
        mTessBaseAPI.recycle();
        mTessBaseAPI.init(getTessDataDir(), language);
    }

    public void recycle() {
        mTessBaseAPI.recycle();
        isRecycled = true;
    }

    /*
     * Get traineddata directory
     */
    public static String getTessDataDir() {
        return FileUtils.getExternalRoot() + "/tessdata";
    }

    /*
    * Set bitmap and get text
    * Remove all space
    */
    public String getTextFromBitmap(Bitmap mBitmap) {
        if (isRecycled) return null;
        mTessBaseAPI.setImage(mBitmap);
        String ret = mTessBaseAPI.getUTF8Text().replaceAll(" ", "");
        if (Global.DEBUG) Log.d(LOG_TAG, ret);
        Logger.out(Logger.INFO, LOG_TAG, "getTextFromBitmap", ret);
        return ret;
    }
}
