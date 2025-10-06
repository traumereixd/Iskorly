package com.bandecoot.itemscoreanalysisprogram;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class NetworkUtil {
    private static final String TAG = "ISA_VISION_NET";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private NetworkUtil() {}

    // Calls Google Cloud Vision (DOCUMENT_TEXT_DETECTION). Returns recognized text or "".
    public static String callVisionApi(OkHttpClient httpClient, byte[] jpegBytes, String apiKey) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Log.e(TAG, "Missing API key");
            return "";
        }
        String base64 = Base64.encodeToString(jpegBytes, Base64.NO_WRAP);
        String url = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;

        // Build request JSON
        JSONObject image = new JSONObject().put("content", base64);
        JSONObject feature = new JSONObject()
                .put("type", "DOCUMENT_TEXT_DETECTION")
                .put("maxResults", 1);
        JSONObject request = new JSONObject()
                .put("image", image)
                .put("features", new JSONArray().put(feature));
        JSONObject root = new JSONObject()
                .put("requests", new JSONArray().put(request));

        RequestBody body = RequestBody.create(root.toString(), JSON);
        Request httpReq = new Request.Builder().url(url).post(body).build();

        try (Response resp = httpClient.newCall(httpReq).execute()) {
            if (!resp.isSuccessful()) {
                String errBody = resp.body() != null ? resp.body().string() : "";
                Log.e(TAG, "Vision API error " + resp.code() + ": " + errBody);
                return "";
            }
            String respStr = resp.body() != null ? resp.body().string() : "";
            JSONObject respJson = new JSONObject(respStr);
            JSONArray responses = respJson.optJSONArray("responses");
            if (responses == null || responses.length() == 0) return "";

            JSONObject first = responses.optJSONObject(0);
            if (first == null) return "";

            if (first.has("fullTextAnnotation")) {
                JSONObject full = first.optJSONObject("fullTextAnnotation");
                return full != null ? full.optString("text", "") : "";
            }
            JSONArray textAnn = first.optJSONArray("textAnnotations");
            if (textAnn != null && textAnn.length() > 0) {
                JSONObject top = textAnn.optJSONObject(0);
                return top != null ? top.optString("description", "") : "";
            }
            return "";
        }
    }
}