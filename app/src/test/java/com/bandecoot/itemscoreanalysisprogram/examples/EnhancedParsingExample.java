package com.bandecoot.itemscoreanalysisprogram.examples;

import com.bandecoot.itemscoreanalysisprogram.AnswerConfidence;
import com.bandecoot.itemscoreanalysisprogram.ParseResult;
import com.bandecoot.itemscoreanalysisprogram.Parser;
import com.bandecoot.itemscoreanalysisprogram.RangeHint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example usage of enhanced OCR parsing features.
 * Demonstrates confidence scoring, gap detection, and mixed format support.
 */
public class EnhancedParsingExample {
    
    public static void example1_BasicConfidenceAndGaps() {
        System.out.println("\n=== Example 1: Confidence Scoring and Gap Detection ===\n");
        
        // Create answer key for questions 1-5
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        answerKey.put(3, "C");
        answerKey.put(4, "D");
        answerKey.put(5, "E");
        
        // Simulated OCR text (missing questions 2 and 4, has unusual answer for 3)
        String ocrText = "1. A\n3. XYZ#!\n5. E";
        
        // Parse with enhanced features
        ParseResult result = Parser.parseOcrTextEnhanced(ocrText, answerKey);
        
        System.out.println("Demonstrates confidence scoring and gap detection");
    }
}
