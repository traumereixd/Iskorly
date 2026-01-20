package com.bandecoot.itemscoreanalysisprogram;

/**
 * Model class representing a type hint for a question range.
 * Used to improve parser accuracy on mixed-format exams.
 */
public class RangeHint {
    /**
     * Question type enum
     */
    public enum QuestionType {
        MULTIPLE_CHOICE("Multiple Choice"),
        MATCHING("Matching"),
        IDENTIFICATION("Identification"),
        TRUE_FALSE("True/False"),
        ENUMERATION("Enumeration");
        
        private final String displayName;
        
        QuestionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static QuestionType fromDisplayName(String displayName) {
            for (QuestionType type : values()) {
                if (type.displayName.equals(displayName)) {
                    return type;
                }
            }
            return MULTIPLE_CHOICE; // Default fallback
        }
    }
    
    private int startQuestion;
    private int endQuestion;
    private QuestionType type;
    
    public RangeHint(int startQuestion, int endQuestion, QuestionType type) {
        this.startQuestion = startQuestion;
        this.endQuestion = endQuestion;
        this.type = type;
    }
    
    public int getStartQuestion() {
        return startQuestion;
    }
    
    public void setStartQuestion(int startQuestion) {
        this.startQuestion = startQuestion;
    }
    
    public int getEndQuestion() {
        return endQuestion;
    }
    
    public void setEndQuestion(int endQuestion) {
        this.endQuestion = endQuestion;
    }
    
    public QuestionType getType() {
        return type;
    }
    
    public void setType(QuestionType type) {
        this.type = type;
    }
    
    /**
     * Check if a question number falls within this range
     */
    public boolean contains(int questionNumber) {
        return questionNumber >= startQuestion && questionNumber <= endQuestion;
    }
}
