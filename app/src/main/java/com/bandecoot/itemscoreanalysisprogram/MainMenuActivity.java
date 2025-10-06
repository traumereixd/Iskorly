package com.bandecoot.itemscoreanalysisprogram;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main Menu launcher activity for v1.3.
 * Provides entry points: Start (launches MainActivity), Tutorial, and Credits.
 */
public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        TextView titleTextView = findViewById(R.id.main_menu_title);
        TextView taglineTextView = findViewById(R.id.main_menu_tagline);
        Button startButton = findViewById(R.id.btn_main_start);
        Button tutorialButton = findViewById(R.id.btn_main_tutorial);
        Button creditsButton = findViewById(R.id.btn_main_credits);

        // Start button launches MainActivity
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Tutorial button shows dialog with tutorial text
        tutorialButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tutorial_title)
                    .setMessage(R.string.tutorial_text)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        });

        // Credits button shows dialog with credits text
        creditsButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.menu_credits)
                    .setMessage(R.string.credits_text)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        });
    }
}
