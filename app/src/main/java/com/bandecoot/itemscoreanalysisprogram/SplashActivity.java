package com.bandecoot.itemscoreanalysisprogram;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash screen for Iskorly UX 2.0
 * Shows animated video then navigates to MainMenuActivity
 */
public class SplashActivity extends AppCompatActivity {
    
    private static final String TAG = "SplashActivity";
    private static final int TIMEOUT_MS = 5000; // 5 second timeout
    private VideoView videoView;
    private Handler timeoutHandler;
    private boolean hasNavigated = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        videoView = findViewById(R.id.videoSplash);
        timeoutHandler = new Handler(Looper.getMainLooper());
        
        // Set up timeout guard
        timeoutHandler.postDelayed(this::navigateToMainMenu, TIMEOUT_MS);
        
        try {
            // Load video from raw resources
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.iskorly_splash);
            videoView.setVideoURI(videoUri);
            
            videoView.setOnPreparedListener(mp -> {
                // Mute audio
                mp.setVolume(0f, 0f);
                // Start playback
                mp.start();
            });
            
            videoView.setOnCompletionListener(mp -> navigateToMainMenu());
            
            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Video playback error: what=" + what + ", extra=" + extra);
                navigateToMainMenu();
                return true;
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to load video", e);
            navigateToMainMenu();
        }
    }
    
    private void navigateToMainMenu() {
        if (hasNavigated) {
            return; // Already navigated
        }
        hasNavigated = true;
        
        // Cancel timeout
        timeoutHandler.removeCallbacksAndMessages(null);
        
        Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeoutHandler != null) {
            timeoutHandler.removeCallbacksAndMessages(null);
        }
    }
}
