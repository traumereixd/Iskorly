package com.bandecoot.itemscoreanalysisprogram;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * SimpleCropActivity - Enhanced custom crop UI with rotate, flip, and aspect ratio controls.
 * 
 * Features:
 * - Rotate right/left (90° increments)
 * - Flip horizontal
 * - Aspect ratio presets (Free, 4:3, 1:1)
 * - Rotation angle display
 * - Dark theme support
 * - Haptic feedback
 * - Performance safeguards (bitmap recycling, OOM handling)
 * - Accessibility (content descriptions, 56dp touch targets)
 */
public class SimpleCropActivity extends AppCompatActivity {
    
    private static final String TAG = "SimpleCropActivity";
    private static final String EXTRA_IMAGE_URI = "image_uri";
    private static final String EXTRA_OUTPUT_URI = "output_uri";
    private static final int MAX_OUTPUT_SIZE = 2048; // Max dimension to prevent OOM
    
    private CropImageView cropImageView;
    private TextView rotationAngleText;
    private ImageButton rotateLeftButton;
    private ImageButton rotateRightButton;
    private ImageButton flipHorizontalButton;
    private ImageButton cancelButton;
    private ImageButton confirmButton;
    private Button aspectRatioFree;
    private Button aspectRatio4_3;
    private Button aspectRatio1_1;
    
    private Uri sourceUri;
    private Uri outputUri;
    private int currentRotation = 0; // Track rotation angle: 0, 90, 180, 270
    private boolean isFlipped = false; // Track horizontal flip state
    private Bitmap sourceBitmap;
    
    /**
     * Create intent to launch SimpleCropActivity
     */
    public static Intent createIntent(Activity context, Uri sourceUri, Uri outputUri) {
        Intent intent = new Intent(context, SimpleCropActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, sourceUri.toString());
        intent.putExtra(EXTRA_OUTPUT_URI, outputUri.toString());
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_crop);
        
        // Get URIs from intent
        Intent intent = getIntent();
        String sourceUriString = intent.getStringExtra(EXTRA_IMAGE_URI);
        String outputUriString = intent.getStringExtra(EXTRA_OUTPUT_URI);
        
        if (sourceUriString == null || outputUriString == null) {
            Log.e(TAG, "Missing source or output URI");
            Toast.makeText(this, "Error: Invalid crop parameters", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        
        sourceUri = Uri.parse(sourceUriString);
        outputUri = Uri.parse(outputUriString);
        
        // Initialize views
        cropImageView = findViewById(R.id.cropImageView);
        rotationAngleText = findViewById(R.id.rotationAngleText);
        rotateLeftButton = findViewById(R.id.rotateLeftButton);
        rotateRightButton = findViewById(R.id.rotateRightButton);
        flipHorizontalButton = findViewById(R.id.flipHorizontalButton);
        cancelButton = findViewById(R.id.cancelButton);
        confirmButton = findViewById(R.id.confirmButton);
        aspectRatioFree = findViewById(R.id.aspectRatioFree);
        aspectRatio4_3 = findViewById(R.id.aspectRatio4_3);
        aspectRatio1_1 = findViewById(R.id.aspectRatio1_1);
        
        // Load image into crop view
        loadImage();
        
        // Setup listeners
        setupListeners();
        
        // Set default aspect ratio (Free)
        setAspectRatioFree();
    }
    
    private void loadImage() {
        try {
            // Load with downsampling to prevent OOM
            InputStream is = getContentResolver().openInputStream(sourceUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            if (is != null) is.close();
            
            // Calculate sample size
            int maxDim = Math.max(options.outWidth, options.outHeight);
            int sampleSize = 1;
            while (maxDim / sampleSize > MAX_OUTPUT_SIZE * 2) {
                sampleSize *= 2;
            }
            
            // Decode with sample size
            is = getContentResolver().openInputStream(sourceUri);
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            sourceBitmap = BitmapFactory.decodeStream(is, null, options);
            if (is != null) is.close();
            
            if (sourceBitmap == null) {
                throw new Exception("Failed to decode bitmap");
            }
            
            cropImageView.setImageBitmap(sourceBitmap);
            cropImageView.setGuidelines(CropImageView.Guidelines.ON);
            cropImageView.setAutoZoomEnabled(true);
            
            Log.d(TAG, "Image loaded successfully: " + options.outWidth + "x" + options.outHeight + 
                  ", sampleSize=" + sampleSize);
            
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Out of memory loading image", e);
            handleOutOfMemory();
        } catch (Exception e) {
            Log.e(TAG, "Error loading image", e);
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    
    private void handleOutOfMemory() {
        // Try again with higher sample size
        try {
            if (sourceBitmap != null) {
                sourceBitmap.recycle();
                sourceBitmap = null;
            }
            
            InputStream is = getContentResolver().openInputStream(sourceUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4; // More aggressive downsampling
            sourceBitmap = BitmapFactory.decodeStream(is, null, options);
            if (is != null) is.close();
            
            if (sourceBitmap != null) {
                cropImageView.setImageBitmap(sourceBitmap);
                Toast.makeText(this, "Image downscaled to prevent memory issues", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Recovered from OOM with sample size 4");
            } else {
                throw new Exception("Failed to decode with fallback");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to recover from OOM", e);
            Toast.makeText(this, "Image too large to process", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    
    private void setupListeners() {
        rotateLeftButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            rotateLeft();
        });
        
        rotateRightButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            rotateRight();
        });
        
        flipHorizontalButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            flipHorizontal();
        });
        
        cancelButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            cancel();
        });
        
        confirmButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            confirm();
        });
        
        aspectRatioFree.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            setAspectRatioFree();
        });
        
        aspectRatio4_3.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            setAspectRatio4_3();
        });
        
        aspectRatio1_1.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            setAspectRatio1_1();
        });
    }
    
    private void rotateLeft() {
        currentRotation = (currentRotation - 90 + 360) % 360;
        cropImageView.rotateImage(-90);
        updateRotationDisplay();
        Log.d(TAG, "Rotated left, current angle: " + currentRotation);
    }
    
    private void rotateRight() {
        currentRotation = (currentRotation + 90) % 360;
        cropImageView.rotateImage(90);
        updateRotationDisplay();
        Log.d(TAG, "Rotated right, current angle: " + currentRotation);
    }
    
    private void flipHorizontal() {
        isFlipped = !isFlipped;
        cropImageView.flipImageHorizontally();
        Log.d(TAG, "Flipped horizontal, flipped state: " + isFlipped);
    }
    
    private void updateRotationDisplay() {
        rotationAngleText.setText(currentRotation + "°");
    }
    
    private void setAspectRatioFree() {
        cropImageView.clearAspectRatio();
        highlightAspectButton(aspectRatioFree);
        Log.d(TAG, "Aspect ratio set to Free");
    }
    
    private void setAspectRatio4_3() {
        cropImageView.setAspectRatio(4, 3);
        highlightAspectButton(aspectRatio4_3);
        Log.d(TAG, "Aspect ratio set to 4:3");
    }
    
    private void setAspectRatio1_1() {
        cropImageView.setAspectRatio(1, 1);
        highlightAspectButton(aspectRatio1_1);
        Log.d(TAG, "Aspect ratio set to 1:1");
    }
    
    private void highlightAspectButton(Button selectedButton) {
        // Reset all buttons
        aspectRatioFree.setPressed(false);
        aspectRatio4_3.setPressed(false);
        aspectRatio1_1.setPressed(false);
        
        // Highlight selected
        selectedButton.setPressed(true);
    }
    
    private void cancel() {
        Log.d(TAG, "Crop canceled by user");
        cleanup();
        setResult(RESULT_CANCELED);
        finish();
    }
    
    private void confirm() {
        Log.d(TAG, "Confirm crop");
        Toast.makeText(this, "Processing crop...", Toast.LENGTH_SHORT).show();
        
        new Thread(() -> {
            Bitmap croppedBitmap = null;
            FileOutputStream fos = null;
            
            try {
                // Get cropped bitmap
                croppedBitmap = cropImageView.getCroppedImage();
                
                if (croppedBitmap == null) {
                    throw new Exception("Failed to get cropped image");
                }
                
                // Apply rotation and flip transformations
                croppedBitmap = applyTransformations(croppedBitmap);
                
                // Ensure output doesn't exceed max size
                if (croppedBitmap.getWidth() > MAX_OUTPUT_SIZE || croppedBitmap.getHeight() > MAX_OUTPUT_SIZE) {
                    croppedBitmap = downscaleBitmap(croppedBitmap, MAX_OUTPUT_SIZE);
                }
                
                // Save to output URI
                File outputFile = new File(outputUri.getPath());
                fos = new FileOutputStream(outputFile);
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                
                Log.d(TAG, "Cropped image saved to: " + outputUri);
                
                runOnUiThread(() -> {
                    Intent resultIntent = new Intent();
                    resultIntent.setData(outputUri);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
                
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "Out of memory during crop", e);
                runOnUiThread(() -> {
                    Toast.makeText(SimpleCropActivity.this, "Image too large, using fallback", Toast.LENGTH_SHORT).show();
                    // Return original URI as fallback
                    Intent resultIntent = new Intent();
                    resultIntent.setData(sourceUri);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error saving cropped image", e);
                runOnUiThread(() -> {
                    Toast.makeText(SimpleCropActivity.this, "Crop failed", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                });
            } finally {
                if (croppedBitmap != null) {
                    croppedBitmap.recycle();
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }).start();
    }
    
    private Bitmap applyTransformations(Bitmap bitmap) {
        // Note: CropImageView already handles rotation and flip internally
        // This method is kept for reference and future custom transformations
        return bitmap;
    }
    
    private Bitmap downscaleBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        float scale = Math.min(
            (float) maxSize / width,
            (float) maxSize / height
        );
        
        if (scale >= 1.0f) {
            return bitmap;
        }
        
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);
        
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        if (scaled != bitmap) {
            bitmap.recycle();
        }
        
        Log.d(TAG, "Downscaled bitmap from " + width + "x" + height + " to " + newWidth + "x" + newHeight);
        return scaled;
    }
    
    private void cleanup() {
        if (sourceBitmap != null && !sourceBitmap.isRecycled()) {
            sourceBitmap.recycle();
            sourceBitmap = null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanup();
        Log.d(TAG, "SimpleCropActivity destroyed");
    }
}
