package com.bandecoot.itemscoreanalysisprogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced parsing result with confidence scoring and gap detection.
 * Contains parsed answers along with quality metadata for UI display.
 */
public class ParseResult {
    private final LinkedHashMap<Integer, String> answers;
    private final Map<Integer, AnswerConfidence> confidenceMap;
    private final List<Integer> missingQuestions;
    private final int lowConfidenceCount;
    
    public ParseResult(LinkedHashMap<Integer, String> answers, 
                      Map<Integer, AnswerConfidence> confidenceMap,
                      List<Integer> missingQuestions) {
        this.answers = answers != null ? answers : new LinkedHashMap<>();
        this.confidenceMap = confidenceMap != null ? confidenceMap : new HashMap<>();
        this.missingQuestions = missingQuestions != null ? missingQuestions : new ArrayList<>();
        
        // Count low confidence answers
        int lowCount = 0;
        for (AnswerConfidence conf : this.confidenceMap.values()) {
            if (conf.isLowConfidence()) {
                lowCount++;
            }
        }
        this.lowConfidenceCount = lowCount;
    }
    
    /**
     * Get the parsed answers map
     */
    public LinkedHashMap<Integer, String> getAnswers() {
        return answers;
    }
    
    /**
     * Get confidence metadata for a specific question
     */
    public AnswerConfidence getConfidence(int questionNumber) {
        return confidenceMap.get(questionNumber);
    }
    
    /**
     * Get all confidence metadata
     */
    public Map<Integer, AnswerConfidence> getConfidenceMap() {
        return confidenceMap;
    }
    
    /**
     * Get list of missing question numbers in the detected range
     */
    public List<Integer> getMissingQuestions() {
        return missingQuestions;
    }
    
    /**
     * Get count of low-confidence answers
     */
    public int getLowConfidenceCount() {
        return lowConfidenceCount;
    }
    
    /**
     * Get formatted summary of missing questions (e.g., "3, 7, 12")
     */
    public String getMissingQuestionsSummary() {
        if (missingQuestions.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < missingQuestions.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(missingQuestions.get(i));
        }
        return sb.toString();
    }
    
    /**
     * Check if there are any missing questions
     */
    public boolean hasMissingQuestions() {
        return !missingQuestions.isEmpty();
    }
    
    /**
     * Get a display label for UI showing warnings
     */
    public String getWarningLabel() {
        List<String> warnings = new ArrayList<>();
        
        if (hasMissingQuestions()) {
            warnings.add("Missing: " + getMissingQuestionsSummary());
        }
        
        if (lowConfidenceCount > 0) {
            warnings.add(lowConfidenceCount + " low-confidence");
        }
        
        if (warnings.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < warnings.size(); i++) {
            if (i > 0) sb.append(" | ");
            sb.append(warnings.get(i));
        }
        return sb.toString();
    }
}
