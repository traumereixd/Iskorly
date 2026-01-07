package com.bandecoot.itemscoreanalysisprogram;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Vision-only OCR processor with multi-pass preprocessing and smart parsing.
 * No longer uses OCR.Space fallback in active pipeline.
 */
public class OcrProcessor {
    private static final String TAG = "ISA_VISION_PROC";
    
    // Scoring constants
    private static final int SCORE_PER_FILLED_ANSWER = 10;
    private static final int SCORE_NUMERIC_ANCHOR_BONUS = 2;
    
    private final OkHttpClient httpClient;
    private final String visionApiKey;
    private final String ocrSpaceApiKey; // Kept for legacy compatibility but not used
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
     * Process bitmap through multi-variant Vision-only OCR pipeline.
     * Generates multiple preprocessed variants, runs Vision DOCUMENT_TEXT_DETECTION on each,
     * parses and scores each variant, then selects the best result.
     * 
     * Strategy:
     * 1. Generate up to MAX_VARIANTS preprocessing variants (now 8)
     * 2. Run Vision OCR on each variant with high quality settings
     * 3. Parse each result with smart parser
     * 4. Score based on: filled answer count, numeric anchor presence, quality metrics
     * 5. Early-exit if filled threshold is met (EARLY_EXIT_FILLED_THRESHOLD - now 70%)
     * 6. Optionally call AI re-parser if result is below REPARSE_MIN_FILLED_THRESHOLD
     * 7. Return the best scoring variant
     */
    public HashMap<Integer, String> processImage(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null");
            return new HashMap<>();
        }
        
        Log.d(TAG, "Starting multi-variant OCR processing (MAX_VARIANTS=" + 
                BuildConfig.MAX_VARIANTS + ", EARLY_EXIT=" + BuildConfig.EARLY_EXIT_FILLED_THRESHOLD + ")");
        
        // Phase 3: Analyze image quality to guide preprocessing selection
        ImagePreprocessor.ImageQuality quality = ImagePreprocessor.analyzeImageQuality(bitmap);
        Log.d(TAG, "Adaptive preprocessing based on: " + quality);
        
        // Generate preprocessing variants (prioritized based on image quality)
        List<PreprocessVariant> variants = new ArrayList<>();
        
        // Adaptive variant selection based on image quality
        if (quality.isBlurry) {
            // Prioritize sharpening for blurry images
            Log.d(TAG, "Image is blurry - prioritizing sharpening variants");
            addVariant(variants, "sharpened", ImagePreprocessor.preprocessSharpened(bitmap));
            addVariant(variants, "classroom", ImagePreprocessor.preprocessForClassroom(bitmap));
            addVariant(variants, "ultra_contrast", ImagePreprocessor.preprocessUltraHighContrast(bitmap));
        } else if (quality.isLowLight || quality.contrast < 0.15f) {
            // Prioritize contrast enhancement for low-light/low-contrast images
            Log.d(TAG, "Image has low light/contrast - prioritizing contrast variants");
            addVariant(variants, "ultra_contrast", ImagePreprocessor.preprocessUltraHighContrast(bitmap));
            addVariant(variants, "adaptive_histogram", ImagePreprocessor.preprocessAdaptiveHistogram(bitmap));
            addVariant(variants, "classroom", ImagePreprocessor.preprocessForClassroom(bitmap));
        } else if (quality.isHighLight) {
            // Prioritize adaptive methods for overexposed images
            Log.d(TAG, "Image is overexposed - prioritizing adaptive variants");
            addVariant(variants, "adaptive_histogram", ImagePreprocessor.preprocessAdaptiveHistogram(bitmap));
            addVariant(variants, "classroom", ImagePreprocessor.preprocessForClassroom(bitmap));
            addVariant(variants, "light", ImagePreprocessor.preprocessLight(bitmap));
        } else if (quality.brightness > 100 && quality.brightness < 180 && quality.contrast > 0.20f) {
            // Good quality image - use lighter preprocessing first
            Log.d(TAG, "Image quality is good - using lighter preprocessing");
            addVariant(variants, "light", ImagePreprocessor.preprocessLight(bitmap));
            addVariant(variants, "original", bitmap.copy(bitmap.getConfig(), false));
            addVariant(variants, "standard", ImageUtil.enhanceForOcr(bitmap));
        } else {
            // Default: try classroom preprocessing first
            Log.d(TAG, "Using default preprocessing priority");
            addVariant(variants, "classroom", ImagePreprocessor.preprocessForClassroom(bitmap));
            addVariant(variants, "light", ImagePreprocessor.preprocessLight(bitmap));
            addVariant(variants, "adaptive_histogram", ImagePreprocessor.preprocessAdaptiveHistogram(bitmap));
        }
        
        // Fill remaining slots with other variants (up to MAX_VARIANTS)
        addVariant(variants, "standard", ImageUtil.enhanceForOcr(bitmap));
        addVariant(variants, "grayscale", ImagePreprocessor.toGrayscale(bitmap));
        addVariant(variants, "sharpened", ImagePreprocessor.preprocessSharpened(bitmap));
        addVariant(variants, "ultra_contrast", ImagePreprocessor.preprocessUltraHighContrast(bitmap));
        addVariant(variants, "adaptive_histogram", ImagePreprocessor.preprocessAdaptiveHistogram(bitmap));
        addVariant(variants, "light", ImagePreprocessor.preprocessLight(bitmap));
        addVariant(variants, "classroom", ImagePreprocessor.preprocessForClassroom(bitmap));
        addVariant(variants, "original", bitmap.copy(bitmap.getConfig(), false));
        
        if (variants.isEmpty()) {
            Log.e(TAG, "All preprocessing variants failed");
            return new HashMap<>();
        }
        
        Log.d(TAG, "Generated " + variants.size() + " preprocessing variants (adaptive prioritization)");
        
        // Process each variant and score
        PreprocessResult bestResult = null;
        int bestScore = -1;
        String bestOcrText = "";
        int variantIndex = 0;
        
        for (PreprocessVariant variant : variants) {
            variantIndex++;
            try {
                // Compress to JPEG with higher quality (95% instead of 80%) and larger size (2048px instead of 1600px)
                // This preserves more detail for poor quality images
                byte[] jpegBytes = ImageUtil.resizeAndCompressHighQuality(variant.bitmap, 2048);
                
                // Call Vision API (DOCUMENT_TEXT_DETECTION)
                String recognizedText = callVisionApi(jpegBytes);
                
                if (recognizedText == null) {
                    recognizedText = "";
                }
                
                // Parse with smart parser
                HashMap<Integer, String> parsed = parseAndFilterSmart(recognizedText);
                
                // Score this variant
                int score = scoreVariant(parsed, recognizedText);
                int filledCount = countFilledAnswers(parsed);
                float fillRatio = answerKey.isEmpty() ? 0 : (float) filledCount / answerKey.size();
                
                Log.d(TAG, String.format("Variant %d/%d '%s': score=%d, filled=%d/%d (%.1f%%)",
                        variantIndex, variants.size(), variant.name, score, 
                        filledCount, answerKey.size(), fillRatio * 100));
                
                if (score > bestScore) {
                    bestScore = score;
                    bestResult = new PreprocessResult(variant.name, parsed, recognizedText);
                    bestOcrText = recognizedText;
                }
                
                // Early exit if we've met the threshold (now 70% instead of 90%)
                if (fillRatio >= BuildConfig.EARLY_EXIT_FILLED_THRESHOLD) {
                    Log.d(TAG, "Early exit triggered at " + (fillRatio * 100) + "% filled (threshold: " + 
                            (BuildConfig.EARLY_EXIT_FILLED_THRESHOLD * 100) + "%)");
                    break;
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error processing variant '" + variant.name + "'", e);
            } finally {
                // Clean up bitmap
                variant.bitmap.recycle();
            }
        }
        
        if (bestResult == null) {
            Log.e(TAG, "No successful OCR variant");
            return new HashMap<>();
        }
        
        Log.d(TAG, "Selected best variant: '" + bestResult.variantName + "' with score " + bestScore);
        
        // Check if we need a second pass with TEXT_DETECTION mode
        int filledCount = countFilledAnswers(bestResult.parsedAnswers);
        float fillRatio = answerKey.isEmpty() ? 0 : (float) filledCount / answerKey.size();
        
        // If we got less than 50% filled, try TEXT_DETECTION mode on best variant
        if (fillRatio < 0.50f) {
            Log.d(TAG, String.format("Fill ratio %.1f%% is low, attempting second pass with TEXT_DETECTION mode",
                    fillRatio * 100));
            
            try {
                // Try the top 2-3 variants with TEXT_DETECTION mode
                int maxRetries = Math.min(3, variants.size());
                PreprocessResult textDetectionBest = null;
                int textDetectionBestScore = -1;
                
                // Sort variants by score (we need to recreate them since originals were recycled)
                // For now, just retry the best variant type
                Bitmap retryBitmap = null;
                
                // Recreate the best variant
                switch (bestResult.variantName) {
                    case "light":
                        retryBitmap = ImagePreprocessor.preprocessLight(bitmap);
                        break;
                    case "classroom":
                        retryBitmap = ImagePreprocessor.preprocessForClassroom(bitmap);
                        break;
                    case "ultra_contrast":
                        retryBitmap = ImagePreprocessor.preprocessUltraHighContrast(bitmap);
                        break;
                    case "sharpened":
                        retryBitmap = ImagePreprocessor.preprocessSharpened(bitmap);
                        break;
                    case "adaptive_histogram":
                        retryBitmap = ImagePreprocessor.preprocessAdaptiveHistogram(bitmap);
                        break;
                    default:
                        // Use original for standard/grayscale/original variants
                        retryBitmap = bitmap.copy(bitmap.getConfig(), false);
                        break;
                }
                
                if (retryBitmap != null) {
                    byte[] jpegBytes = ImageUtil.resizeAndCompressHighQuality(retryBitmap, 2048);
                    
                    // Call Vision API with TEXT_DETECTION mode
                    String recognizedText = callVisionApi(jpegBytes, false);
                    
                    if (recognizedText != null && !recognizedText.isEmpty()) {
                        HashMap<Integer, String> parsed = parseAndFilterSmart(recognizedText);
                        int score = scoreVariant(parsed, recognizedText);
                        int newFilledCount = countFilledAnswers(parsed);
                        
                        Log.d(TAG, String.format("TEXT_DETECTION mode: score=%d, filled=%d/%d (%.1f%%)",
                                score, newFilledCount, answerKey.size(),
                                (float) newFilledCount / answerKey.size() * 100));
                        
                        // Use TEXT_DETECTION result if it's better
                        if (score > bestScore) {
                            Log.d(TAG, "TEXT_DETECTION mode produced better result, using it");
                            bestResult = new PreprocessResult(bestResult.variantName + "_text_detect",
                                    parsed, recognizedText);
                            bestOcrText = recognizedText;
                            filledCount = newFilledCount;
                            fillRatio = (float) filledCount / answerKey.size();
                        }
                    }
                    
                    retryBitmap.recycle();
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error in TEXT_DETECTION second pass", e);
            }
        }
        
        // Check if we should call AI re-parser
        filledCount = countFilledAnswers(bestResult.parsedAnswers);
        fillRatio = answerKey.isEmpty() ? 0 : (float) filledCount / answerKey.size();
        
        if (fillRatio < BuildConfig.REPARSE_MIN_FILLED_THRESHOLD && 
            !BuildConfig.REPARSE_ENDPOINT.isEmpty()) {
            
            Log.d(TAG, String.format("Fill ratio %.1f%% below threshold %.1f%%, calling AI re-parser",
                    fillRatio * 100, BuildConfig.REPARSE_MIN_FILLED_THRESHOLD * 100));
            
            try {
                Map<Integer, String> reParsed = NetworkUtil.callReparserEndpoint(
                        httpClient, BuildConfig.REPARSE_ENDPOINT, bestOcrText, answerKey);
                
                // Merge re-parsed results (non-empty values override existing)
                int mergedCount = 0;
                for (Map.Entry<Integer, String> entry : reParsed.entrySet()) {
                    String newValue = entry.getValue();
                    if (newValue != null && !newValue.trim().isEmpty()) {
                        bestResult.parsedAnswers.put(entry.getKey(), newValue);
                        mergedCount++;
                    }
                }
                
                Log.d(TAG, "AI re-parser merged " + mergedCount + " improved answers");
                
            } catch (Exception e) {
                Log.e(TAG, "Error calling AI re-parser", e);
            }
        }
        
        return bestResult.parsedAnswers;
    }
    
    /**
     * Helper method to add a variant to the list if not already present and within limit.
     * 
     * @param variants List of variants
     * @param name Variant name
     * @param bitmap Preprocessed bitmap
     */
    private void addVariant(List<PreprocessVariant> variants, String name, Bitmap bitmap) {
        if (variants.size() >= BuildConfig.MAX_VARIANTS) {
            if (bitmap != null) bitmap.recycle();
            return;
        }
        
        // Check if variant with this name already exists
        for (PreprocessVariant v : variants) {
            if (v.name.equals(name)) {
                if (bitmap != null) bitmap.recycle();
                return;
            }
        }
        
        if (bitmap != null) {
            variants.add(new PreprocessVariant(name, bitmap));
        }
    }
    
    /**
     * Score a parsed variant based on:
     * - Number of non-empty answers that align with answer key
     * - Small bonus for presence of numeric anchors in text
     */
    private int scoreVariant(HashMap<Integer, String> parsed, String text) {
        int score = 0;
        
        // Main score: count filled answers that match answer key questions
        for (Map.Entry<Integer, String> entry : parsed.entrySet()) {
            if (answerKey.containsKey(entry.getKey()) && !entry.getValue().trim().isEmpty()) {
                score += SCORE_PER_FILLED_ANSWER;
            }
        }
        
        // Bonus: numeric anchors present (indicates numbered format was detected)
        // Check for patterns like "1.", "2)", "3.", "4)" etc. in text
        if (text != null && text.matches(".*\\d+[.)].*")) {
            score += SCORE_NUMERIC_ANCHOR_BONUS;
        }
        
        return score;
    }
    
    /**
     * Count how many non-empty answers are in the map.
     */
    private int countFilledAnswers(HashMap<Integer, String> answers) {
        int count = 0;
        for (String value : answers.values()) {
            if (value != null && !value.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Helper class to hold preprocessing variant.
     */
    private static class PreprocessVariant {
        final String name;
        final Bitmap bitmap;
        
        PreprocessVariant(String name, Bitmap bitmap) {
            this.name = name;
            this.bitmap = bitmap;
        }
    }
    
    /**
     * Helper class to hold preprocessing result.
     */
    private static class PreprocessResult {
        final String variantName;
        final HashMap<Integer, String> parsedAnswers;
        final String recognizedText;
        
        PreprocessResult(String variantName, HashMap<Integer, String> parsedAnswers, String recognizedText) {
            this.variantName = variantName;
            this.parsedAnswers = parsedAnswers;
            this.recognizedText = recognizedText;
        }
    }

    
    /**
     * Parse OCR text using smart parser and filter to answer key questions.
     * Uses number-aware parser that can handle both numbered and unnumbered formats.
     */
    private HashMap<Integer, String> parseAndFilterSmart(String text) {
        // Use smart parser with answer key validation
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        // Filter to answer key
        LinkedHashMap<Integer, String> filtered = Parser.filterToAnswerKey(parsed, answerKey);
        
        // Convert to HashMap
        HashMap<Integer, String> result = new HashMap<>();
        result.putAll(filtered);
        
        return result;
    }
    
    /**
     * Legacy parse method (kept for backward compatibility, but not used in new pipeline).
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
     * Supports both DOCUMENT_TEXT_DETECTION and TEXT_DETECTION modes.
     * 
     * @param jpegBytes Image bytes
     * @param useDocumentMode If true, use DOCUMENT_TEXT_DETECTION; if false, use TEXT_DETECTION
     * @return Recognized text or null on error
     */
    private String callVisionApi(byte[] jpegBytes, boolean useDocumentMode) {
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
            // Use DOCUMENT_TEXT_DETECTION for structured text, TEXT_DETECTION for general text
            String detectionType = useDocumentMode ? "DOCUMENT_TEXT_DETECTION" : "TEXT_DETECTION";
            feature.put("type", detectionType);
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
     * Call Google Vision API for OCR (overload for backward compatibility).
     * Defaults to DOCUMENT_TEXT_DETECTION mode.
     */
    private String callVisionApi(byte[] jpegBytes) {
        return callVisionApi(jpegBytes, true);
    }
    
    // ========== Legacy OCR.Space stub methods (kept for backward compatibility) ==========
    // These methods are no longer called in the active pipeline but kept for code references
    
    /**
     * OCR.Space fallback (LEGACY - not used in active pipeline).
     * Kept as stub for backward compatibility.
     */
    @Deprecated
    private String callOcrSpaceApi(byte[] jpegBytes) {
        // No longer used in active Vision-only pipeline
        Log.d(TAG, "OCR.Space fallback disabled in Vision-only mode");
        return null;
    }
    
    /**
     * Check if OCR.Space API key is configured (LEGACY).
     */
    @Deprecated
    private boolean hasOcrSpaceKey() {
        return false; // Always false in Vision-only mode
    }
    
    // ========== Phase 4: OCR Robustness Enhancements ==========
    
    /**
     * Process image with high-contrast preprocessing mode.
     * Uses adaptive thresholding via Otsu binarization.
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
            // Apply classroom preprocessing (includes Otsu binarization)
            Bitmap preprocessed = ImagePreprocessor.preprocessForClassroom(bitmap);
            if (preprocessed == null) {
                Log.e(TAG, "Classroom preprocessing failed");
                return new HashMap<>();
            }
            
            // Compress to JPEG
            byte[] jpegBytes = ImageUtil.resizeAndCompress(preprocessed, 1600);
            preprocessed.recycle();
            
            // Call Vision API
            String recognizedText = callVisionApi(jpegBytes);
            
            if (recognizedText == null) {
                recognizedText = "";
            }
            
            // Parse with smart parser
            return parseAndFilterSmart(recognizedText);
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing image with high-contrast", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Process image using two-column OCR mode.
     * Splits image into left/right halves, processes each separately with preprocessing,
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
            
            // Process left half with preprocessing
            HashMap<Integer, String> leftAnswers = processHalfWithPreprocessing(leftHalf, "left");
            leftHalf.recycle();
            
            // Process right half with preprocessing
            HashMap<Integer, String> rightAnswers = processHalfWithPreprocessing(rightHalf, "right");
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
     * Process a single half of the image with classroom preprocessing (helper for two-column mode).
     */
    private HashMap<Integer, String> processHalfWithPreprocessing(Bitmap half, String side) {
        try {
            // Apply classroom preprocessing
            Bitmap preprocessed = ImagePreprocessor.preprocessForClassroom(half);
            if (preprocessed == null) {
                Log.e(TAG, "Preprocessing failed for " + side + " half");
                return new HashMap<>();
            }
            
            // Compress to JPEG
            byte[] jpegBytes = ImageUtil.resizeAndCompress(preprocessed, 1600);
            preprocessed.recycle();
            
            // Call Vision API
            String recognizedText = callVisionApi(jpegBytes);
            
            if (recognizedText == null) {
                recognizedText = "";
            }
            
            // Parse with smart parser
            return parseAndFilterSmart(recognizedText);
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing " + side + " half", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Process image using smart parser (LEGACY - integrated into main pipeline).
     * Redirects to standard multi-variant processing.
     * 
     * @param bitmap Source bitmap
     * @return Parsed answers using smart parser
     */
    @Deprecated
    public HashMap<Integer, String> processImageWithSmartParser(Bitmap bitmap) {
        // Redirect to standard multi-variant processing which includes smart parsing
        return processImage(bitmap);
    }
    
    /**
     * Close and cleanup resources.
     */
    public void close() {
        // Nothing to close for now
    }
}
