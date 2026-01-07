package com.bandecoot.itemscoreanalysisprogram.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.bandecoot.itemscoreanalysisprogram.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Cloud Vision OCR via OkHttp.
 */
public class CloudVisionOcrEngine implements OcrEngine {
    private static final String TAG = "CloudVisionOcrEngine";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final OkHttpClient client = new OkHttpClient();

    @Override public String name() { return "Cloud Vision"; }

    @Override
    public void recognize(Context ctx, Bitmap bitmap, Callback cb) {
        if (bitmap == null) {
            postError(cb, new IllegalArgumentException("Bitmap is null"));
            return;
        }
        executor.execute(() -> {
            try {
                String apiKey = BuildConfig.GCLOUD_VISION_API_KEY == null ? "" : BuildConfig.GCLOUD_VISION_API_KEY.trim();
                if (apiKey.isEmpty()) {
                    postError(cb, new IllegalStateException("GCLOUD_VISION_API_KEY missing"));
                    return;
                }
                Bitmap toSend = bitmap;
                int maxDim = Math.max(bitmap.getWidth(), bitmap.getHeight());
                // Increased from 1600 to 2048 for better quality with poor cameras
                if (maxDim > 2048) {
                    double scale = 2048.0 / maxDim;
                    toSend = Bitmap.createScaledBitmap(bitmap,
                            (int) (bitmap.getWidth() * scale),
                            (int) (bitmap.getHeight() * scale),
                            true);
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // Increased JPEG quality from 80 to 95 for better OCR results
                toSend.compress(Bitmap.CompressFormat.JPEG, 95, baos);
                String base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

                JSONObject req = new JSONObject()
                        .put("image", new JSONObject().put("content", base64))
                        .put("features", new JSONArray()
                                .put(new JSONObject()
                                        .put("type", "DOCUMENT_TEXT_DETECTION")
                                        .put("maxResults", 1)));
                JSONObject payload = new JSONObject().put("requests", new JSONArray().put(req));

                Request request = new Request.Builder()
                        .url("https://vision.googleapis.com/v1/images:annotate?key=" + apiKey)
                        .post(RequestBody.create(payload.toString(), MediaType.parse("application/json; charset=utf-8")))
                        .build();

                try (Response resp = client.newCall(request).execute()) {
                    if (!resp.isSuccessful()) {
                        postError(cb, new RuntimeException("Vision error: " + resp.code() + " " + resp.message()));
                        return;
                    }
                    String body = resp.body() != null ? resp.body().string() : "";
                    JSONObject root = new JSONObject(body);
                    JSONArray responses = root.optJSONArray("responses");
                    String text = "";
                    if (responses != null && responses.length() > 0) {
                        JSONObject first = responses.optJSONObject(0);
                        if (first != null) {
                            JSONObject fta = first.optJSONObject("fullTextAnnotation");
                            if (fta != null) text = fta.optString("text", "");
                            if (text.isEmpty()) {
                                JSONArray ta = first.optJSONArray("textAnnotations");
                                if (ta != null && ta.length() > 0) {
                                    JSONObject t0 = ta.optJSONObject(0);
                                    if (t0 != null) text = t0.optString("description", "");
                                }
                            }
                        }
                    }
                    postResult(cb, text.trim());
                }
            } catch (Exception e) {
                Log.e(TAG, "OCR failed", e);
                postError(cb, e);
            }
        });
    }

    private void postResult(Callback cb, String text) {
        mainHandler.post(() -> {
            try { cb.onResult(text); } catch (Throwable t) { Log.w(TAG, "callback onResult", t); }
        });
    }

    private void postError(Callback cb, Exception e) {
        mainHandler.post(() -> {
            try { cb.onError(e); } catch (Throwable t) { Log.w(TAG, "callback onError", t); }
        });
    }

    @Override public void close() {}
}