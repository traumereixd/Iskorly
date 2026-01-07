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
     * High quality resize and compress for OCR.
     * Uses higher JPEG quality (95%) and allows larger images (2048px).
     * This preserves more detail for poor quality camera images.
     * 
     * @param src Source bitmap
     * @param maxDim Maximum dimension (width or height)
     * @return JPEG-compressed bytes at 95% quality
     */
    public static byte[] resizeAndCompressHighQuality(Bitmap src, int maxDim) {
        if (src == null) return new byte[0];
        int w = src.getWidth(), h = src.getHeight();
        float scale = Math.min(1f, maxDim / (float)Math.max(w, h));
        int nw = Math.round(w * scale), nh = Math.round(h * scale);
        Bitmap scaled = (scale < 1f) ? Bitmap.createScaledBitmap(src, nw, nh, true) : src;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 95, out); // 95% quality instead of 92%
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
    
    /**
     * Phase 2 Feature #3: Apply adaptive thresholding for high-contrast OCR mode.
     * Uses a simple threshold approach to convert to binary black/white image.
     * This improves handwriting extraction in challenging conditions.
     * 
     * @param src Source bitmap
     * @return Thresholded bitmap with high contrast
     */
    public static Bitmap thresholdForOcr(Bitmap src) {
        if (src == null) return null;
        
        int width = src.getWidth();
        int height = src.getHeight();
        
        // Create a mutable copy
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        
        // First convert to grayscale and calculate mean brightness
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        
        long sumBrightness = 0;
        for (int pixel : pixels) {
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;
            int gray = (r + g + b) / 3;
            sumBrightness += gray;
        }
        
        // Calculate threshold (slightly below mean to preserve dark text)
        int threshold = (int) (sumBrightness / pixels.length * 0.85);
        threshold = Math.max(80, Math.min(180, threshold)); // Clamp between 80-180
        
        // Apply threshold: pixels darker than threshold -> black, else -> white
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;
            int gray = (r + g + b) / 3;
            
            // If darker than threshold, set to black; otherwise white
            int newColor = (gray < threshold) ? 0xFF000000 : 0xFFFFFFFF;
            pixels[i] = newColor;
        }
        
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }
    
    /**
     * Phase 2 Feature #3: Split image vertically into left and right halves.
     * Used for two-column OCR to avoid cross-column text confusion.
     * 
     * @param src Source bitmap
     * @return Array of two bitmaps [leftHalf, rightHalf]
     */
    public static Bitmap[] splitImageVertically(Bitmap src) {
        if (src == null) return new Bitmap[]{null, null};
        
        int width = src.getWidth();
        int height = src.getHeight();
        int halfWidth = width / 2;
        
        // Create left half (0 to halfWidth)
        Bitmap leftHalf = Bitmap.createBitmap(src, 0, 0, halfWidth, height);
        
        // Create right half (halfWidth to width)
        Bitmap rightHalf = Bitmap.createBitmap(src, halfWidth, 0, width - halfWidth, height);
        
        return new Bitmap[]{leftHalf, rightHalf};
    }
}
