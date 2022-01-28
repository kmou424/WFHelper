package moe.kmou424.WorldFlipper.Helper.Info;

import android.graphics.Point;

public class BitmapInfo {
    public final Point point;
    public final int width, height;

    public BitmapInfo(int x, int y, int width, int height) {
        this.point = new Point(x, y);
        this.width = width;
        this.height = height;
    }
}
