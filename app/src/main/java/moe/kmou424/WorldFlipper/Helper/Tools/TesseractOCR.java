package moe.kmou424.WorldFlipper.Helper.Tools;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

public class TesseractOCR {
    private final static String LOG_TAG = "TesseractOCR";
    private final TessBaseAPI mTessBaseAPI;
    public static final String[] TRAINED_DATA_RES_LIST = {"chi_sim.traineddata", "eng.traineddata"};
    private boolean isRecycled;
    private String mLanguage;

    public TesseractOCR(String language) {
        mTessBaseAPI = new TessBaseAPI();
        Logger.outWithSysStream(Logger.INFO, LOG_TAG, LOG_TAG,
                "Init details: Location: \"" + getTessDataDir() + "\", Language: \"" + language + "\"");
        mTessBaseAPI.init(FileUtils.getExternalRoot() + "/", language);
        this.mLanguage = language;
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
        if (isRecycled) return "";
        mTessBaseAPI.setImage(mBitmap);
        String ret = mTessBaseAPI.getUTF8Text().replaceAll(" ", "");
        Logger.outWithSysStream(Logger.INFO, String.format("%s(%s)", LOG_TAG, mLanguage), "getTextFromBitmap", ret);
        return ret;
    }
}
