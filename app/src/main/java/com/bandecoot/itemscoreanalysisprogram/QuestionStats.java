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
}
