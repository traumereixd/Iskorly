package com.bandecoot.itemscoreanalysisprogram;

/**
 * Represents confidence metadata for a parsed answer.
 * Used to highlight low-confidence answers in the UI and provide quality metrics.
 */
public class AnswerConfidence {
    private final int questionNumber;
    private final String answer;
    private final float confidenceScore; // 0.0 to 1.0
    private final String[] flags; // e.g., "TOO_SHORT", "UNUSUAL_CHARS", "NOT_IN_ALLOWED_SET"
    
    public AnswerConfidence(int questionNumber, String answer, float confidenceScore, String[] flags) {
        this.questionNumber = questionNumber;
        this.answer = answer;
        this.confidenceScore = Math.max(0.0f, Math.min(1.0f, confidenceScore)); // Clamp to [0,1]
        this.flags = flags != null ? flags : new String[0];
    }
    
    public int getQuestionNumber() {
        return questionNumber;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public float getConfidenceScore() {
        return confidenceScore;
    }
    
    public String[] getFlags() {
        return flags;
    }
    
    public boolean isLowConfidence() {
        return confidenceScore < 0.5f;
    }
    
    public boolean hasFlag(String flag) {
        for (String f : flags) {
            if (f.equals(flag)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Q").append(questionNumber)
          .append(": ").append(answer)
          .append(" (confidence: ").append(String.format("%.2f", confidenceScore));
        if (flags.length > 0) {
            sb.append(", flags: ");
            for (int i = 0; i < flags.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(flags[i]);
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
