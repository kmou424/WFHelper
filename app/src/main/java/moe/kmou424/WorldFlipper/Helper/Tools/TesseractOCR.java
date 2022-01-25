package moe.kmou424.WorldFlipper.Helper.Tools;

import com.googlecode.tesseract.android.TessBaseAPI;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

public class TesseractOCR {
    private final static String LOG_TAG = "TesseractOCR";
    private final TessBaseAPI mTessBaseAPI;
    public static final String[] TRAINED_DATA_RES_LIST = {"chi_sim.traineddata", "chi_sim_vert.traineddata", "eng.traineddata"};

    public TesseractOCR(String language) {
        this.mTessBaseAPI = new TessBaseAPI();
        Logger.out(Logger.INFO, LOG_TAG, LOG_TAG,
                "Init details: Location: \"" + getTessDataDir() + "\", Language: \"" + language + "\"");
        this.mTessBaseAPI.init(FileUtils.getExternalRoot() + "/", language);
    }

    /*
     * Get main OCR object
     */
    public TessBaseAPI getTessBaseAPI() {
        return this.mTessBaseAPI;
    }

    /*
     * Reset OCR language
     */
    public void resetTessBaseAPI(String language) {
        mTessBaseAPI.recycle();
        this.mTessBaseAPI.init(getTessDataDir(), language);
    }

    /*
     * Get traineddata directory
     */
    public static String getTessDataDir() {
        return FileUtils.getExternalRoot() + "/tessdata";
    }
}
