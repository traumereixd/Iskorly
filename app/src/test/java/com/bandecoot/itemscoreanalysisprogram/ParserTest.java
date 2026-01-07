package com.bandecoot.itemscoreanalysisprogram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class ParserTest {

    @Test
    public void parseOcrTextSmartWithFallback_handlesInlineNumberWithoutSpace() {
        String text = "1. A\n2. B.\n3.Z.4.C\n5. Apple";

        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        answerKey.put(3, "Z");
        answerKey.put(4, "C");
        answerKey.put(5, "Apple");

        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);

        assertEquals("C", parsed.get(4));
    }
    
    // Test various punctuation formats
    @Test
    public void parseNumberAnchoredRobust_handlesVariousPunctuationFormats() {
        String text = "1.A\n2)B\n3 - C\n4: D\n5. E";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        answerKey.put(3, "C");
        answerKey.put(4, "D");
        answerKey.put(5, "E");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("B", parsed.get(2));
        assertEquals("C", parsed.get(3));
        assertEquals("D", parsed.get(4));
        assertEquals("E", parsed.get(5));
    }
    
    // Test compressed multi-item lines
    @Test
    public void parseNumberAnchoredRobust_handlesCompressedMultiItemLines() {
        String text = "1.A2.B3.C";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        answerKey.put(3, "C");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("B", parsed.get(2));
        assertEquals("C", parsed.get(3));
    }
    
    // Test out-of-order compressed items
    @Test
    public void parseNumberAnchoredRobust_handlesOutOfOrderCompressed() {
        String text = "1.A  3.Z   5.C";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(3, "Z");
        answerKey.put(5, "C");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("Z", parsed.get(3));
        assertEquals("C", parsed.get(5));
    }
    
    // Test cross-line number-answer pairing
    @Test
    public void parseNumberAnchoredRobust_handlesCrossLinePairing() {
        String text = "1. A\n5.\nC\n7\nD";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(5, "C");
        answerKey.put(7, "D");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("C", parsed.get(5));
        assertEquals("D", parsed.get(7));
    }
    
    // Test answer-first patterns
    @Test
    public void parseNumberAnchoredRobust_handlesAnswerFirst() {
        String text = "True 1.\nFalse 2.";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "True");
        answerKey.put(2, "False");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("True", parsed.get(1));
        assertEquals("False", parsed.get(2));
    }
    
    // Test answer-first with letters
    @Test
    public void parseNumberAnchoredRobust_handlesLetterAnswerFirst() {
        String text = "A 1.\nB 2.";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("B", parsed.get(2));
    }
    
    // Test Roman numerals in various formats
    @Test
    public void parseNumberAnchoredRobust_handlesRomanNumerals() {
        String text = "I.A\nII)B\nIII - C\nIV: D\nV. E";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        answerKey.put(3, "C");
        answerKey.put(4, "D");
        answerKey.put(5, "E");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("B", parsed.get(2));
        assertEquals("C", parsed.get(3));
        assertEquals("D", parsed.get(4));
        assertEquals("E", parsed.get(5));
    }
    
    // Test out-of-order/jumbled sequences
    @Test
    public void parseNumberAnchoredRobust_handlesJumbledSequence() {
        String text = "5. E\n2. B\n1. A\n3. C\n4. D";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        answerKey.put(3, "C");
        answerKey.put(4, "D");
        answerKey.put(5, "E");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("B", parsed.get(2));
        assertEquals("C", parsed.get(3));
        assertEquals("D", parsed.get(4));
        assertEquals("E", parsed.get(5));
    }
    
    // Test duplicate handling with allowed-set preference
    @Test
    public void parseNumberAnchoredRobust_handlesDuplicatesWithAllowedSetPreference() {
        // First occurrence is wrong, second is in allowed set
        String text = "1. X\n1. A";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        // Should prefer "A" because it's in the allowed set
        assertEquals("A", parsed.get(1));
    }
    
    // Test mixed formats in one document
    @Test
    public void parseNumberAnchoredRobust_handlesMixedFormats() {
        String text = "1.A\n2) B\n3 - C\nD 4.\n5.\nE\nVI:F\n7.G8.H";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        answerKey.put(3, "C");
        answerKey.put(4, "D");
        answerKey.put(5, "E");
        answerKey.put(6, "F");
        answerKey.put(7, "G");
        answerKey.put(8, "H");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("B", parsed.get(2));
        assertEquals("C", parsed.get(3));
        assertEquals("D", parsed.get(4));
        assertEquals("E", parsed.get(5));
        assertEquals("F", parsed.get(6));
        assertEquals("G", parsed.get(7));
        assertEquals("H", parsed.get(8));
    }
    
    // Test word answers
    @Test
    public void parseNumberAnchoredRobust_handlesWordAnswers() {
        String text = "1. Apple\n2)Banana\n3 - Cherry";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "Apple");
        answerKey.put(2, "Banana");
        answerKey.put(3, "Cherry");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("Apple", parsed.get(1));
        assertEquals("Banana", parsed.get(2));
        assertEquals("Cherry", parsed.get(3));
    }
    
    // Test that non-allowed answers are still captured
    @Test
    public void parseNumberAnchoredRobust_capturesNonAllowedAnswers() {
        String text = "1. A\n2. X\n3. C";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B"); // Correct answer in key is B, but OCR parsed X from student sheet
        answerKey.put(3, "C");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("X", parsed.get(2)); // Should still capture X even though it doesn't match the answer key
        assertEquals("C", parsed.get(3));
    }
    
    // Test empty answer key returns empty
    @Test
    public void parseOcrTextSmartWithFallback_emptyAnswerKeyReturnsEmpty() {
        String text = "1. A\n2. B\n3. C";
        Map<Integer, String> answerKey = new HashMap<>();
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertNotNull(parsed);
        assertEquals(0, parsed.size());
    }
    
    // Test null text returns empty
    @Test
    public void parseOcrTextSmartWithFallback_nullTextReturnsEmpty() {
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(null, answerKey);
        
        assertNotNull(parsed);
        assertEquals(0, parsed.size());
    }
}
