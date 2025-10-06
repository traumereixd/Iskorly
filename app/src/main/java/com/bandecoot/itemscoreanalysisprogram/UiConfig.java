package com.bandecoot.itemscoreanalysisprogram;

/**
 * UI Configuration constants for Iskorly UX 2.0
 */
public final class UiConfig {
    private UiConfig() {} // Prevent instantiation
    
    /**
     * Enable/disable uCrop interactive cropping.
     * Set to false for debugging or to force fallback auto-crop.
     */
    public static final boolean ENABLE_UCROP = true;
    
    /**
     * Splash screen display duration in milliseconds.
     */
    public static final int SPLASH_DELAY_MS = 800;
    
    /**
     * Logging tag for crop fix debugging
     */
    public static final String CROP_FIX = "CROP_FIX";
}
