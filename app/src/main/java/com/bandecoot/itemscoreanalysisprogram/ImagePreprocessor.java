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
 * Includes adaptive preprocessing based on image quality analysis.
 */
public final class ImagePreprocessor {
    private static final String TAG = "ImagePreprocessor";
    
    private ImagePreprocessor() {}
    
    /**
     * Image quality metrics for adaptive preprocessing.
     */
    public static class ImageQuality {
        public final float brightness;      // 0-255, average brightness
        public final float contrast;        // 0-1, standard deviation of brightness
        public final boolean isBlurry;      // True if image appears blurry
        public final boolean isLowLight;    // True if image is underexposed
        public final boolean isHighLight;   // True if image is overexposed
        
        public ImageQuality(float brightness, float contrast, boolean isBlurry,
                          boolean isLowLight, boolean isHighLight) {
            this.brightness = brightness;
            this.contrast = contrast;
            this.isBlurry = isBlurry;
            this.isLowLight = isLowLight;
            this.isHighLight = isHighLight;
        }
        
        @Override
        public String toString() {
            return String.format("ImageQuality[bright=%.1f, contrast=%.3f, blur=%b, lowLight=%b, highLight=%b]",
                    brightness, contrast, isBlurry, isLowLight, isHighLight);
        }
    }
    
    /**
     * Analyze image quality to determine optimal preprocessing.
     * 
     * @param src Source bitmap
     * @return ImageQuality metrics
     */
    public static ImageQuality analyzeImageQuality(Bitmap src) {
        if (src == null) {
            return new ImageQuality(128, 0.5f, false, false, false);
        }
        
        int width = src.getWidth();
        int height = src.getHeight();
        
        // Sample pixels (don't process entire image for speed)
        int sampleStep = Math.max(1, width / 100); // Sample ~100 pixels wide
        int[] pixels = new int[(width / sampleStep) * (height / sampleStep)];
        int pixelIndex = 0;
        
        for (int y = 0; y < height; y += sampleStep) {
            for (int x = 0; x < width; x += sampleStep) {
                pixels[pixelIndex++] = src.getPixel(x, y);
            }
        }
        
        // Calculate brightness statistics
        float sumBrightness = 0;
        float[] brightnesses = new float[pixelIndex];
        
        for (int i = 0; i < pixelIndex; i++) {
            int pixel = pixels[i];
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;
            float brightness = (float)(0.299 * r + 0.587 * g + 0.114 * b);
            brightnesses[i] = brightness;
            sumBrightness += brightness;
        }
        
        float avgBrightness = sumBrightness / pixelIndex;
        
        // Calculate contrast (standard deviation)
        float sumSquaredDiff = 0;
        for (int i = 0; i < pixelIndex; i++) {
            float diff = brightnesses[i] - avgBrightness;
            sumSquaredDiff += diff * diff;
        }
        float contrast = (float) Math.sqrt(sumSquaredDiff / pixelIndex) / 255f;
        
        // Detect blur using edge detection (simplified Laplacian)
        boolean isBlurry = detectBlur(src, sampleStep);
        
        // Detect lighting conditions
        boolean isLowLight = avgBrightness < 80;
        boolean isHighLight = avgBrightness > 200;
        
        ImageQuality quality = new ImageQuality(avgBrightness, contrast, isBlurry, isLowLight, isHighLight);
        Log.d(TAG, "Image quality: " + quality);
        
        return quality;
    }
    
    /**
     * Detect if image is blurry using simplified edge detection.
     * 
     * @param src Source bitmap
     * @param sampleStep Sampling step for performance
     * @return True if image appears blurry
     */
    private static boolean detectBlur(Bitmap src, int sampleStep) {
        int width = src.getWidth();
        int height = src.getHeight();
        
        // Sample edge strength
        float sumEdgeStrength = 0;
        int edgeCount = 0;
        
        for (int y = sampleStep; y < height - sampleStep; y += sampleStep * 2) {
            for (int x = sampleStep; x < width - sampleStep; x += sampleStep * 2) {
                int center = src.getPixel(x, y);
                int right = src.getPixel(x + sampleStep, y);
                int bottom = src.getPixel(x, y + sampleStep);
                
                // Calculate brightness
                int cBright = getBrightness(center);
                int rBright = getBrightness(right);
                int bBright = getBrightness(bottom);
                
                // Edge strength (horizontal + vertical gradients)
                float edgeStrength = Math.abs(cBright - rBright) + Math.abs(cBright - bBright);
                sumEdgeStrength += edgeStrength;
                edgeCount++;
            }
        }
        
        float avgEdgeStrength = sumEdgeStrength / edgeCount;
        
        // Low average edge strength indicates blur
        boolean isBlurry = avgEdgeStrength < 20;
        
        Log.d(TAG, "Edge strength: " + avgEdgeStrength + " (blurry: " + isBlurry + ")");
        return isBlurry;
    }
    
    /**
     * Get brightness of a pixel.
     */
    private static int getBrightness(int pixel) {
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;
        return (int)(0.299 * r + 0.587 * g + 0.114 * b);
    }
    
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
        
        // Convert to grayscale intensities using standard luminance formula
        int[] gray = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            int color = pixels[i];
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            // Use standard luminance formula accounting for human eye sensitivity
            gray[i] = (int)(0.299 * r + 0.587 * g + 0.114 * b);
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
    
    /**
     * Ultra-high contrast preprocessing for faded or low-contrast images.
     * Applies aggressive contrast enhancement.
     * 
     * @param src Source bitmap
     * @return Ultra-high contrast bitmap
     */
    public static Bitmap preprocessUltraHighContrast(Bitmap src) {
        if (src == null) return null;
        
        Log.d(TAG, "Starting ultra-high contrast preprocessing");
        
        Bitmap gray = toGrayscale(src);
        if (gray == null) return null;
        
        Bitmap ultraContrast = applyUltraContrast(gray);
        gray.recycle();
        
        Log.d(TAG, "Ultra-high contrast preprocessing complete");
        return ultraContrast;
    }
    
    /**
     * Apply ultra-high contrast enhancement.
     * More aggressive than standard contrast enhancement.
     * 
     * @param src Source bitmap (preferably grayscale)
     * @return Bitmap with ultra-high contrast
     */
    private static Bitmap applyUltraContrast(Bitmap src) {
        if (src == null) return null;
        
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        
        // Ultra-aggressive contrast: scale values around midpoint with higher intensity
        ColorMatrix ultraContrast = new ColorMatrix(new float[]{
            2.0f, 0,    0,    0, -128,  // Red: double contrast
            0,    2.0f, 0,    0, -128,  // Green: double contrast
            0,    0,    2.0f, 0, -128,  // Blue: double contrast
            0,    0,    0,    1, 0      // Alpha: unchanged
        });
        
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ultraContrast));
        canvas.drawBitmap(src, 0, 0, paint);
        
        Log.d(TAG, "Applied ultra-high contrast");
        return result;
    }
    
    /**
     * Sharpen filter to enhance edges and improve text clarity.
     * Useful for slightly blurry images.
     * 
     * @param src Source bitmap
     * @return Sharpened bitmap
     */
    public static Bitmap preprocessSharpened(Bitmap src) {
        if (src == null) return null;
        
        Log.d(TAG, "Starting sharpened preprocessing");
        
        Bitmap gray = toGrayscale(src);
        if (gray == null) return null;
        
        Bitmap sharpened = applySharpen(gray);
        gray.recycle();
        
        if (sharpened == null) return gray;
        
        Bitmap contrasted = enhanceContrast(sharpened);
        sharpened.recycle();
        
        Log.d(TAG, "Sharpened preprocessing complete");
        return contrasted;
    }
    
    /**
     * Apply sharpening filter using convolution.
     * Enhances edges to make text more readable.
     * 
     * @param src Source bitmap
     * @return Sharpened bitmap
     */
    private static Bitmap applySharpen(Bitmap src) {
        if (src == null) return null;
        
        int width = src.getWidth();
        int height = src.getHeight();
        
        // Get pixels
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        
        // Sharpen kernel (3x3):
        // [ 0, -1,  0]
        // [-1,  5, -1]
        // [ 0, -1,  0]
        int[] output = new int[pixels.length];
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int idx = y * width + x;
                
                // Get center and neighbors
                int center = pixels[idx];
                int top = pixels[(y - 1) * width + x];
                int bottom = pixels[(y + 1) * width + x];
                int left = pixels[y * width + (x - 1)];
                int right = pixels[y * width + (x + 1)];
                
                // Extract grayscale values
                int cR = (center >> 16) & 0xFF;
                int tR = (top >> 16) & 0xFF;
                int bR = (bottom >> 16) & 0xFF;
                int lR = (left >> 16) & 0xFF;
                int rR = (right >> 16) & 0xFF;
                
                // Apply sharpen kernel
                int newR = 5 * cR - tR - bR - lR - rR;
                newR = Math.max(0, Math.min(255, newR));
                
                // Create grayscale pixel
                output[idx] = 0xFF000000 | (newR << 16) | (newR << 8) | newR;
            }
        }
        
        // Handle borders (copy original)
        for (int x = 0; x < width; x++) {
            output[x] = pixels[x]; // Top row
            output[(height - 1) * width + x] = pixels[(height - 1) * width + x]; // Bottom row
        }
        for (int y = 0; y < height; y++) {
            output[y * width] = pixels[y * width]; // Left column
            output[y * width + (width - 1)] = pixels[y * width + (width - 1)]; // Right column
        }
        
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(output, 0, width, 0, 0, width, height);
        
        Log.d(TAG, "Applied sharpening filter");
        return result;
    }
    
    /**
     * Adaptive histogram equalization for uneven lighting conditions.
     * Improves local contrast across the image.
     * 
     * @param src Source bitmap
     * @return Histogram-equalized bitmap
     */
    public static Bitmap preprocessAdaptiveHistogram(Bitmap src) {
        if (src == null) return null;
        
        Log.d(TAG, "Starting adaptive histogram preprocessing");
        
        Bitmap gray = toGrayscale(src);
        if (gray == null) return null;
        
        Bitmap equalized = applyHistogramEqualization(gray);
        gray.recycle();
        
        if (equalized == null) return gray;
        
        Bitmap contrasted = enhanceContrast(equalized);
        equalized.recycle();
        
        Log.d(TAG, "Adaptive histogram preprocessing complete");
        return contrasted;
    }
    
    /**
     * Apply histogram equalization to improve contrast.
     * Uses cumulative distribution function to redistribute pixel intensities.
     * 
     * @param src Source bitmap (preferably grayscale)
     * @return Equalized bitmap
     */
    private static Bitmap applyHistogramEqualization(Bitmap src) {
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
            gray[i] = (int)(0.299 * r + 0.587 * g + 0.114 * b);
        }
        
        // Build histogram
        int[] histogram = new int[256];
        for (int value : gray) {
            histogram[value]++;
        }
        
        // Calculate cumulative distribution function (CDF)
        int[] cdf = new int[256];
        cdf[0] = histogram[0];
        for (int i = 1; i < 256; i++) {
            cdf[i] = cdf[i - 1] + histogram[i];
        }
        
        // Find minimum non-zero CDF value
        int cdfMin = 0;
        for (int i = 0; i < 256; i++) {
            if (cdf[i] > 0) {
                cdfMin = cdf[i];
                break;
            }
        }
        
        // Equalize using CDF
        int totalPixels = pixels.length;
        int[] equalized = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            int oldValue = gray[i];
            int newValue = (int) ((cdf[oldValue] - cdfMin) * 255.0 / (totalPixels - cdfMin));
            newValue = Math.max(0, Math.min(255, newValue));
            equalized[i] = 0xFF000000 | (newValue << 16) | (newValue << 8) | newValue;
        }
        
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(equalized, 0, width, 0, 0, width, height);
        
        Log.d(TAG, "Applied histogram equalization");
        return result;
    }
}
