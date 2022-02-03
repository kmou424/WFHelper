package moe.kmou424.WorldFlipper.Helper.Tools;

import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

import moe.kmou424.WorldFlipper.Helper.Info.BitmapInfo;

public class TestUtils {
    public static void testOCR(ImageView imageView, String subFilePath, BitmapInfo bitmapInfo) {
        TesseractOCR mTesseractOCR = new TesseractOCR("chi_sim");
        Bitmap b = BitmapUtils.read(Environment.getExternalStorageDirectory() + "/" + subFilePath);
        Bitmap bitmap = BitmapUtils.crop(b, bitmapInfo);
        imageView.setImageBitmap(bitmap);
        mTesseractOCR.getTextFromBitmap(bitmap);
    }

    public static void testOCRWithScreenShot(ImageView imageView, BitmapInfo bitmapInfo) {
        TesseractOCR mTesseractOCR = new TesseractOCR("chi_sim");
        String file = RootUtils.takeScreenShot();
        Bitmap b = BitmapUtils.read(file);
        Bitmap bitmap = BitmapUtils.crop(b, bitmapInfo);
        imageView.setImageBitmap(bitmap);
        mTesseractOCR.getTextFromBitmap(bitmap);
    }
}
