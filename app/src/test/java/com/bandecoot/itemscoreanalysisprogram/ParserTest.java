package com.bandecoot.itemscoreanalysisprogram;

import static org.junit.Assert.assertEquals;

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
}
