package com.bandecoot.itemscoreanalysisprogram;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Parser {
    private static final String TAG = "ISA_PARSER";
    
    // Parser configuration constants
    private static final float ORDER_ONLY_FALLBACK_THRESHOLD = 0.3f; // Switch to order-only below 30% filled

    private Parser() {}

    // Roman numeral to digit conversion map
    private static final String[] ROMAN_NUMERALS = {
        "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
        "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX",
        "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI", "XXVII", "XXVIII", "XXIX", "XXX"
    };

    // Parses OCR text into Q->Answer map with enhanced patterns and roman numeral support
    public static LinkedHashMap<Integer, String> parseOcrTextToAnswers(String text) {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        if (text == null || text.trim().isEmpty()) return map;

        // Normalize line breaks and common OCR artifacts
        String normalized = normalizeText(text);

        // Convert roman numerals to digits
        String convertedText = convertRomanNumerals(normalized);

        // Extract answers using multiple patterns
        extractAnswersWithPatterns(convertedText, map);

        // Deduplicate and limit to valid range (1-200)
        LinkedHashMap<Integer, String> filtered = new LinkedHashMap<>();
        for (Integer q : map.keySet()) {
            if (q >= 1 && q <= 200 && !filtered.containsKey(q)) {
                filtered.put(q, map.get(q));
            }
        }

        Log.d(TAG, "Parsed " + filtered.size() + " answers from enhanced parser");
        return filtered;
    }

    private static String normalizeText(String text) {
        // Normalize line breaks and remove common artifacts
        return text.replaceAll("[\r]", "\n")
                  .replaceAll("[•·–—_]+", " ")  // Remove noise punctuation
                  .replaceAll("\\s+", " ");     // Collapse multiple spaces
    }

    private static String convertRomanNumerals(String text) {
        String result = text;
        
        // Convert roman numerals at the start of lines followed by punctuation/space
        Pattern romanPattern = Pattern.compile("(?:^|\\n)\\s*([IVX]+)\\s*[.):)]?\\s*", Pattern.MULTILINE);
        Matcher matcher = romanPattern.matcher(result);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String roman = matcher.group(1).toUpperCase();
            int digit = romanToDigit(roman);
            if (digit > 0) {
                String replacement = matcher.group().replaceFirst(roman, String.valueOf(digit));
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group()));
            }
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }

    private static int romanToDigit(String roman) {
        for (int i = 0; i < ROMAN_NUMERALS.length; i++) {
            if (ROMAN_NUMERALS[i].equals(roman)) {
                return i + 1;
            }
        }
        return -1;
    }

    private static void extractAnswersWithPatterns(String text, LinkedHashMap<Integer, String> map) {
        // Pattern 1: Multi-line format "number [punctuation] answer"
        // Supports: "1 A", "1. B", "1) C", "1 - D", "1: E"
        Pattern p1 = Pattern.compile("^\\s*(\\d{1,3})\\s*[.):)]?\\s*[-:]?\\s*([A-Za-z]\\w{0,39})\\b", Pattern.MULTILINE);
        extractWithPattern(p1, text, map);

        // Pattern 2: Inline pairs "number punctuation answer"  
        Pattern p2 = Pattern.compile("(\\d{1,3})\\s*[.):)]\\s*[-:]?\\s*([A-Za-z]\\w{0,39})");
        extractWithPattern(p2, text, map);

        // Pattern 3: Simple number-letter pairs
        Pattern p3 = Pattern.compile("(\\d{1,3})\\s+([A-Z])\\b");
        extractWithPattern(p3, text, map);
    }

    private static void extractWithPattern(Pattern pattern, String text, LinkedHashMap<Integer, String> map) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int q = safeInt(matcher.group(1));
            if (q <= 0 || q > 200) continue;
            
            String rawAnswer = matcher.group(2);
            String answer = cleanAnswer(rawAnswer);
            
            if (!answer.isEmpty() && !map.containsKey(q)) {
                // If single letter A-Z, use uppercase; otherwise keep as-is
                if (answer.length() == 1 && Character.isLetter(answer.charAt(0))) {
                    answer = answer.toUpperCase(Locale.US);
                }
                map.put(q, answer);
            }
        }
    }

    private static String cleanAnswer(String answer) {
        if (answer == null) return "";
        
        // Trim and remove trailing punctuation/noise
        String cleaned = answer.trim().replaceAll("[.,;!?]+$", "");
        
        // Limit length to 40 characters
        if (cleaned.length() > 40) {
            cleaned = cleaned.substring(0, 40).trim();
        }
        
        return cleaned;
    }

    private static int safeInt(String s) {
        try { 
            return Integer.parseInt(s.trim()); 
        } catch (Exception e) { 
            return -1; 
        }
    }

    /**
     * Filter parsed answers to only include questions present in the answer key.
     * For questions in the key but not in parsed, add them with empty string.
     * Questions not in the key are ignored.
     * 
     * @param parsed The raw parsed answers from OCR
     * @param answerKey The current answer key (question->answer map)
     * @return Filtered map containing only answer key questions, sorted by question number
     */
    public static LinkedHashMap<Integer, String> filterToAnswerKey(
            LinkedHashMap<Integer, String> parsed, 
            java.util.Map<Integer, String> answerKey) {
        
        LinkedHashMap<Integer, String> filtered = new LinkedHashMap<>();
        
        if (answerKey == null || answerKey.isEmpty()) {
            // No answer key: return empty (don't show any parsed answers)
            Log.d(TAG, "No answer key set - returning empty filtered map");
            return filtered;
        }
        
        // Get sorted list of answer key questions
        java.util.List<Integer> keyQuestions = new java.util.ArrayList<>(answerKey.keySet());
        java.util.Collections.sort(keyQuestions);
        
        // For each question in answer key, get parsed value or empty string
        for (Integer q : keyQuestions) {
            String parsedAnswer = (parsed != null && parsed.containsKey(q)) ? parsed.get(q) : "";
            filtered.put(q, parsedAnswer != null ? parsedAnswer : "");
        }
        
        Log.d(TAG, "Filtered to " + filtered.size() + " answer key questions (from " + 
              (parsed != null ? parsed.size() : 0) + " parsed)");
        return filtered;
    }

    /**
     * Smart parser with fallback: tries number-aware parsing first, then falls back to order-only.
     * This is the main entry point for the new multi-pass OCR pipeline.
     * 
     * Strategy:
     * 1. Try parsing with number-aware mode (looks for "1. A", "2) B", etc.)
     * 2. If too few answers found, try order-only mode (map lines sequentially to answer key)
     * 3. Return whichever yields more filled answers
     * 
     * @param text OCR text to parse
     * @param answerKey Current answer key for validation and fallback mapping
     * @return Parsed answers map
     */
    public static LinkedHashMap<Integer, String> parseOcrTextSmartWithFallback(
            String text, java.util.Map<Integer, String> answerKey) {
        
        if (text == null || text.trim().isEmpty()) return new LinkedHashMap<>();
        if (answerKey == null || answerKey.isEmpty()) return new LinkedHashMap<>();
        
        // Step 1: Try number-aware parsing
        LinkedHashMap<Integer, String> numberedResult = parseNumberAware(text, answerKey);
        
        // Step 2: If result is sparse, try order-only fallback
        int filledCount = countFilledAnswers(numberedResult);
        int expectedCount = answerKey.size();
        
        Log.d(TAG, "Number-aware parsing found " + filledCount + " filled answers out of " + expectedCount);
        
        // If we got less than threshold of expected answers, try order-only mode
        if (filledCount < expectedCount * ORDER_ONLY_FALLBACK_THRESHOLD) {
            Log.d(TAG, "Trying order-only fallback (below " + (ORDER_ONLY_FALLBACK_THRESHOLD * 100) + "% threshold)");
            LinkedHashMap<Integer, String> orderOnlyResult = parseOrderOnly(text, answerKey);
            int orderFilledCount = countFilledAnswers(orderOnlyResult);
            
            Log.d(TAG, "Order-only parsing found " + orderFilledCount + " filled answers");
            
            // Use whichever got more filled answers
            if (orderFilledCount > filledCount) {
                return orderOnlyResult;
            }
        }
        
        return numberedResult;
    }
    
    /**
     * Number-aware parser: looks for numbered patterns like "1. A", "2) B", etc.
     * Strips leading numbers and extracts answers.
     */
    private static LinkedHashMap<Integer, String> parseNumberAware(
            String text, java.util.Map<Integer, String> answerKey) {
        
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        java.util.Set<String> allowedSet = buildAllowedSet(answerKey);
        
        // Normalize and convert roman numerals
        String normalized = normalizeTextPreserveLines(text);
        String converted = convertRomanNumeralsPreserveLines(normalized);
        
        // Parse line by line
        String[] lines = converted.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            // Try patterns: "1. A", "1) B", "1 A", "1- A", "1: A"
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                    "^\\s*(\\d{1,3})\\s*[.):)]?\\s*[-:]?\\s*([A-Za-z]\\w{0,39})\\b");
            java.util.regex.Matcher m = p.matcher(line);
            
            if (m.find()) {
                int q = safeInt(m.group(1));
                String answer = m.group(2).trim();
                String canonical = canonical(answer);
                
                if (q >= 1 && q <= 200 && answerKey.containsKey(q) && 
                    allowedSet.contains(canonical) && !map.containsKey(q)) {
                    map.put(q, answer);
                }
            }
        }
        
        return map;
    }
    
    /**
     * Order-only parser: ignores numbers, maps each line sequentially to answer key questions.
     * Useful when OCR dropped numbering but preserved answer order.
     */
    private static LinkedHashMap<Integer, String> parseOrderOnly(
            String text, java.util.Map<Integer, String> answerKey) {
        
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        java.util.Set<String> allowedSet = buildAllowedSet(answerKey);
        
        // Get sorted list of answer key questions
        java.util.List<Integer> keyQuestions = new java.util.ArrayList<>(answerKey.keySet());
        java.util.Collections.sort(keyQuestions);
        
        // Normalize text
        String normalized = normalizeTextPreserveLines(text);
        String converted = convertRomanNumeralsPreserveLines(normalized);
        
        // Extract candidate answers (lines that look like answers)
        java.util.List<String> candidates = new java.util.ArrayList<>();
        String[] lines = converted.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // Strip any leading numbering (e.g., "1. ", "2) ", etc.)
            String cleaned = line.replaceFirst("^\\s*\\d{1,3}\\s*[.):)]?\\s*[-:]?\\s*", "");
            cleaned = cleaned.trim();
            
            if (cleaned.isEmpty()) continue;
            
            // Check if it looks like an answer (single word or single letter)
            String[] words = cleaned.split("\\s+");
            if (words.length <= 3) { // Accept up to 3 words as potential answer
                String candidate = words[0]; // Take first word
                String canonicalCandidate = canonical(candidate);
                
                // Only accept if it's in allowed set
                if (allowedSet.contains(canonicalCandidate)) {
                    candidates.add(candidate);
                }
            }
        }
        
        Log.d(TAG, "Order-only found " + candidates.size() + " candidate answers");
        
        // Map candidates to answer key questions in order
        int minCount = Math.min(candidates.size(), keyQuestions.size());
        for (int i = 0; i < minCount; i++) {
            Integer q = keyQuestions.get(i);
            String answer = candidates.get(i);
            map.put(q, answer);
        }
        
        return map;
    }
    
    /**
     * Count how many non-empty answers are in the map.
     */
    private static int countFilledAnswers(java.util.Map<Integer, String> answers) {
        int count = 0;
        for (String value : answers.values()) {
            if (value != null && !value.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    // ---------------------------
    // Smart OCR Parser (answer-first aware)
    // ---------------------------

    /**
     * Parse OCR text with answer-first support and strict answer key validation.
     * Supports both "1. A" and "True 1." formats.
     * Only extracts valid answers that match the allowed set from the answer key.
     * 
     * @param text OCR text to parse
     * @param answerKey Current answer key to derive valid answer tokens
     * @return Parsed answers map
     */
    public static LinkedHashMap<Integer, String> parseOcrTextToAnswersSmart(
            String text, java.util.Map<Integer, String> answerKey) {
        
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        if (text == null || text.trim().isEmpty()) return map;
        if (answerKey == null || answerKey.isEmpty()) return map;

        // Build allowed answer set from answer key
        java.util.Set<String> allowedSet = buildAllowedSet(answerKey);
        Log.d(TAG, "Smart parser allowed set: " + allowedSet);

        // Normalize text but preserve line breaks
        String normalized = normalizeTextPreserveLines(text);
        
        // Convert roman numerals to digits
        String converted = convertRomanNumeralsPreserveLines(normalized);
        
        // Parse line by line
        String[] lines = converted.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            // Try number-first pattern: "1. A" or "1 ) B"
            Pattern p1 = Pattern.compile("^\\s*(\\d{1,3})\\s*[.):)]?\\s*[-:]?\\s*([A-Za-z]\\w{0,39})\\b");
            Matcher m1 = p1.matcher(line);
            if (m1.find()) {
                int q = safeInt(m1.group(1));
                String answer = m1.group(2).trim();
                String canonical = canonical(answer);
                
                if (q >= 1 && q <= 200 && allowedSet.contains(canonical) && !map.containsKey(q)) {
                    map.put(q, answer);
                    continue;
                }
            }
            
            // Try answer-first pattern: "True 1." or "FALSE 2)"
            Pattern p2 = Pattern.compile("^\\s*([A-Za-z]\\w{0,39})\\s+(\\d{1,3})\\s*[.):)]?");
            Matcher m2 = p2.matcher(line);
            if (m2.find()) {
                String answer = m2.group(1).trim();
                int q = safeInt(m2.group(2));
                String canonical = canonical(answer);
                
                if (q >= 1 && q <= 200 && allowedSet.contains(canonical) && !map.containsKey(q)) {
                    map.put(q, answer);
                }
            }
        }
        
        Log.d(TAG, "Smart parser extracted " + map.size() + " answers");
        return map;
    }

    /**
     * Normalize text but preserve line breaks for line-by-line parsing.
     */
    private static String normalizeTextPreserveLines(String text) {
        return text.replaceAll("\r\n", "\n")
                   .replaceAll("\r", "\n")
                   .replaceAll("[•·–—_]+", " ");
    }

    /**
     * Convert roman numerals but preserve line structure.
     */
    private static String convertRomanNumeralsPreserveLines(String text) {
        String result = text;
        Pattern romanPattern = Pattern.compile("(?:^|\\n)\\s*([IVX]+)\\s*[.):)]?\\s*");
        Matcher matcher = romanPattern.matcher(result);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String roman = matcher.group(1).toUpperCase();
            int digit = romanToDigit(roman);
            if (digit > 0) {
                String replacement = matcher.group().replaceFirst(roman, String.valueOf(digit));
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group()));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Canonicalize answer for comparison (normalize synonyms).
     * T/F -> TRUE/FALSE, Y/N -> YES/NO, single letters -> uppercase
     */
    private static String canonical(String answer) {
        if (answer == null) return "";
        String upper = answer.toUpperCase(Locale.US).trim();
        
        // Normalize T/F
        if (upper.equals("T")) return "TRUE";
        if (upper.equals("F")) return "FALSE";
        
        // Normalize Y/N
        if (upper.equals("Y")) return "YES";
        if (upper.equals("N")) return "NO";
        
        return upper;
    }

    /**
     * Build set of allowed answer tokens from answer key (with synonyms).
     */
    private static java.util.Set<String> buildAllowedSet(java.util.Map<Integer, String> answerKey) {
        java.util.Set<String> allowed = new java.util.HashSet<>();
        
        for (String ans : answerKey.values()) {
            if (ans == null || ans.trim().isEmpty()) continue;
            String canonical = canonical(ans);
            allowed.add(canonical);
            
            // Add original too if different
            String upper = ans.toUpperCase(Locale.US).trim();
            if (!upper.equals(canonical)) {
                allowed.add(upper);
            }
            
            // Add common variants
            if (canonical.equals("TRUE")) {
                allowed.add("T");
                allowed.add("TRUE");
            } else if (canonical.equals("FALSE")) {
                allowed.add("F");
                allowed.add("FALSE");
            } else if (canonical.equals("YES")) {
                allowed.add("Y");
                allowed.add("YES");
            } else if (canonical.equals("NO")) {
                allowed.add("N");
                allowed.add("NO");
            }
        }
        
        return allowed;
    }
}