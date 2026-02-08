package com.bandecoot.itemscoreanalysisprogram.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bandecoot.itemscoreanalysisprogram.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Azure Read API OCR engine implementing async analyze + poll workflow.
 * Uses the Azure Computer Vision Read API for document text recognition.
 */
public class AzureReadOcrEngine implements OcrEngine {
    private static final String TAG = "AzureReadOcrEngine";
    private static final int MAX_POLL_ATTEMPTS = 30;
    private static final long POLL_INTERVAL_MS = 1000; // 1 second
    
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final java.util.concurrent.ExecutorService executor = Executors.newSingleThreadExecutor();
    private final OkHttpClient client = new OkHttpClient();

    @Override 
    public String name() { 
        return "Azure Read API"; 
    }

    @Override
    public void recognize(Context ctx, Bitmap bitmap, Callback cb) {
        if (bitmap == null) {
            postError(cb, new IllegalArgumentException("Bitmap is null"));
            return;
        }
        executor.execute(() -> {
            try {
                String apiKey = BuildConfig.AZURE_VISION_KEY == null ? "" : BuildConfig.AZURE_VISION_KEY.trim();
                String endpoint = BuildConfig.AZURE_VISION_ENDPOINT == null ? "" : BuildConfig.AZURE_VISION_ENDPOINT.trim();
                
                if (apiKey.isEmpty()) {
                    postError(cb, new IllegalStateException("AZURE_VISION_KEY missing"));
                    return;
                }
                if (endpoint.isEmpty()) {
                    postError(cb, new IllegalStateException("AZURE_VISION_ENDPOINT missing"));
                    return;
                }
                
                // Ensure endpoint doesn't end with slash
                if (endpoint.endsWith("/")) {
                    endpoint = endpoint.substring(0, endpoint.length() - 1);
                }
                
                // Resize and compress bitmap
                Bitmap toSend = bitmap;
                int maxDim = Math.max(bitmap.getWidth(), bitmap.getHeight());
                if (maxDim > 2048) {
                    double scale = 2048.0 / maxDim;
                    toSend = Bitmap.createScaledBitmap(bitmap,
                            (int) (bitmap.getWidth() * scale),
                            (int) (bitmap.getHeight() * scale),
                            true);
                }
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                toSend.compress(Bitmap.CompressFormat.JPEG, 95, baos);
                byte[] imageBytes = baos.toByteArray();

                // Step 1: Submit analyze request
                String analyzeUrl = endpoint + "/vision/v3.2/read/analyze";
                Request analyzeRequest = new Request.Builder()
                        .url(analyzeUrl)
                        .header("Ocp-Apim-Subscription-Key", apiKey)
                        .header("Content-Type", "application/octet-stream")
                        .post(RequestBody.create(imageBytes, MediaType.parse("application/octet-stream")))
                        .build();

                String operationLocation;
                try (Response analyzeResp = client.newCall(analyzeRequest).execute()) {
                    if (!analyzeResp.isSuccessful()) {
                        String errorBody = analyzeResp.body() != null ? analyzeResp.body().string() : "";
                        postError(cb, new RuntimeException("Azure analyze error: " + analyzeResp.code() + " " + errorBody));
                        return;
                    }
                    
                    // Get operation location from header
                    operationLocation = analyzeResp.header("Operation-Location");
                    if (operationLocation == null || operationLocation.isEmpty()) {
                        postError(cb, new RuntimeException("Azure analyze response missing Operation-Location header"));
                        return;
                    }
                }

                // Step 2: Poll for results
                String text = pollForResults(operationLocation, apiKey);
                postResult(cb, text.trim());
                
            } catch (Exception e) {
                Log.e(TAG, "OCR failed", e);
                postError(cb, e);
            }
        });
    }

    /**
     * Poll the Azure Read API operation until it completes.
     */
    private String pollForResults(String operationLocation, String apiKey) throws Exception {
        for (int attempt = 0; attempt < MAX_POLL_ATTEMPTS; attempt++) {
            Request pollRequest = new Request.Builder()
                    .url(operationLocation)
                    .header("Ocp-Apim-Subscription-Key", apiKey)
                    .get()
                    .build();

            try (Response pollResp = client.newCall(pollRequest).execute()) {
                if (!pollResp.isSuccessful()) {
                    String errorBody = pollResp.body() != null ? pollResp.body().string() : "";
                    throw new RuntimeException("Azure poll error: " + pollResp.code() + " " + errorBody);
                }

                String body = pollResp.body() != null ? pollResp.body().string() : "";
                JSONObject result = new JSONObject(body);
                String status = result.optString("status", "");

                if ("succeeded".equalsIgnoreCase(status)) {
                    // Extract text from results
                    return extractText(result);
                } else if ("failed".equalsIgnoreCase(status)) {
                    throw new RuntimeException("Azure Read API analysis failed");
                } else if ("running".equalsIgnoreCase(status) || "notStarted".equalsIgnoreCase(status)) {
                    // Wait before next poll
                    Thread.sleep(POLL_INTERVAL_MS);
                } else {
                    throw new RuntimeException("Unknown Azure Read API status: " + status);
                }
            }
        }
        
        throw new RuntimeException("Azure Read API polling timeout after " + MAX_POLL_ATTEMPTS + " attempts");
    }

    /**
     * Extract text from Azure Read API response.
     */
    private String extractText(JSONObject result) {
        StringBuilder text = new StringBuilder();
        
        try {
            JSONObject analyzeResult = result.optJSONObject("analyzeResult");
            if (analyzeResult == null) {
                return "";
            }
            
            JSONArray readResults = analyzeResult.optJSONArray("readResults");
            if (readResults == null) {
                return "";
            }
            
            // Iterate through pages
            for (int i = 0; i < readResults.length(); i++) {
                JSONObject page = readResults.getJSONObject(i);
                JSONArray lines = page.optJSONArray("lines");
                
                if (lines != null) {
                    for (int j = 0; j < lines.length(); j++) {
                        JSONObject line = lines.getJSONObject(j);
                        String lineText = line.optString("text", "");
                        if (!lineText.isEmpty()) {
                            text.append(lineText).append("\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting text from Azure response", e);
        }
        
        return text.toString();
    }

    private void postResult(Callback cb, String text) {
        mainHandler.post(() -> {
            try { 
                cb.onResult(text); 
            } catch (Throwable t) { 
                Log.w(TAG, "callback onResult", t); 
            }
        });
    }

    private void postError(Callback cb, Exception e) {
        mainHandler.post(() -> {
            try { 
                cb.onError(e); 
            } catch (Throwable t) { 
                Log.w(TAG, "callback onError", t); 
            }
        });
    }

    @Override 
    public void close() {
        executor.shutdown();
    }
}
