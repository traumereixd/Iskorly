package com.bandecoot.itemscoreanalysisprogram;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Feature #5: Masterlist - Compute per-question statistics across all saved records.
 * Enhanced with per-section analytics and summary statistics.
 */
public class QuestionStats {
    
    public static class QuestionStat {
        public final int questionNum;
        public int correctCount = 0;
        public int incorrectCount = 0;
        public int attemptCount = 0;
        public double percentCorrect = 0.0;
        public String mostCommonWrong = "";
        public int mostCommonWrongCount = 0;
        
        public QuestionStat(int questionNum) {
            this.questionNum = questionNum;
        }
        
        public void compute() {
            if (attemptCount > 0) {
                percentCorrect = 100.0 * correctCount / attemptCount;
            }
        }
    }
    
    /**
     * Summary statistics for a section or overall dataset.
     */
    public static class SectionSummary {
        public String sectionName;
        public int totalScore = 0;        // Sum of all scores
        public double mean = 0.0;         // Average percentage
        public double stdDev = 0.0;       // Standard deviation of percentages
        public double mps = 0.0;          // Mean Percentage Score (same as mean)
        public int recordCount = 0;       // Number of records
        
        public SectionSummary(String sectionName) {
            this.sectionName = sectionName;
        }
    }
    
    /**
     * Compute per-question statistics from history records.
     * 
     * @param historyArray JSONArray of all history records
     * @param answerKey The answer key to use for correct answers
     * @param filterExam Optional: filter by exam name (null = all)
     * @param filterSection Optional: filter by section name (null = all)
     * @return Map of question number to QuestionStat
     */
    public static Map<Integer, QuestionStat> computeQuestionStats(
            JSONArray historyArray,
            Map<Integer, String> answerKey,
            String filterExam,
            String filterSection) {
        
        Map<Integer, QuestionStat> stats = new HashMap<>();
        Map<Integer, Map<String, Integer>> wrongAnswers = new HashMap<>();
        
        // Initialize stats for each question in answer key
        for (Integer q : answerKey.keySet()) {
            stats.put(q, new QuestionStat(q));
            wrongAnswers.put(q, new HashMap<>());
        }
        
        // Process each history record
        for (int i = 0; i < historyArray.length(); i++) {
            JSONObject record = historyArray.optJSONObject(i);
            if (record == null) continue;
            
            // Apply filters
            String exam = record.optString("exam", "");
            String section = record.optString("section", "");
            
            if (filterExam != null && !filterExam.isEmpty() && !exam.equalsIgnoreCase(filterExam)) {
                continue;
            }
            if (filterSection != null && !filterSection.isEmpty() && !section.equalsIgnoreCase(filterSection)) {
                continue;
            }
            
            // Get answers from this record
            JSONObject answers = record.optJSONObject("answers");
            if (answers == null) continue;
            
            // Check each question in answer key
            for (Map.Entry<Integer, String> keyEntry : answerKey.entrySet()) {
                int q = keyEntry.getKey();
                String correctAnswer = keyEntry.getValue();
                
                QuestionStat stat = stats.get(q);
                if (stat == null) continue;
                
                String studentAnswer = answers.optString(String.valueOf(q), null);
                if (studentAnswer == null || studentAnswer.isEmpty()) {
                    // Student didn't answer this question
                    continue;
                }
                
                stat.attemptCount++;
                
                if (correctAnswer.equalsIgnoreCase(studentAnswer)) {
                    stat.correctCount++;
                } else {
                    stat.incorrectCount++;
                    
                    // Track wrong answers
                    Map<String, Integer> wrongMap = wrongAnswers.get(q);
                    if (wrongMap != null) {
                        wrongMap.put(studentAnswer, wrongMap.getOrDefault(studentAnswer, 0) + 1);
                    }
                }
            }
        }
        
        // Compute percentages and find most common wrong answer
        for (Map.Entry<Integer, QuestionStat> entry : stats.entrySet()) {
            QuestionStat stat = entry.getValue();
            stat.compute();
            
            // Find most common wrong answer
            Map<String, Integer> wrongMap = wrongAnswers.get(entry.getKey());
            if (wrongMap != null && !wrongMap.isEmpty()) {
                String mostCommon = "";
                int maxCount = 0;
                for (Map.Entry<String, Integer> wrongEntry : wrongMap.entrySet()) {
                    if (wrongEntry.getValue() > maxCount) {
                        maxCount = wrongEntry.getValue();
                        mostCommon = wrongEntry.getKey();
                    }
                }
                stat.mostCommonWrong = mostCommon;
                stat.mostCommonWrongCount = maxCount;
            }
        }
        
        return stats;
    }
    
    /**
     * Get sorted list of question numbers from the stats map.
     */
    public static List<Integer> getSortedQuestions(Map<Integer, QuestionStat> stats) {
        List<Integer> questions = new ArrayList<>(stats.keySet());
        Collections.sort(questions);
        return questions;
    }
    
    /**
     * Compute per-section question statistics.
     * Returns a map of section name to per-question stats for that section.
     */
    public static Map<String, Map<Integer, QuestionStat>> computePerSectionStats(
            JSONArray historyArray,
            Map<Integer, String> answerKey,
            String filterExam) {
        
        Map<String, Map<Integer, QuestionStat>> sectionStats = new HashMap<>();
        Map<String, Map<Integer, Map<String, Integer>>> sectionWrongAnswers = new HashMap<>();
        
        // Process each history record
        for (int i = 0; i < historyArray.length(); i++) {
            JSONObject record = historyArray.optJSONObject(i);
            if (record == null) continue;
            
            // Apply exam filter
            String exam = record.optString("exam", "");
            if (filterExam != null && !filterExam.isEmpty() && !exam.equalsIgnoreCase(filterExam)) {
                continue;
            }
            
            String section = record.optString("section", "Unsectioned");
            
            // Initialize section if needed
            if (!sectionStats.containsKey(section)) {
                Map<Integer, QuestionStat> stats = new HashMap<>();
                for (Integer q : answerKey.keySet()) {
                    stats.put(q, new QuestionStat(q));
                }
                sectionStats.put(section, stats);
                sectionWrongAnswers.put(section, new HashMap<>());
            }
            
            // Get answers from this record
            JSONObject answers = record.optJSONObject("answers");
            if (answers == null) continue;
            
            Map<Integer, QuestionStat> stats = sectionStats.get(section);
            Map<Integer, Map<String, Integer>> wrongAnswers = sectionWrongAnswers.get(section);
            
            // Check each question in answer key
            for (Map.Entry<Integer, String> keyEntry : answerKey.entrySet()) {
                int q = keyEntry.getKey();
                String correctAnswer = keyEntry.getValue();
                
                QuestionStat stat = stats.get(q);
                if (stat == null) continue;
                
                String studentAnswer = answers.optString(String.valueOf(q), null);
                if (studentAnswer == null || studentAnswer.isEmpty()) {
                    continue;
                }
                
                stat.attemptCount++;
                
                if (correctAnswer.equalsIgnoreCase(studentAnswer)) {
                    stat.correctCount++;
                } else {
                    stat.incorrectCount++;
                    
                    // Track wrong answers
                    if (!wrongAnswers.containsKey(q)) {
                        wrongAnswers.put(q, new HashMap<>());
                    }
                    Map<String, Integer> wrongMap = wrongAnswers.get(q);
                    wrongMap.put(studentAnswer, wrongMap.getOrDefault(studentAnswer, 0) + 1);
                }
            }
        }
        
        // Compute percentages and find most common wrong answer for each section
        for (String section : sectionStats.keySet()) {
            Map<Integer, QuestionStat> stats = sectionStats.get(section);
            Map<Integer, Map<String, Integer>> wrongAnswers = sectionWrongAnswers.get(section);
            
            for (Map.Entry<Integer, QuestionStat> entry : stats.entrySet()) {
                QuestionStat stat = entry.getValue();
                stat.compute();
                
                // Find most common wrong answer
                Map<String, Integer> wrongMap = wrongAnswers.get(entry.getKey());
                if (wrongMap != null && !wrongMap.isEmpty()) {
                    String mostCommon = "";
                    int maxCount = 0;
                    for (Map.Entry<String, Integer> wrongEntry : wrongMap.entrySet()) {
                        if (wrongEntry.getValue() > maxCount) {
                            maxCount = wrongEntry.getValue();
                            mostCommon = wrongEntry.getKey();
                        }
                    }
                    stat.mostCommonWrong = mostCommon;
                    stat.mostCommonWrongCount = maxCount;
                }
            }
        }
        
        return sectionStats;
    }
    
    /**
     * Compute section summary statistics (Overall Score, Mean, SD, MPS).
     */
    public static SectionSummary computeSectionSummary(
            JSONArray historyArray,
            String sectionName,
            String filterExam) {
        
        SectionSummary summary = new SectionSummary(sectionName);
        List<Double> percentages = new ArrayList<>();
        
        for (int i = 0; i < historyArray.length(); i++) {
            JSONObject record = historyArray.optJSONObject(i);
            if (record == null) continue;
            
            // Apply filters
            String exam = record.optString("exam", "");
            String section = record.optString("section", "Unsectioned");
            
            if (filterExam != null && !filterExam.isEmpty() && !exam.equalsIgnoreCase(filterExam)) {
                continue;
            }
            if (!section.equalsIgnoreCase(sectionName)) {
                continue;
            }
            
            int score = record.optInt("score", 0);
            double percent = record.optDouble("percent", 0.0);
            
            summary.totalScore += score;
            percentages.add(percent);
            summary.recordCount++;
        }
        
        // Compute mean
        if (!percentages.isEmpty()) {
            double sum = 0.0;
            for (double p : percentages) {
                sum += p;
            }
            summary.mean = sum / percentages.size();
            summary.mps = summary.mean; // MPS is same as mean
            
            // Compute standard deviation
            double sumSquaredDiff = 0.0;
            for (double p : percentages) {
                double diff = p - summary.mean;
                sumSquaredDiff += diff * diff;
            }
            summary.stdDev = Math.sqrt(sumSquaredDiff / percentages.size());
        }
        
        return summary;
    }
    
    /**
     * Compute overall summary across all sections.
     */
    public static SectionSummary computeOverallSummary(
            JSONArray historyArray,
            String filterExam) {
        
        SectionSummary summary = new SectionSummary("Overall");
        List<Double> percentages = new ArrayList<>();
        
        for (int i = 0; i < historyArray.length(); i++) {
            JSONObject record = historyArray.optJSONObject(i);
            if (record == null) continue;
            
            // Apply exam filter
            String exam = record.optString("exam", "");
            if (filterExam != null && !filterExam.isEmpty() && !exam.equalsIgnoreCase(filterExam)) {
                continue;
            }
            
            int score = record.optInt("score", 0);
            double percent = record.optDouble("percent", 0.0);
            
            summary.totalScore += score;
            percentages.add(percent);
            summary.recordCount++;
        }
        
        // Compute mean
        if (!percentages.isEmpty()) {
            double sum = 0.0;
            for (double p : percentages) {
                sum += p;
            }
            summary.mean = sum / percentages.size();
            summary.mps = summary.mean;
            
            // Compute standard deviation
            double sumSquaredDiff = 0.0;
            for (double p : percentages) {
                double diff = p - summary.mean;
                sumSquaredDiff += diff * diff;
            }
            summary.stdDev = Math.sqrt(sumSquaredDiff / percentages.size());
        }
        
        return summary;
    }
    
    /**
     * Get list of unique section names from history.
     */
    public static List<String> getUniqueSections(JSONArray historyArray, String filterExam) {
        List<String> sections = new ArrayList<>();
        for (int i = 0; i < historyArray.length(); i++) {
            JSONObject record = historyArray.optJSONObject(i);
            if (record == null) continue;
            
            String exam = record.optString("exam", "");
            if (filterExam != null && !filterExam.isEmpty() && !exam.equalsIgnoreCase(filterExam)) {
                continue;
            }
            
            String section = record.optString("section", "Unsectioned");
            if (!sections.contains(section)) {
                sections.add(section);
            }
        }
        Collections.sort(sections);
        return sections;
    }
}
