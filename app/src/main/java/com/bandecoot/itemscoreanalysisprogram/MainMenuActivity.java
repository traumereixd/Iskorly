package com.bandecoot.itemscoreanalysisprogram;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

/**
 * Main Menu for Iskorly UX 2.0
 * Provides navigation to all major features
 */
public class MainMenuActivity extends AppCompatActivity {
    
    private static final String TAG = "ISA_MENU";
    private static final String UX2_CHECK = "UX2_CHECK";
    
    private MaterialCardView cardStartScan, cardTutorial, cardCredits;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        
        // One-time UX 2.0 feature check log
        logUx2Features();
        
        // Initialize views
        cardStartScan = findViewById(R.id.card_start_scan);
        cardTutorial = findViewById(R.id.card_tutorial);
        cardCredits = findViewById(R.id.card_credits);
        
        // Start Scan - launches MainActivity normally without extras
        cardStartScan.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        
        // Tutorial
        cardTutorial.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            showTutorial();
        });

        // Credits
        cardCredits.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            showCredits();
        });
    }
    
    /**
     * Log UX 2.0 features enabled (one-time check)
     */
    private void logUx2Features() {
        if (getSharedPreferences("AppPrefs", MODE_PRIVATE).getBoolean("ux2_logged", false)) {
            return;
        }
        
        StringBuilder features = new StringBuilder("UX 2.0 Features:\n");
        features.append("- Splash Screen: Enabled\n");
        features.append("- Main Menu: Redesigned\n");
        features.append("- Color Palette: Indigo + Emerald\n");
        features.append("- Typography: Inter font\n");
        features.append("- Crop Library: CanHub Android Image Cropper\n");
        
        // Check if file_paths.xml is accessible
        try {
            int resId = getResources().getIdentifier("file_paths", "xml", getPackageName());
            features.append("- FileProvider paths: ").append(resId != 0 ? "OK" : "MISSING").append("\n");
        } catch (Exception e) {
            features.append("- FileProvider paths: ERROR\n");
        }
        
        Log.i(UX2_CHECK, features.toString());
        
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
            .edit()
            .putBoolean("ux2_logged", true)
            .apply();
    }

    private void showTutorial() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.tutorial_title))
                .setMessage(getString(R.string.tutorial_text))
                .setPositiveButton("OK", null)
                .show();
    }

    private void showCredits() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.menu_credits))
                .setMessage(getString(R.string.credits_text))
                .setPositiveButton("OK", null)
                .show();
    }
    
    @Override
    public void onBackPressed() {
        // Double back to exit pattern
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity(); // Exit app completely
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
