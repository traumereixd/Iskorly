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
    private static final int MAX_QUESTION_NUMBER = 200; // Maximum valid question number
    private static final int MAX_ANSWER_LENGTH = 40; // Maximum answer text length
    
    // Regex pattern to split lines containing multiple numbered items
    // Updated to handle compressed sequences like "1.A2.B3.C" or "1.A  3.Z  5.C"
    // Matches: answer_letter [optional space/punct] number [optional punct/space] answer_letter
    // Looks behind for alphanumeric character, looks ahead for number followed by answer
    private static final String MULTI_ITEM_SPLIT_PATTERN = "(?<=[A-Za-z0-9])(?=[\\s.]*\\d{1,3}[.):)]?[\\s-:]*[A-Za-z])";

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

        // Deduplicate and limit to valid range (1-MAX_QUESTION_NUMBER)
        LinkedHashMap<Integer, String> filtered = new LinkedHashMap<>();
        for (Integer q : map.keySet()) {
            if (q >= 1 && q <= MAX_QUESTION_NUMBER && !filtered.containsKey(q)) {
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
            if (q <= 0 || q > MAX_QUESTION_NUMBER) continue;
            
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
        
        // Limit length to MAX_ANSWER_LENGTH
        if (cleaned.length() > MAX_ANSWER_LENGTH) {
            cleaned = cleaned.substring(0, MAX_ANSWER_LENGTH).trim();
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
     * Robust number-anchored parser that handles misalignment, compression, and out-of-order items.
     * Strictly detects question numbers anywhere and pairs each with the nearest valid answer token.
     * 
     * Features:
     * - Accepts multiple formats: 1.A, 1)B, 1-C, 1:D, 1 WORD, roman numerals, etc.
     * - Handles compressed multi-item lines: "1.A2.B3.C" or "1.A  3.Z  5.C"
     * - Supports cross-line linking: "5." on one line, "C" on next
     * - Supports answer-first: "True 1." or "A 1."
     * - Uses allowed-set for preference but doesn't reject candidates
     * - Resolves duplicates by "first non-blank wins" unless later matches allowed-set
     * 
     * @param text OCR text to parse
     * @param answerKey Answer key for validation and allowed-set building
     * @return Parsed answers map
     */
    private static LinkedHashMap<Integer, String> parseNumberAnchoredRobust(
            String text, java.util.Map<Integer, String> answerKey) {
        
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        if (text == null || text.trim().isEmpty()) return map;
        if (answerKey == null || answerKey.isEmpty()) return map;
        
        // Build allowed answer set from answer key
        java.util.Set<String> allowedSet = buildAllowedSet(answerKey);
        
        // Normalize and convert roman numerals while preserving line structure
        String normalized = normalizeTextPreserveLines(text);
        String converted = convertRomanNumeralsPreserveLines(normalized);
        
        // Split into lines and process with cross-line linking
        String[] lines = converted.split("\n");
        Integer pendingNumber = null; // For cross-line number-only pairing
        
        for (int lineIdx = 0; lineIdx < lines.length; lineIdx++) {
            String line = lines[lineIdx].trim();
            if (line.isEmpty()) continue;
            
            // First, try to split compressed multi-item lines like "1.A2.B3.C" or "1.A  3.Z  5.C"
            java.util.List<String> segments = splitCompressedLine(line);
            
            for (String segment : segments) {
                segment = segment.trim();
                if (segment.isEmpty()) continue;
                
                // Pattern 1: Number-first with various separators
                // Matches: "1.A", "1)B", "1-C", "1:D", "1 WORD", etc.
                Pattern numFirst = Pattern.compile("^\\s*(\\d{1,3})\\s*[.):)]?\\s*[-:,]?\\s*([A-Za-z][A-Za-z0-9]{0,39})\\b");
                Matcher m1 = numFirst.matcher(segment);
                if (m1.find()) {
                    int q = safeInt(m1.group(1));
                    String answer = m1.group(2).trim();
                    if (q >= 1 && q <= MAX_QUESTION_NUMBER) {
                        addAnswerWithPreference(map, q, answer, allowedSet);
                        pendingNumber = null; // Clear pending
                        continue;
                    }
                }
                
                // Pattern 2: Answer-first patterns like "True 1." or "A 1."
                Pattern ansFirst = Pattern.compile("^\\s*([A-Za-z][A-Za-z0-9]{0,39})\\b[\\s\\W]{0,3}(\\d{1,3})[.):)]?\\s*$");
                Matcher m2 = ansFirst.matcher(segment);
                if (m2.find()) {
                    String answer = m2.group(1).trim();
                    int q = safeInt(m2.group(2));
                    if (q >= 1 && q <= MAX_QUESTION_NUMBER) {
                        addAnswerWithPreference(map, q, answer, allowedSet);
                        pendingNumber = null; // Clear pending
                        continue;
                    }
                }
                
                // Pattern 3: Number-only (for cross-line linking)
                Pattern numOnly = Pattern.compile("^\\s*(\\d{1,3})\\s*[.):)]?\\s*$");
                Matcher m3 = numOnly.matcher(segment);
                if (m3.find()) {
                    int q = safeInt(m3.group(1));
                    if (q >= 1 && q <= MAX_QUESTION_NUMBER) {
                        pendingNumber = q;
                        continue;
                    }
                }
                
                // Pattern 4: Answer-only (pair with pending number if available)
                if (pendingNumber != null) {
                    // Extract answer from this segment
                    String answer = extractFirstValidAnswer(segment);
                    if (!answer.isEmpty()) {
                        addAnswerWithPreference(map, pendingNumber, answer, allowedSet);
                        pendingNumber = null;
                    }
                }
            }
        }
        
        Log.d(TAG, "Number-anchored robust parser extracted " + map.size() + " answers");
        return map;
    }
    
    /**
     * Split a compressed line containing multiple items like "1.A2.B3.C" or "1.A  3.Z  5.C"
     * into individual segments.
     */
    private static java.util.List<String> splitCompressedLine(String line) {
        java.util.List<String> segments = new java.util.ArrayList<>();
        
        // Enhanced pattern to detect boundaries between answer and next number
        // Looks for: [answer][optional space/punct][number][optional punct]
        // E.g., "A2.B" splits at boundary before "2"
        Pattern boundary = Pattern.compile("(?<=[A-Za-z0-9])(?=[\\s.]*\\d{1,3}[.):)]?[\\s-:]*[A-Za-z])");
        String[] parts = boundary.split(line);
        
        if (parts.length > 1) {
            // Successfully split
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    segments.add(part.trim());
                }
            }
        } else {
            // No split, return original
            segments.add(line);
        }
        
        return segments;
    }
    
    /**
     * Extract the first valid answer token from text.
     */
    private static String extractFirstValidAnswer(String text) {
        if (text == null || text.trim().isEmpty()) return "";
        
        // Match answer pattern: word starting with letter
        Pattern ansPattern = Pattern.compile("\\b([A-Za-z][A-Za-z0-9]{0,39})\\b");
        Matcher m = ansPattern.matcher(text);
        if (m.find()) {
            String answer = m.group(1);
            if (answer.length() > MAX_ANSWER_LENGTH) {
                answer = answer.substring(0, MAX_ANSWER_LENGTH);
            }
            // Clean trailing punctuation
            answer = answer.replaceAll("[.,;!?]+$", "");
            
            // Uppercase single letters
            if (answer.length() == 1 && Character.isLetter(answer.charAt(0))) {
                return answer.toUpperCase(Locale.US);
            }
            return answer;
        }
        return "";
    }
    
    /**
     * Add answer to map with allowed-set preference for duplicates.
     * - If question not in map, add it (even if not in allowed set)
     * - If question already in map:
     *   - Keep existing if both are not in allowed set (first wins)
     *   - Keep existing if existing is in allowed set
     *   - Replace with new if new is in allowed set and existing isn't
     */
    private static void addAnswerWithPreference(
            LinkedHashMap<Integer, String> map, 
            int question, 
            String answer, 
            java.util.Set<String> allowedSet) {
        
        if (answer == null || answer.trim().isEmpty()) return;
        
        // Clean and normalize answer
        String cleaned = answer.trim().replaceAll("[.,;!?]+$", "");
        if (cleaned.isEmpty()) return;
        
        // Limit length
        if (cleaned.length() > MAX_ANSWER_LENGTH) {
            cleaned = cleaned.substring(0, MAX_ANSWER_LENGTH).trim();
        }
        
        // Uppercase single letters
        if (cleaned.length() == 1 && Character.isLetter(cleaned.charAt(0))) {
            cleaned = cleaned.toUpperCase(Locale.US);
        }
        
        String canonicalNew = canonical(cleaned);
        boolean newInAllowedSet = allowedSet != null && allowedSet.contains(canonicalNew);
        
        if (!map.containsKey(question)) {
            // First occurrence, add it
            map.put(question, cleaned);
        } else {
            // Duplicate: prefer allowed-set match
            String existing = map.get(question);
            String canonicalExisting = canonical(existing);
            boolean existingInAllowedSet = allowedSet != null && allowedSet.contains(canonicalExisting);
            
            if (newInAllowedSet && !existingInAllowedSet) {
                // New answer is in allowed set but existing isn't - replace
                map.put(question, cleaned);
                Log.d(TAG, "Replaced Q" + question + ": '" + existing + "' -> '" + cleaned + "' (allowed-set preference)");
            }
            // Otherwise keep existing (first non-blank wins)
        }
    }

    /**
     * Smart parser with fallback: tries robust number-anchored first, then falls back to other strategies.
     * This is the main entry point for the new multi-pass OCR pipeline.
     * 
     * Strategy:
     * 1. Try robust number-anchored parsing (handles all formats, misalignment, compression)
     * 2. Try gap-tolerant parsing (hybrid strategy)
     * 3. Try number-aware parsing
     * 4. If too few answers found, try order-only mode
     * 5. Return whichever yields most filled answers
     * 
     * @param text OCR text to parse
     * @param answerKey Current answer key for validation and fallback mapping
     * @return Parsed answers map
     */
    public static LinkedHashMap<Integer, String> parseOcrTextSmartWithFallback(
            String text, java.util.Map<Integer, String> answerKey) {
        
        if (text == null || text.trim().isEmpty()) return new LinkedHashMap<>();
        if (answerKey == null || answerKey.isEmpty()) return new LinkedHashMap<>();
        
        // Step 1: Try robust number-anchored parsing
        LinkedHashMap<Integer, String> robustResult = parseNumberAnchoredRobust(text, answerKey);
        int robustFilledCount = countFilledAnswers(robustResult);
        
        Log.d(TAG, "Robust number-anchored parsing found " + robustFilledCount + " filled answers");
        
        // Step 2: Try gap-tolerant parsing (hybrid strategy)
        LinkedHashMap<Integer, String> gapTolerantResult = parseGapTolerant(text, answerKey);
        int gapFilledCount = countFilledAnswers(gapTolerantResult);
        
        Log.d(TAG, "Gap-tolerant parsing found " + gapFilledCount + " filled answers");
        
        // Step 3: Try number-aware parsing
        LinkedHashMap<Integer, String> numberAwareResult = parseNumberAware(text, answerKey);
        int numberAwareFilledCount = countFilledAnswers(numberAwareResult);
        
        Log.d(TAG, "Number-aware parsing found " + numberAwareFilledCount + " filled answers");
        
        // Find the best result so far
        LinkedHashMap<Integer, String> bestResult = robustResult;
        int bestFilledCount = robustFilledCount;
        
        if (gapFilledCount > bestFilledCount) {
            bestResult = gapTolerantResult;
            bestFilledCount = gapFilledCount;
        }
        
        if (numberAwareFilledCount > bestFilledCount) {
            bestResult = numberAwareResult;
            bestFilledCount = numberAwareFilledCount;
        }
        
        // Step 4: If result is sparse, try order-only fallback
        int expectedCount = answerKey.size();
        
        if (bestFilledCount < expectedCount * ORDER_ONLY_FALLBACK_THRESHOLD) {
            Log.d(TAG, "Trying order-only fallback (below " + (ORDER_ONLY_FALLBACK_THRESHOLD * 100) + "% threshold)");
            LinkedHashMap<Integer, String> orderOnlyResult = parseOrderOnly(text, answerKey);
            int orderFilledCount = countFilledAnswers(orderOnlyResult);
            
            Log.d(TAG, "Order-only parsing found " + orderFilledCount + " filled answers");
            
            // Use whichever got more filled answers
            if (orderFilledCount > bestFilledCount) {
                return orderOnlyResult;
            }
        }
        
        return bestResult;
    }
    
    /**
     * Gap-tolerant parser with hybrid strategy:
     * - Normalize text, split/join lines, strip numbering tokens
     * - Handle roman numerals, accept A..Z or short words
     * - Build two maps: byNumber (explicit numbers) and byOrder (sequence)
     * - Merge by filling gaps using orphan lines
     * 
     * @param text OCR text to parse
     * @param answerKey Current answer key for validation
     * @return Parsed answers map with gaps filled
     */
    private static LinkedHashMap<Integer, String> parseGapTolerant(
            String text, java.util.Map<Integer, String> answerKey) {
        
        if (text == null || text.trim().isEmpty()) return new LinkedHashMap<>();
        if (answerKey == null || answerKey.isEmpty()) return new LinkedHashMap<>();
        
        // Build allowed answer set from answer key
        java.util.Set<String> allowedSet = buildAllowedSet(answerKey);
        
        // Get sorted list of answer key questions
        java.util.List<Integer> keyQuestions = new java.util.ArrayList<>(answerKey.keySet());
        java.util.Collections.sort(keyQuestions);
        
        // Normalize and convert roman numerals
        String normalized = normalizeTextPreserveLines(text);
        String converted = convertRomanNumeralsPreserveLines(normalized);
        
        // Split lines that accidentally contain multiple numbered items
        // E.g., "1. A 2. B" becomes ["1. A", "2. B"]
        String[] rawLines = converted.split("\n");
        java.util.List<String> splitLines = new java.util.ArrayList<>();
        for (String line : rawLines) {
            // Try to split if line contains multiple question patterns
            String[] parts = line.split(MULTI_ITEM_SPLIT_PATTERN);
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    splitLines.add(part.trim());
                }
            }
        }
        
        // Phase 1: Build byNumber map (number-aware mapping)
        LinkedHashMap<Integer, String> byNumber = new LinkedHashMap<>();
        java.util.List<String> orphanLines = new java.util.ArrayList<>();
        
        for (String line : splitLines) {
            if (line.trim().isEmpty()) continue;
            
            // Try to extract question number and answer
            // Strip numbering tokens: "1.", "2)", "3 -", "4:", etc.
            Pattern numberPattern = Pattern.compile("^\\s*(\\d{1,3})\\s*[.):)]?\\s*[-:]?\\s*(.*)$");
            Matcher matcher = numberPattern.matcher(line);
            
            if (matcher.find()) {
                int q = safeInt(matcher.group(1));
                String answerPart = matcher.group(2).trim();
                
                // Extract answer (single letter or short text)
                String answer = extractAnswer(answerPart, allowedSet);
                
                if (q >= 1 && q <= MAX_QUESTION_NUMBER && answerKey.containsKey(q) && !answer.isEmpty() && !byNumber.containsKey(q)) {
                    byNumber.put(q, answer);
                } else if (!answer.isEmpty()) {
                    // Valid answer but not matched to a question - add to orphans
                    orphanLines.add(answer);
                }
            } else {
                // No number found, try to extract answer only
                String answer = extractAnswer(line, allowedSet);
                if (!answer.isEmpty()) {
                    orphanLines.add(answer);
                }
            }
        }
        
        Log.d(TAG, "Gap-tolerant: byNumber=" + byNumber.size() + ", orphans=" + orphanLines.size());
        
        // Phase 2: Fill gaps using orphan lines in order
        LinkedHashMap<Integer, String> merged = new LinkedHashMap<>();
        int orphanIndex = 0;
        
        for (Integer q : keyQuestions) {
            if (byNumber.containsKey(q)) {
                // Use explicitly numbered answer
                merged.put(q, byNumber.get(q));
            } else if (orphanIndex < orphanLines.size()) {
                // Fill gap with next orphan line
                merged.put(q, orphanLines.get(orphanIndex));
                orphanIndex++;
            } else {
                // No orphan available, leave empty
                merged.put(q, "");
            }
        }
        
        int filledCount = countFilledAnswers(merged);
        Log.d(TAG, "Gap-tolerant merge: filled " + filledCount + "/" + keyQuestions.size() + " questions");
        
        return merged;
    }
    
    /**
     * Extract answer from text, accepting single letters (A-Z) or short words.
     * Prefers answers in allowed set but doesn't reject others.
     * 
     * @param text Text to extract answer from
     * @param allowedSet Set of allowed answers (canonical form) - used for preference, not rejection
     * @return Extracted answer or empty string
     */
    private static String extractAnswer(String text, java.util.Set<String> allowedSet) {
        if (text == null || text.trim().isEmpty()) return "";
        
        // Trim and take first word
        String[] words = text.trim().split("\\s+");
        if (words.length == 0) return "";
        
        String candidate = words[0];
        
        // Limit length to MAX_ANSWER_LENGTH
        if (candidate.length() > MAX_ANSWER_LENGTH) {
            candidate = candidate.substring(0, MAX_ANSWER_LENGTH);
        }
        
        // Remove trailing punctuation
        candidate = candidate.replaceAll("[.,;!?]+$", "");
        
        if (candidate.isEmpty()) return "";
        
        // NOTE: We no longer reject based on allowed set - it's used for preference in deduplication
        // This allows capturing answers even if they don't match the answer key exactly
        
        // Return original case if single letter, otherwise keep as-is
        if (candidate.length() == 1 && Character.isLetter(candidate.charAt(0))) {
            return candidate.toUpperCase(Locale.US);
        }
        
        return candidate;
    }
    
    /**
     * Number-aware parser: looks for numbered patterns like "1. A", "2) B", etc.
     * Strips leading numbers and extracts answers.
     * Updated to prefer allowed-set matches but not reject others.
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
                
                if (q >= 1 && q <= MAX_QUESTION_NUMBER && answerKey.containsKey(q) && !map.containsKey(q)) {
                    // Prefer allowed-set matches but don't reject others
                    if (allowedSet.contains(canonical)) {
                        map.put(q, answer);
                    } else if (!map.containsKey(q)) {
                        // Accept non-allowed-set answers if we don't have a better option
                        map.put(q, answer);
                    }
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
                
                if (q >= 1 && q <= MAX_QUESTION_NUMBER && allowedSet.contains(canonical) && !map.containsKey(q)) {
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
                
                if (q >= 1 && q <= MAX_QUESTION_NUMBER && allowedSet.contains(canonical) && !map.containsKey(q)) {
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
