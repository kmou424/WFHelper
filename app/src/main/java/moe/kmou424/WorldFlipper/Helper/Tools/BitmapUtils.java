package moe.kmou424.WorldFlipper.Helper.Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import moe.kmou424.WorldFlipper.Helper.Constants.Global;
import moe.kmou424.WorldFlipper.Helper.Info.BitmapInfo;
import moe.kmou424.WorldFlipper.Helper.Info.RgbInfo;
import moe.kmou424.WorldFlipper.Helper.Logger.Logger;

public class BitmapUtils {
    private final static String LOG_TAG = "ImageUtils";

    public static Bitmap crop(Bitmap mBitmap, BitmapInfo mBitmapInfo) {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, mBitmapInfo.point.x, mBitmapInfo.point.y, mBitmapInfo.width, mBitmapInfo.height);
        return setGrayscale(bitmap);
    }

    public static Bitmap cropWhiteFont(Bitmap mBitmap, BitmapInfo mBitmapInfo) {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, mBitmapInfo.point.x, mBitmapInfo.point.y, mBitmapInfo.width, mBitmapInfo.height);
        return setGrayscale(invertBitmap(bitmap));
    }

    public static Bitmap read(String mImagePath) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(mImagePath);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static String getHexRGBString(Bitmap mBitmap, Point mPoint) {
        int pixel = mBitmap.getPixel(mPoint.x, mPoint.y);
        String ret = String.format("#%s%s%s",
                Integer.toHexString(Color.red(pixel)),
                Integer.toHexString(Color.green(pixel)),
                Integer.toHexString(Color.blue(pixel))).toUpperCase();
        String mLogContent = String.format("Picked color %s", ret);
        if (Global.DEBUG) Log.d(LOG_TAG, mLogContent);
        Logger.out(Logger.INFO, LOG_TAG, "getHexRGBString", mLogContent);

        return ret;
    }

    public static int getHexRGB(Bitmap mBitmap, Point mPoint) {
        int pixel = mBitmap.getPixel(mPoint.x, mPoint.y);
        String ret = String.format("%s%s%s",
                Integer.toHexString(Color.red(pixel)),
                Integer.toHexString(Color.green(pixel)),
                Integer.toHexString(Color.blue(pixel)));
        String mLogContent = String.format("Picked color %s", ret);
        if (Global.DEBUG) Log.d(LOG_TAG, mLogContent);
        Logger.out(Logger.INFO, LOG_TAG, "getHexRGB", mLogContent);

        return Integer.parseInt(ret, 16);
    }

    public static RgbInfo getPixelRgbInfo(Bitmap mBitmap, Point mPoint) {
        int pixel = mBitmap.getPixel(mPoint.x, mPoint.y);
        String ret = String.format("%s%s%s",
                Integer.toHexString(Color.red(pixel)),
                Integer.toHexString(Color.green(pixel)),
                Integer.toHexString(Color.blue(pixel)));
        String mLogContent = String.format("Picked color %s", ret);
        if (Global.DEBUG) Log.d(LOG_TAG, mLogContent);
        Logger.out(Logger.INFO, LOG_TAG, "getPixelRgbInfo", mLogContent);
        return new RgbInfo(
                Integer.parseInt(Integer.toHexString(Color.red(pixel)), 16),
                Integer.parseInt(Integer.toHexString(Color.green(pixel)), 16),
                Integer.parseInt(Integer.toHexString(Color.blue(pixel)), 16));
    }

    private static Bitmap invertBitmap(Bitmap bitmap) {
        int sWidth, sHeight, sRow, sCol, sPixel, sIndex;
        int sA, sR, sG, sB;

        int[] sPixels;
        sWidth = bitmap.getWidth();
        sHeight = bitmap.getHeight();
        sPixels = new int[sWidth * sHeight];
        bitmap.getPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight);
        for (sRow = 0; sRow < sHeight; sRow++) {
            sIndex = sRow * sWidth;
            for (sCol = 0; sCol < sWidth; sCol++) {
                sPixel = sPixels[sIndex];
                sA = (sPixel >> 24) & 0xff;
                sR = (sPixel >> 16) & 0xff;
                sG = (sPixel >> 8) & 0xff;
                sB = sPixel & 0xff;
                sR = 255 - sR;
                sG = 255 - sG;
                sB = 255 - sB;
                sPixel = ((sA & 0xff) << 24 | (sR & 0xff) << 16 | (sG & 0xff) << 8 | sB & 0xff);
                sPixels[sIndex] = sPixel;
                sIndex++;
            }
        }
        bitmap.setPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight);
        return bitmap;
    }

    private static Bitmap setGrayscale(Bitmap img) {
        Bitmap bitmap = img.copy(img.getConfig(), true);
        int c;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                c = bitmap.getPixel(i, j);
                byte gray = (byte) (.299 * Color.red(c) + .587 * Color.green(c) + .114 * Color.blue(c));
                bitmap.setPixel(i, j, Color.argb(255, gray, gray, gray));
            }
        }
        return bitmap;
    }
}