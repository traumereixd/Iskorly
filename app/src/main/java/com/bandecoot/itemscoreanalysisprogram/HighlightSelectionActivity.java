package com.bandecoot.itemscoreanalysisprogram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class HighlightSelectionActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URI = "image_uri";
    public static final String EXTRA_ALLOW_RETAKE = "allow_retake";
    public static final int RESULT_RETAKE = RESULT_FIRST_USER;
    private static final String TAG = "HighlightSelection";
    private static final int OUTPUT_QUALITY = 95;

    private ImageView imageView;
    private HighlightOverlayView overlayView;
    private Button cancelButton;
    private Button retakeButton;
    private Button confirmButton;
    private Uri sourceUri;
    private Bitmap previewBitmap;
    private int sourceWidth;
    private int sourceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlight_selection);

        imageView = findViewById(R.id.highlightImageView);
        overlayView = findViewById(R.id.highlightOverlay);
        cancelButton = findViewById(R.id.button_highlight_cancel);
        retakeButton = findViewById(R.id.button_highlight_retake);
        confirmButton = findViewById(R.id.button_highlight_confirm);

        Intent intent = getIntent();
        String sourceUriString = intent.getStringExtra(EXTRA_IMAGE_URI);
        boolean allowRetake = intent.getBooleanExtra(EXTRA_ALLOW_RETAKE, true);

        if (sourceUriString == null) {
            Toast.makeText(this, "Missing image", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        sourceUri = Uri.parse(sourceUriString);
        if (!allowRetake && retakeButton != null) {
            retakeButton.setVisibility(android.view.View.GONE);
        }

        loadPreviewImage();
        setupListeners();
    }

    private void loadPreviewImage() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int target = Math.max(metrics.widthPixels, metrics.heightPixels);
        if (target <= 0) {
            target = 1;
        }
        int sampleSize = 1;
        try (InputStream raw = getContentResolver().openInputStream(sourceUri)) {
            if (raw == null) {
                throw new IllegalStateException("Input stream null");
            }
            java.io.BufferedInputStream buffered = new java.io.BufferedInputStream(raw);
            buffered.mark(256 * 1024);
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(buffered, null, bounds);
            sourceWidth = bounds.outWidth;
            sourceHeight = bounds.outHeight;
            if (sourceWidth <= 0 || sourceHeight <= 0) {
                throw new IllegalStateException("Invalid bounds");
            }
            sampleSize = Math.max(sampleSize, 1);
            while ((sourceWidth / sampleSize) > target || (sourceHeight / sampleSize) > target) {
                sampleSize *= 2;
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = sampleSize;
            try {
                buffered.reset();
                previewBitmap = BitmapFactory.decodeStream(buffered, null, opts);
            } catch (Exception resetError) {
                Log.w(TAG, "Buffered reset failed, reopening stream", resetError);
                try (InputStream retry = getContentResolver().openInputStream(sourceUri)) {
                    previewBitmap = BitmapFactory.decodeStream(retry, null, opts);
                }
            }
            if (previewBitmap == null) {
                throw new IllegalStateException("Preview decode failed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load image", e);
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        imageView.setImageBitmap(previewBitmap);
        imageView.post(() -> {
            RectF bounds = getImageBounds();
            overlayView.setImageBounds(bounds);
        });
    }

    private RectF getImageBounds() {
        if (previewBitmap == null) {
            return new RectF();
        }
        RectF rect = new RectF(0f, 0f, previewBitmap.getWidth(), previewBitmap.getHeight());
        Matrix matrix = imageView.getImageMatrix();
        matrix.mapRect(rect);
        return rect;
    }

    private void setupListeners() {
        cancelButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            setResult(RESULT_CANCELED);
            finish();
        });

        retakeButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            setResult(RESULT_RETAKE);
            finish();
        });

        confirmButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            confirmButton.setEnabled(false);
            cropAndReturn();
        });
    }

    private void cropAndReturn() {
        Toast.makeText(this, "Cropping...", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            Rect cropRect = getCropRectInSource();
            if (cropRect == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Invalid selection", Toast.LENGTH_SHORT).show();
                    confirmButton.setEnabled(true);
                });
                return;
            }

            Bitmap cropped = null;
            try (InputStream is = getContentResolver().openInputStream(sourceUri)) {
                if (is == null) {
                    throw new IllegalStateException("Input stream null");
                }
                android.graphics.BitmapRegionDecoder decoder = null;
                try {
                    decoder = android.graphics.BitmapRegionDecoder.newInstance(is, false);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    cropped = decoder.decodeRegion(cropRect, opts);
                    if (cropped == null) {
                        throw new IllegalStateException("Cropped bitmap null");
                    }
                } finally {
                    if (decoder != null) {
                        decoder.recycle();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed cropping region", e);
            }

            if (cropped == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Crop failed", Toast.LENGTH_SHORT).show();
                    confirmButton.setEnabled(true);
                });
                return;
            }

            File outFile = new File(getCacheDir(), "highlight_crop_" + System.currentTimeMillis() + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                cropped.compress(Bitmap.CompressFormat.JPEG, OUTPUT_QUALITY, fos);
                fos.flush();
            } catch (Exception e) {
                Log.e(TAG, "Failed to save crop", e);
                cropped.recycle();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to save crop", Toast.LENGTH_SHORT).show();
                    confirmButton.setEnabled(true);
                });
                return;
            }
            cropped.recycle();

            Uri outputUri = Uri.fromFile(outFile);
            runOnUiThread(() -> {
                Intent result = new Intent();
                result.setData(outputUri);
                setResult(RESULT_OK, result);
                finish();
            });
        }).start();
    }

    private Rect getCropRectInSource() {
        RectF normalized = overlayView.getNormalizedCropRect();
        if (normalized == null || sourceWidth <= 0 || sourceHeight <= 0) {
            return null;
        }
        int left = Math.max(0, Math.round(normalized.left * sourceWidth));
        int top = Math.max(0, Math.round(normalized.top * sourceHeight));
        int right = Math.min(sourceWidth, Math.round(normalized.right * sourceWidth));
        int bottom = Math.min(sourceHeight, Math.round(normalized.bottom * sourceHeight));
        if (right - left < 1 || bottom - top < 1) {
            return null;
        }
        return new Rect(left, top, right, bottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (previewBitmap != null && !previewBitmap.isRecycled()) {
            previewBitmap.recycle();
            previewBitmap = null;
        }
    }
}
