package com.bandecoot.itemscoreanalysisprogram;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity; /**
 * Simple launcher menu: Start / Tutorial / Credits
 * Keeps existing MainActivity flow intact.
 */
public class MainMenuActivity extends AppCompatActivity {

    private Button startBtn, tutorialBtn, creditsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        startBtn = findViewById(R.id.button_main_start);
        tutorialBtn = findViewById(R.id.button_main_tutorial);
        creditsBtn = findViewById(R.id.button_main_credits);

        startBtn.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            startActivity(new Intent(this, MainActivity.class));
        });

        tutorialBtn.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            showTutorial();
        });

        creditsBtn.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            showCredits();
        });
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
}
