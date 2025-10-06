package com.bandecoot.itemscoreanalysisprogram;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    
    /**
     * Feature #3: Enhance image for OCR - convert to grayscale and increase contrast
     * to improve handwriting recognition.
     * 
     * @param src Source bitmap
     * @return Enhanced bitmap suitable for OCR
     */
    public static Bitmap enhanceForOcr(Bitmap src) {
        if (src == null) return null;
        
        // Create a mutable copy
        Bitmap enhanced = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(enhanced);
        
        // Create grayscale + contrast enhancement color matrix
        ColorMatrix grayscale = new ColorMatrix();
        grayscale.setSaturation(0); // Convert to grayscale
        
        // Increase contrast (adjust values to emphasize dark text)
        ColorMatrix contrast = new ColorMatrix(new float[]{
            1.5f, 0,    0,    0, -64,  // Red
            0,    1.5f, 0,    0, -64,  // Green
            0,    0,    1.5f, 0, -64,  // Blue
            0,    0,    0,    1, 0     // Alpha
        });
        
        // Combine matrices
        grayscale.postConcat(contrast);
        
        // Apply to paint and draw
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(grayscale));
        canvas.drawBitmap(src, 0, 0, paint);
        
        return enhanced;
    }
}
