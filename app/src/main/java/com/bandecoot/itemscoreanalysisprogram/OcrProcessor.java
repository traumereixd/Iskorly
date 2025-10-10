package com.bandecoot.itemscoreanalysisprogram;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Feature #6: Helper class to encapsulate OCR processing logic.
 * Handles Vision API, OCR.Space fallback, image preprocessing, and answer parsing.
 */
public class OcrProcessor {
    private static final String TAG = "ISA_OCR_PROCESSOR";
    
    private final OkHttpClient httpClient;
    private final String visionApiKey;
    private final String ocrSpaceApiKey;
    private final Map<Integer, String> answerKey;
    
    public OcrProcessor(String visionApiKey, String ocrSpaceApiKey, Map<Integer, String> answerKey) {
        this.visionApiKey = visionApiKey;
        this.ocrSpaceApiKey = ocrSpaceApiKey;
        this.answerKey = answerKey;
        
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Process a bitmap through OCR pipeline:
     * 1. Enhance image for OCR
     * 2. Compress to JPEG
     * 3. Call Vision API
     * 4. Fallback to OCR.Space if empty
     * 5. Parse answers
     * 6. Filter to answer key
     */
    public HashMap<Integer, String> processImage(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null");
            return new HashMap<>();
        }
        
        try {
            // Enhance for OCR
            Bitmap enhanced = ImageUtil.enhanceForOcr(bitmap);
            if (enhanced == null) {
                Log.e(TAG, "Enhancement failed");
                return new HashMap<>();
            }
            
            // Compress to JPEG
            byte[] jpegBytes = ImageUtil.resizeAndCompress(enhanced, 1600);
            enhanced.recycle();
            
            // OCR
            String recognizedText = callVisionApi(jpegBytes);
            
            // Fallback to OCR.Space if empty
            if ((recognizedText == null || recognizedText.trim().isEmpty()) && hasOcrSpaceKey()) {
                Log.d(TAG, "Vision returned empty, trying OCR.Space fallback");
                recognizedText = callOcrSpaceApi(jpegBytes);
            }
            
            if (recognizedText == null) {
                recognizedText = "";
            }
            
            // Parse and filter
            return parseAndFilter(recognizedText);
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing image", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Parse OCR text and filter to answer key questions.
     */
    private HashMap<Integer, String> parseAndFilter(String text) {
        // Parse using enhanced parser
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextToAnswers(text);
        
        // Filter to answer key
        LinkedHashMap<Integer, String> filtered = Parser.filterToAnswerKey(parsed, answerKey);
        
        // Convert to HashMap
        HashMap<Integer, String> result = new HashMap<>();
        result.putAll(filtered);
        
        return result;
    }
    
    /**
     * Call Google Vision API for OCR.
     */
    private String callVisionApi(byte[] jpegBytes) {
        if (visionApiKey == null || visionApiKey.trim().isEmpty()) {
            Log.d(TAG, "Vision API key not configured");
            return null;
        }
        
        try {
            String base64Image = Base64.encodeToString(jpegBytes, Base64.NO_WRAP);
            String url = "https://vision.googleapis.com/v1/images:annotate?key=" + visionApiKey;
            
            JSONObject req = new JSONObject();
            JSONArray requests = new JSONArray();
            JSONObject image = new JSONObject();
            image.put("content", base64Image);
            
            JSONObject feature = new JSONObject();
            feature.put("type", "DOCUMENT_TEXT_DETECTION");
            feature.put("maxResults", 1);
            
            JSONObject request = new JSONObject();
            request.put("image", image);
            request.put("features", new JSONArray().put(feature));
            requests.put(request);
            req.put("requests", requests);
            
            RequestBody body = RequestBody.create(req.toString(), MediaType.parse("application/json"));
            Request httpReq = new Request.Builder().url(url).post(body).build();
            
            try (Response resp = httpClient.newCall(httpReq).execute()) {
                if (!resp.isSuccessful()) {
                    Log.e(TAG, "Vision API error: " + resp.code());
                    return null;
                }
                
                String respStr = resp.body().string();
                JSONObject respJson = new JSONObject(respStr);
                JSONArray responses = respJson.optJSONArray("responses");
                if (responses == null || responses.length() == 0) return null;
                
                JSONObject first = responses.getJSONObject(0);
                if (first.has("fullTextAnnotation")) {
                    JSONObject fullText = first.getJSONObject("fullTextAnnotation");
                    return fullText.optString("text", "");
                } else if (first.has("textAnnotations")) {
                    JSONArray textAnn = first.getJSONArray("textAnnotations");
                    if (textAnn.length() > 0) {
                        JSONObject top = textAnn.getJSONObject(0);
                        return top.optString("description", "");
                    }
                }
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Vision API call failed", e);
            return null;
        }
    }
    
    /**
     * Call OCR.Space API as fallback.
     */
    private String callOcrSpaceApi(byte[] jpegBytes) {
        if (!hasOcrSpaceKey()) {
            return null;
        }
        
        try {
            Log.d(TAG, "Using OCR.Space fallback");
            
            String base64Image = Base64.encodeToString(jpegBytes, Base64.NO_WRAP);
            String body = "base64Image=" + java.net.URLEncoder.encode("data:image/jpeg;base64," + base64Image, "UTF-8")
                    + "&language=eng"
                    + "&isOverlayRequired=false";
            
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
            RequestBody requestBody = RequestBody.create(body, mediaType);
            
            Request request = new Request.Builder()
                    .url("https://api.ocr.space/parse/image")
                    .post(requestBody)
                    .addHeader("apikey", ocrSpaceApiKey)
                    .build();
            
            try (Response resp = httpClient.newCall(request).execute()) {
                if (!resp.isSuccessful()) {
                    Log.e(TAG, "OCR.Space API error: " + resp.code());
                    return null;
                }
                
                String respStr = resp.body().string();
                JSONObject respJson = new JSONObject(respStr);
                JSONArray parsed = respJson.optJSONArray("ParsedResults");
                
                if (parsed != null && parsed.length() > 0) {
                    JSONObject pr = parsed.getJSONObject(0);
                    String text = pr.optString("ParsedText", "");
                    Log.d(TAG, "OCR.Space returned " + text.length() + " chars");
                    return text.trim();
                }
                
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "OCR.Space API call failed", e);
            return null;
        }
    }
    
    /**
     * Check if OCR.Space API key is configured.
     */
    private boolean hasOcrSpaceKey() {
        return ocrSpaceApiKey != null && !ocrSpaceApiKey.trim().isEmpty();
    }
    
    // ========== Phase 4: OCR Robustness Enhancements ==========
    
    /**
     * Process image with high-contrast preprocessing mode.
     * Uses adaptive thresholding to improve handwriting recognition.
     * 
     * @param bitmap Source bitmap
     * @return Parsed answers
     */
    public HashMap<Integer, String> processImageWithHighContrast(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null");
            return new HashMap<>();
        }
        
        try {
            // Apply high-contrast thresholding
            Bitmap thresholded = ImageUtil.thresholdForOcr(bitmap);
            if (thresholded == null) {
                Log.e(TAG, "Thresholding failed");
                return new HashMap<>();
            }
            
            // Compress to JPEG
            byte[] jpegBytes = ImageUtil.resizeAndCompress(thresholded, 1600);
            thresholded.recycle();
            
            // OCR
            String recognizedText = callVisionApi(jpegBytes);
            
            // Fallback to OCR.Space if empty
            if ((recognizedText == null || recognizedText.trim().isEmpty()) && hasOcrSpaceKey()) {
                Log.d(TAG, "Vision returned empty, trying OCR.Space fallback with high-contrast");
                recognizedText = callOcrSpaceApi(jpegBytes);
            }
            
            if (recognizedText == null) {
                recognizedText = "";
            }
            
            // Parse and filter
            return parseAndFilter(recognizedText);
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing image with high-contrast", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Process image using two-column OCR mode.
     * Splits image into left/right halves, processes each separately,
     * then merges results (first non-blank answer wins).
     * 
     * @param bitmap Source bitmap
     * @return Parsed answers merged from both columns
     */
    public HashMap<Integer, String> processImageTwoColumn(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null");
            return new HashMap<>();
        }
        
        try {
            // Split image vertically
            Bitmap[] halves = ImageUtil.splitImageVertically(bitmap);
            Bitmap leftHalf = halves[0];
            Bitmap rightHalf = halves[1];
            
            if (leftHalf == null || rightHalf == null) {
                Log.e(TAG, "Image split failed");
                return new HashMap<>();
            }
            
            // Process left half
            HashMap<Integer, String> leftAnswers = processHalf(leftHalf, "left");
            leftHalf.recycle();
            
            // Process right half
            HashMap<Integer, String> rightAnswers = processHalf(rightHalf, "right");
            rightHalf.recycle();
            
            // Merge results: first non-blank answer wins
            HashMap<Integer, String> merged = new HashMap<>();
            
            // Add left answers first
            merged.putAll(leftAnswers);
            
            // Add right answers if not already present or if left was empty
            for (Map.Entry<Integer, String> entry : rightAnswers.entrySet()) {
                Integer q = entry.getKey();
                String rightAns = entry.getValue();
                
                if (!merged.containsKey(q) || merged.get(q).trim().isEmpty()) {
                    merged.put(q, rightAns);
                }
            }
            
            Log.d(TAG, "Two-column merge: left=" + leftAnswers.size() + 
                    ", right=" + rightAnswers.size() + ", merged=" + merged.size());
            
            return merged;
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing two-column image", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Process a single half of the image (helper for two-column mode).
     */
    private HashMap<Integer, String> processHalf(Bitmap half, String side) {
        try {
            // Enhance for OCR
            Bitmap enhanced = ImageUtil.enhanceForOcr(half);
            if (enhanced == null) {
                Log.e(TAG, "Enhancement failed for " + side + " half");
                return new HashMap<>();
            }
            
            // Compress to JPEG
            byte[] jpegBytes = ImageUtil.resizeAndCompress(enhanced, 1600);
            enhanced.recycle();
            
            // OCR
            String recognizedText = callVisionApi(jpegBytes);
            
            // Fallback to OCR.Space if empty
            if ((recognizedText == null || recognizedText.trim().isEmpty()) && hasOcrSpaceKey()) {
                Log.d(TAG, "Vision returned empty for " + side + " half, trying OCR.Space");
                recognizedText = callOcrSpaceApi(jpegBytes);
            }
            
            if (recognizedText == null) {
                recognizedText = "";
            }
            
            // Parse and filter
            return parseAndFilter(recognizedText);
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing " + side + " half", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Process image using smart parser with answer-first support.
     * Uses restricted parsing based on allowed answer tokens from the answer key.
     * 
     * @param bitmap Source bitmap
     * @return Parsed answers using smart parser
     */
    public HashMap<Integer, String> processImageWithSmartParser(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null");
            return new HashMap<>();
        }
        
        try {
            // Enhance for OCR
            Bitmap enhanced = ImageUtil.enhanceForOcr(bitmap);
            if (enhanced == null) {
                Log.e(TAG, "Enhancement failed");
                return new HashMap<>();
            }
            
            // Compress to JPEG
            byte[] jpegBytes = ImageUtil.resizeAndCompress(enhanced, 1600);
            enhanced.recycle();
            
            // OCR
            String recognizedText = callVisionApi(jpegBytes);
            
            // Fallback to OCR.Space if empty
            if ((recognizedText == null || recognizedText.trim().isEmpty()) && hasOcrSpaceKey()) {
                Log.d(TAG, "Vision returned empty, trying OCR.Space fallback");
                recognizedText = callOcrSpaceApi(jpegBytes);
            }
            
            if (recognizedText == null) {
                recognizedText = "";
            }
            
            // Use smart parser with answer key validation
            LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextToAnswersSmart(recognizedText, answerKey);
            
            // Filter to answer key (additional safety check)
            LinkedHashMap<Integer, String> filtered = Parser.filterToAnswerKey(parsed, answerKey);
            
            // Convert to HashMap
            HashMap<Integer, String> result = new HashMap<>();
            result.putAll(filtered);
            
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing image with smart parser", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Close and cleanup resources.
     */
    public void close() {
        // Nothing to close for now
    }
}
