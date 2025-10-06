package com.bandecoot.itemscoreanalysisprogram;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button btnStart = findViewById(R.id.btn_main_start);
        Button btnTutorial = findViewById(R.id.btn_main_tutorial);
        Button btnCredits = findViewById(R.id.btn_main_credits);

        // Start button - launch MainActivity
        btnStart.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // Tutorial button - show tutorial dialog
        btnTutorial.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            showTutorialDialog();
        });

        // Credits button - show credits dialog
        btnCredits.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            showCreditsDialog();
        });
    }

    private void showTutorialDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tutorial_title)
                .setMessage(R.string.tutorial_text)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showCreditsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_credits)
                .setMessage(R.string.credits_text)
                .setPositiveButton("OK", null)
                .show();
    }
}
