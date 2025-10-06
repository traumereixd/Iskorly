package com.bandecoot.itemscoreanalysisprogram;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Parser {
    private static final String TAG = "ISA_PARSER";

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
}