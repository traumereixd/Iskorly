package com.bandecoot.itemscoreanalysisprogram;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash screen for Iskorly UX 2.0
 * Shows branding then navigates to MainMenuActivity
 */
public class SplashActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // For Android 12+, splash screen API handles the display
        // For older versions, this will show briefly before navigating
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }, UiConfig.SPLASH_DELAY_MS);
    }
}
