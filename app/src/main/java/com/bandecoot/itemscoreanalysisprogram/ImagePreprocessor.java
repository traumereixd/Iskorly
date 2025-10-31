package com.bandecoot.itemscoreanalysisprogram;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

/**
 * Image preprocessing utilities for OCR optimization.
 * Provides de-yellowing, grayscale conversion, contrast enhancement, and Otsu binarization.
 */
public final class ImagePreprocessor {
    private static final String TAG = "ImagePreprocessor";
    
    private ImagePreprocessor() {}
    
    /**
     * Apply de-yellow filter to neutralize yellow pad backgrounds.
     * Reduces yellow tint to improve text contrast for OCR.
     * 
     * @param src Source bitmap
     * @return Bitmap with reduced yellow tint
     */
    public static Bitmap deYellow(Bitmap src) {
        if (src == null) return null;
        
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        
        // Color matrix to reduce yellow: reduce red and green slightly, keep blue
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
            0.85f, 0,     0,     0, 0,   // Red: reduce slightly
            0,     0.85f, 0,     0, 0,   // Green: reduce slightly
            0,     0,     1.2f,  0, 0,   // Blue: boost slightly
            0,     0,     0,     1, 0    // Alpha: unchanged
        });
        
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(src, 0, 0, paint);
        
        Log.d(TAG, "Applied de-yellow filter");
        return result;
    }
    
    /**
     * Convert image to grayscale.
     * 
     * @param src Source bitmap
     * @return Grayscale bitmap
     */
    public static Bitmap toGrayscale(Bitmap src) {
        if (src == null) return null;
        
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        
        ColorMatrix grayscale = new ColorMatrix();
        grayscale.setSaturation(0);
        
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(grayscale));
        canvas.drawBitmap(src, 0, 0, paint);
        
        Log.d(TAG, "Converted to grayscale");
        return result;
    }
    
    /**
     * Enhance contrast for better text visibility.
     * 
     * @param src Source bitmap
     * @return Bitmap with enhanced contrast
     */
    public static Bitmap enhanceContrast(Bitmap src) {
        if (src == null) return null;
        
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        
        // Increase contrast by scaling color values around midpoint
        ColorMatrix contrast = new ColorMatrix(new float[]{
            1.5f, 0,    0,    0, -64,  // Red
            0,    1.5f, 0,    0, -64,  // Green
            0,    0,    1.5f, 0, -64,  // Blue
            0,    0,    0,    1, 0     // Alpha
        });
        
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(contrast));
        canvas.drawBitmap(src, 0, 0, paint);
        
        Log.d(TAG, "Enhanced contrast");
        return result;
    }
    
    /**
     * Apply Otsu's binarization algorithm to create black and white image.
     * Automatically calculates optimal threshold for text separation.
     * 
     * @param src Source bitmap (preferably grayscale)
     * @return Binary (black and white) bitmap
     */
    public static Bitmap otsuBinarization(Bitmap src) {
        if (src == null) return null;
        
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        
        // Convert to grayscale intensities
        int[] gray = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            int color = pixels[i];
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            gray[i] = (r + g + b) / 3;
        }
        
        // Build histogram
        int[] histogram = new int[256];
        for (int value : gray) {
            histogram[value]++;
        }
        
        // Calculate Otsu threshold
        int threshold = calculateOtsuThreshold(histogram, pixels.length);
        Log.d(TAG, "Otsu threshold calculated: " + threshold);
        
        // Apply threshold
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (gray[i] < threshold) ? Color.BLACK : Color.WHITE;
        }
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        
        Log.d(TAG, "Applied Otsu binarization");
        return result;
    }
    
    /**
     * Calculate optimal threshold using Otsu's method.
     * Maximizes inter-class variance to find best separation point.
     */
    private static int calculateOtsuThreshold(int[] histogram, int totalPixels) {
        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }
        
        float sumB = 0;
        int wB = 0;
        int wF;
        float varMax = 0;
        int threshold = 0;
        
        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0) continue;
            
            wF = totalPixels - wB;
            if (wF == 0) break;
            
            sumB += i * histogram[i];
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;
            
            // Calculate between-class variance
            float varBetween = (float) wB * wF * (mB - mF) * (mB - mF);
            
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }
        
        return threshold;
    }
    
    /**
     * Full preprocessing pipeline for classroom pages:
     * 1. De-yellow filter
     * 2. Grayscale conversion
     * 3. Contrast enhancement
     * 4. Otsu binarization
     * 
     * @param src Source bitmap
     * @return Fully preprocessed bitmap optimized for OCR
     */
    public static Bitmap preprocessForClassroom(Bitmap src) {
        if (src == null) return null;
        
        Log.d(TAG, "Starting classroom preprocessing pipeline");
        
        // Step 1: De-yellow
        Bitmap deYellowed = deYellow(src);
        if (deYellowed == null) return null;
        
        // Step 2: Grayscale
        Bitmap gray = toGrayscale(deYellowed);
        if (deYellowed != src) deYellowed.recycle();
        if (gray == null) return null;
        
        // Step 3: Enhance contrast
        Bitmap contrasted = enhanceContrast(gray);
        gray.recycle();
        if (contrasted == null) return null;
        
        // Step 4: Otsu binarization
        Bitmap binarized = otsuBinarization(contrasted);
        contrasted.recycle();
        
        Log.d(TAG, "Classroom preprocessing complete");
        return binarized;
    }
    
    /**
     * Light preprocessing variant without binarization.
     * Good for already-clear images.
     * 
     * @param src Source bitmap
     * @return Preprocessed bitmap
     */
    public static Bitmap preprocessLight(Bitmap src) {
        if (src == null) return null;
        
        Log.d(TAG, "Starting light preprocessing");
        
        Bitmap gray = toGrayscale(src);
        if (gray == null) return null;
        
        Bitmap contrasted = enhanceContrast(gray);
        gray.recycle();
        
        Log.d(TAG, "Light preprocessing complete");
        return contrasted;
    }
}
