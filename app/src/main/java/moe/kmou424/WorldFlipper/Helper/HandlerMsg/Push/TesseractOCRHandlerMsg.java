package moe.kmou424.WorldFlipper.Helper.HandlerMsg.Push;

import moe.kmou424.WorldFlipper.Helper.Tools.TesseractOCR;

public class TesseractOCRHandlerMsg {
    private final TesseractOCR mTesseractOCR;

    public TesseractOCRHandlerMsg(TesseractOCR mTesseractOCR) {
        this.mTesseractOCR = mTesseractOCR;
    }

    public TesseractOCR getTesseractOCR() {
        return this.mTesseractOCR;
    }
}
