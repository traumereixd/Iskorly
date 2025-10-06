package com.bandecoot.itemscoreanalysisprogram.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Simple OCR.Space engine implementation for the app's OcrEngine interface.
 * Uses HttpURLConnection and sends the image as x-www-form-urlencoded base64Image parameter.
 *
 * Note: keep your OCR_SPACE_API_KEY out of source control (use local.properties -> BuildConfig).
 */
public class OcrSpaceEngine implements OcrEngine {
    private static final String TAG = "OcrSpaceEngine";
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final String apiKey;
    private final String endpoint;

    // default endpoint
    public OcrSpaceEngine(String apiKey) {
        this(apiKey, "https://api.ocr.space/parse/image");
    }

    public OcrSpaceEngine(String apiKey, String endpoint) {
        this.apiKey = apiKey == null ? "" : apiKey;
        this.endpoint = endpoint == null || endpoint.isEmpty() ? "https://api.ocr.space/parse/image" : endpoint;
    }

    @Override public String name() { return "OCR.Space"; }

    @Override
    public void recognize(Context ctx, Bitmap bitmap, Callback cb) {
        if (bitmap == null) { postError(cb, new IllegalArgumentException("Bitmap is null")); return; }
        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                String base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

                String body = "base64Image=" + java.net.URLEncoder.encode("data:image/jpeg;base64," + base64, "UTF-8")
                        + "&language=eng"
                        + "&isOverlayRequired=false";

                URL url = new URL(endpoint);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(60000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("apikey", apiKey);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                byte[] out = body.getBytes("UTF-8");
                conn.setFixedLengthStreamingMode(out.length);
                try (OutputStream os = conn.getOutputStream()) { os.write(out); }

                int code = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream()
                ));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                if (code < 200 || code >= 300) {
                    postError(cb, new RuntimeException("OCR.Space returned " + code + ": " + sb.toString()));
                    return;
                }

                JSONObject root = new JSONObject(sb.toString());
                JSONArray parsed = root.optJSONArray("ParsedResults");
                String text = "";
                if (parsed != null && parsed.length() > 0) {
                    JSONObject pr = parsed.optJSONObject(0);
                    if (pr != null) text = pr.optString("ParsedText", "");
                }
                postResult(cb, text == null ? "" : text.trim());
            } catch (Exception e) {
                Log.e(TAG, "ocrspace failed", e);
                postError(cb, e instanceof Exception ? (Exception) e : new RuntimeException(e));
            } finally {
                if (conn != null) try { conn.disconnect(); } catch (Throwable ignored) {}
            }
        });
    }

    private void postResult(Callback cb, String text) {
        mainHandler.post(() -> { try { cb.onResult(text); } catch (Throwable ignored) {} });
    }
    private void postError(Callback cb, Exception e) {
        mainHandler.post(() -> { try { cb.onError(e); } catch (Throwable ignored) {} });
    }

    @Override public void close() {}
}