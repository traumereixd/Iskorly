package com.bandecoot.itemscoreanalysisprogram;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    
    /**
     * Call optional AI re-parser endpoint to improve low-confidence OCR results.
     * Only sends raw OCR text (no images or PII) to the endpoint.
     * 
     * Expected response format:
     * {
     *   "answers": {
     *     "1": "Apple",
     *     "2": "A",
     *     "3": "B"
     *   }
     * }
     * 
     * @param httpClient OkHttp client
     * @param endpoint Re-parser endpoint URL
     * @param ocrText Raw OCR text to re-parse
     * @param answerKey Current answer key for context
     * @return Map of re-parsed answers (question number -> answer), or empty map on error
     */
    public static Map<Integer, String> callReparserEndpoint(
            OkHttpClient httpClient, 
            String endpoint, 
            String ocrText,
            Map<Integer, String> answerKey) {
        
        Map<Integer, String> result = new HashMap<>();
        
        if (endpoint == null || endpoint.trim().isEmpty()) {
            return result;
        }
        
        try {
            Log.d(TAG, "Calling AI re-parser endpoint: " + endpoint);
            
            // Build request JSON
            JSONObject request = new JSONObject();
            request.put("text", ocrText);
            
            // Include answer key structure for context (question numbers only)
            JSONArray questionNumbers = new JSONArray();
            for (Integer q : answerKey.keySet()) {
                questionNumbers.put(q);
            }
            request.put("questionNumbers", questionNumbers);
            
            RequestBody body = RequestBody.create(request.toString(), JSON);
            Request httpReq = new Request.Builder()
                    .url(endpoint)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            
            try (Response resp = httpClient.newCall(httpReq).execute()) {
                if (!resp.isSuccessful()) {
                    String errBody = resp.body() != null ? resp.body().string() : "";
                    Log.e(TAG, "Re-parser endpoint error " + resp.code() + ": " + errBody);
                    return result;
                }
                
                String respStr = resp.body() != null ? resp.body().string() : "";
                JSONObject respJson = new JSONObject(respStr);
                
                if (respJson.has("answers")) {
                    JSONObject answers = respJson.getJSONObject("answers");
                    Iterator<String> keys = answers.keys();
                    
                    while (keys.hasNext()) {
                        String qStr = keys.next();
                        try {
                            int q = Integer.parseInt(qStr);
                            String answer = answers.getString(qStr);
                            
                            // Only accept if answer is non-empty and question is in answer key
                            if (answer != null && !answer.trim().isEmpty() && answerKey.containsKey(q)) {
                                result.put(q, answer.trim());
                            }
                        } catch (NumberFormatException e) {
                            Log.w(TAG, "Invalid question number from re-parser: " + qStr);
                        }
                    }
                    
                    Log.d(TAG, "Re-parser returned " + result.size() + " answers");
                }
                
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error calling re-parser endpoint", e);
        }
        
        return result;
    }
}