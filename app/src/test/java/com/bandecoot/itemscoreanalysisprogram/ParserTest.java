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
    
    // Test that student answers differing from answer key are still captured
    // This is crucial for grading - we need to capture what the student actually wrote
    // (even if incorrect) so it can be compared against the answer key for scoring
    @Test
    public void parseNumberAnchoredRobust_capturesNonAllowedAnswers() {
        String text = "1. A\n2. X\n3. C";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B"); // Correct answer in key is B, but student answered X
        answerKey.put(3, "C");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("X", parsed.get(2)); // Should capture incorrect answer X for grading
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
    
    // New tests for header/instruction skipping and improved formats
    
    @Test
    public void parseOcrTextSmartWithFallback_ignoresHeadersAndInstructions() {
        String text = "LYCEUM OF ALABANG - BASIC EDUCATION\n" +
                     "Address: Somewhere St.\n" +
                     "EMAIL: test@test.com TEL: 123-4567\n" +
                     "NAME: ________________\n" +
                     "SECTION: ________________\n" +
                     "DATE: ________________\n" +
                     "TEACHER: ________________\n" +
                     "INSTRUCTIONS: Choose the best answer.\n" +
                     "Read each question carefully.\n" +
                     "1. A\n" +
                     "2. B\n" +
                     "3. C";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        answerKey.put(3, "C");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("B", parsed.get(2));
        assertEquals("C", parsed.get(3));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_handlesCompressedInlinePairs() {
        String text = "1. A 2.B3.C";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(1, "A");
        answerKey.put(2, "B");
        answerKey.put(3, "C");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(1));
        assertEquals("B", parsed.get(2));
        assertEquals("C", parsed.get(3));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_handlesCrossLineNumberAndAnswer() {
        String text = "___ 5.\nC\n7\nD";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(5, "C");
        answerKey.put(7, "D");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("C", parsed.get(5));
        assertEquals("D", parsed.get(7));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_handlesAnswerFirst() {
        String text = "True 30.\nFalse 31.";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(30, "True");
        answerKey.put(31, "False");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("True", parsed.get(30));
        assertEquals("False", parsed.get(31));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_handlesIdentificationWords() {
        String text = "31) Apple\n32. Banana\n33: Cherry";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(31, "Apple");
        answerKey.put(32, "Banana");
        answerKey.put(33, "Cherry");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("Apple", parsed.get(31));
        assertEquals("Banana", parsed.get(32));
        assertEquals("Cherry", parsed.get(33));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_handlesRomanNumeralsVariousPunctuation() {
        String text = "II) B\nIII. C\nIV: D";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(2, "B");
        answerKey.put(3, "C");
        answerKey.put(4, "D");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("B", parsed.get(2));
        assertEquals("C", parsed.get(3));
        assertEquals("D", parsed.get(4));
    }
    
    // New tests for the accuracy upgrades
    
    @Test
    public void parseOcrTextSmartWithFallback_lowercaseRomanNumeralsConverted() {
        String text = "ii) a\nX) b";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(2, "A");
        answerKey.put(10, "B");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("A", parsed.get(2));
        assertEquals("B", parsed.get(10));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_compressedInlinePairsYields123() {
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
    
    @Test
    public void parseOcrTextSmartWithFallback_crossLineNumberOnlyThenAnswerOnly() {
        String text = "___ 5.\n c";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(5, "C");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("C", parsed.get(5));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_answerFirstTrueYields30True() {
        String text = "True 30.";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(30, "True");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("True", parsed.get(30));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_identificationWithPunctuation() {
        String text = "31) O'Brien";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(31, "O'Brien");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("O'Brien", parsed.get(31));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_identificationWithHyphen() {
        String text = "32) re-entry";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(32, "re-entry");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("re-entry", parsed.get(32));
    }
    
    @Test
    public void parseOcrTextSmartWithFallback_identificationWithMultiplePunctuation() {
        String text = "33) O'Brien-Smith";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(33, "O'Brien-Smith");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        assertEquals("O'Brien-Smith", parsed.get(33));
    }
}
