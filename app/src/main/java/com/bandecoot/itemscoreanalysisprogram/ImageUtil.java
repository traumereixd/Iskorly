package com.bandecoot.itemscoreanalysisprogram;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public final class ImageUtil {
    private ImageUtil() {}

    // Resize the bitmap so that the longer side is <= maxDim, then JPEG-compress.
    public static byte[] resizeAndCompress(Bitmap src, int maxDim) {
        if (src == null) return new byte[0];
        int w = src.getWidth(), h = src.getHeight();
        float scale = Math.min(1f, maxDim / (float)Math.max(w, h));
        int nw = Math.round(w * scale), nh = Math.round(h * scale);
        Bitmap scaled = (scale < 1f) ? Bitmap.createScaledBitmap(src, nw, nh, true) : src;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 92, out);
        if (scaled != src) scaled.recycle();
        return out.toByteArray();
    }
}