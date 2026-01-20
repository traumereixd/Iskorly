package com.bandecoot.itemscoreanalysisprogram;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class DebugTest {
    @Test
    public void debugLowercaseRomanNumerals() {
        String text = "ii) a\nX) b";
        
        Map<Integer, String> answerKey = new HashMap<>();
        answerKey.put(2, "A");
        answerKey.put(10, "B");
        
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextSmartWithFallback(text, answerKey);
        
        System.out.println("Parsed results:");
        for (Map.Entry<Integer, String> entry : parsed.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println("Answer key:");
        for (Map.Entry<Integer, String> entry : answerKey.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
