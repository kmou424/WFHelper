package moe.kmou424.WorldFlipper.Helper.Info;

public class ColorInfo {
    public final RgbInfo left, right;

    public ColorInfo(RgbInfo left, RgbInfo right) {
        this.left = left;
        this.right = right;
    }

    public boolean check(RgbInfo mRgb) {
        if (mRgb == null) return false;
        return (mRgb.red >= left.red && mRgb.red <= right.red) &&
                (mRgb.green >= left.green && mRgb.green <= right.green) &&
                (mRgb.blue >= left.blue && mRgb.blue <= right.blue);
    }
}
