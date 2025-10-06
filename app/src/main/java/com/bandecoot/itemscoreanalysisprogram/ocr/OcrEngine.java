package com.bandecoot.itemscoreanalysisprogram.ocr;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Simple OCR engine API used by MainActivity.
 */
public interface OcrEngine {
    String name();
    void recognize(Context ctx, Bitmap bitmap, Callback cb);
    void close();

    interface Callback {
        void onResult(String text);
        void onError(Exception e);
    }
}