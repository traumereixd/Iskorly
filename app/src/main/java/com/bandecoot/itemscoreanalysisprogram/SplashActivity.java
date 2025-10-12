package com.bandecoot.itemscoreanalysisprogram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.RawResourceDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

/**
 * Splash screen for Iskorly UX 2.0
 * Shows animated splash video then navigates to MainMenuActivity
 */
@UnstableApi
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int GUARD_TIMEOUT_MS = 6000; // 6 seconds max
    
    private ExoPlayer player;
    private PlayerView playerView;
    private Handler guardHandler;
    private Runnable guardRunnable;
    private boolean hasNavigated = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        playerView = findViewById(R.id.player_view);
        initializePlayer();
    }
    
    private void initializePlayer() {
        try {
            // Create ExoPlayer instance
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            
            // Build URI for raw resource
            Uri videoUri = RawResourceDataSource.buildRawResourceUri(R.raw.iskorly_splash);
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            
            // Mute audio
            player.setVolume(0f);
            
            // Set up listener for playback state changes
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        Log.d(TAG, "Video playback ended");
                        navigateToMainMenu();
                    }
                }
                
                @Override
                public void onPlayerError(androidx.media3.common.PlaybackException error) {
                    Log.e(TAG, "Playback error, advancing to main menu", error);
                    navigateToMainMenu();
                }
            });
            
            // Prepare and play
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
            
            // Set up guard timeout to ensure we navigate even if playback stalls
            guardHandler = new Handler(Looper.getMainLooper());
            guardRunnable = this::navigateToMainMenu;
            guardHandler.postDelayed(guardRunnable, GUARD_TIMEOUT_MS);
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing player, advancing to main menu", e);
            navigateToMainMenu();
        }
    }
    
    private void navigateToMainMenu() {
        if (hasNavigated) {
            return; // Prevent multiple navigation attempts
        }
        hasNavigated = true;
        
        // Cancel guard timeout
        if (guardHandler != null && guardRunnable != null) {
            guardHandler.removeCallbacks(guardRunnable);
        }
        
        // Navigate to main menu
        Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
    
    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
        if (guardHandler != null && guardRunnable != null) {
            guardHandler.removeCallbacks(guardRunnable);
        }
    }
}
