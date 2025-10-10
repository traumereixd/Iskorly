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
        
        // Advanced analytics (Phase 2)
        public double difficulty = 0.0;        // p-value (proportion correct)
        public double discrimination = 0.0;    // Point-biserial correlation
        public double upperPercent = 0.0;      // % correct in upper 27%
        public double lowerPercent = 0.0;      // % correct in lower 27%
        public double upperLowerDelta = 0.0;   // Difference between upper and lower
        
        public QuestionStat(int questionNum) {
            this.questionNum = questionNum;
        }
        
        public void compute() {
            if (attemptCount > 0) {
                percentCorrect = 100.0 * correctCount / attemptCount;
                difficulty = (double) correctCount / attemptCount; // p-value [0,1]
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
        
        // Advanced analytics (Phase 2)
        public double kr20 = 0.0;         // KR-20 reliability coefficient
        public double cronbachAlpha = 0.0; // Cronbach's alpha
        public double kr21 = 0.0;         // KR-21 fallback estimate
        public double sem = 0.0;          // Standard Error of Measurement
        
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
    
    // ========== Advanced Analytics (Phase 2) ==========
    
    /**
     * Student result used for discrimination and upper/lower analysis.
     */
    private static class StudentResult {
        final String student;
        final int totalScore;
        final Map<Integer, Boolean> itemScores; // Q# -> correct/incorrect
        
        StudentResult(String student, int totalScore, Map<Integer, Boolean> itemScores) {
            this.student = student;
            this.totalScore = totalScore;
            this.itemScores = itemScores;
        }
    }
    
    /**
     * Compute point-biserial discrimination for each item.
     * Discrimination = correlation between item score (0/1) and total test score.
     * 
     * @param historyArray History records
     * @param answerKey Answer key
     * @param filterExam Optional exam filter
     * @param filterSection Optional section filter
     * @param stats Map to update with discrimination values
     */
    public static void computeDiscrimination(
            JSONArray historyArray,
            Map<Integer, String> answerKey,
            String filterExam,
            String filterSection,
            Map<Integer, QuestionStat> stats) {
        
        if (stats == null || stats.isEmpty()) return;
        
        // Build list of student results
        List<StudentResult> students = new ArrayList<>();
        
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
            
            String student = record.optString("student", "");
            JSONObject answers = record.optJSONObject("answers");
            if (answers == null) continue;
            
            Map<Integer, Boolean> itemScores = new HashMap<>();
            int totalScore = 0;
            
            for (Map.Entry<Integer, String> keyEntry : answerKey.entrySet()) {
                int q = keyEntry.getKey();
                String correctAnswer = keyEntry.getValue();
                String studentAnswer = answers.optString(String.valueOf(q), null);
                
                if (studentAnswer != null && !studentAnswer.isEmpty()) {
                    boolean correct = correctAnswer.equalsIgnoreCase(studentAnswer);
                    itemScores.put(q, correct);
                    if (correct) totalScore++;
                }
            }
            
            students.add(new StudentResult(student, totalScore, itemScores));
        }
        
        if (students.size() < 2) return; // Need at least 2 students
        
        // Compute point-biserial for each item
        for (Integer q : answerKey.keySet()) {
            QuestionStat stat = stats.get(q);
            if (stat == null) continue;
            
            double pbis = computePointBiserial(students, q);
            stat.discrimination = pbis;
        }
    }
    
    /**
     * Compute point-biserial correlation for a single item.
     * Formula: r_pbis = (M1 - M0) / SD_total * sqrt(p * q)
     * where M1 = mean total score of those who got item correct
     *       M0 = mean total score of those who got item wrong
     *       SD_total = standard deviation of total scores
     *       p = proportion correct, q = 1 - p
     */
    private static double computePointBiserial(List<StudentResult> students, int questionNum) {
        if (students.isEmpty()) return 0.0;
        
        List<Integer> scoresCorrect = new ArrayList<>();
        List<Integer> scoresIncorrect = new ArrayList<>();
        List<Integer> allScores = new ArrayList<>();
        
        for (StudentResult sr : students) {
            Boolean correct = sr.itemScores.get(questionNum);
            if (correct != null) {
                allScores.add(sr.totalScore);
                if (correct) {
                    scoresCorrect.add(sr.totalScore);
                } else {
                    scoresIncorrect.add(sr.totalScore);
                }
            }
        }
        
        if (scoresCorrect.isEmpty() || scoresIncorrect.isEmpty() || allScores.size() < 2) {
            return 0.0;
        }
        
        // Mean of correct group
        double sumCorrect = 0;
        for (int s : scoresCorrect) sumCorrect += s;
        double meanCorrect = sumCorrect / scoresCorrect.size();
        
        // Mean of incorrect group
        double sumIncorrect = 0;
        for (int s : scoresIncorrect) sumIncorrect += s;
        double meanIncorrect = sumIncorrect / scoresIncorrect.size();
        
        // SD of all scores
        double sumAll = 0;
        for (int s : allScores) sumAll += s;
        double meanAll = sumAll / allScores.size();
        
        double sumSqDiff = 0;
        for (int s : allScores) {
            double diff = s - meanAll;
            sumSqDiff += diff * diff;
        }
        double sdAll = Math.sqrt(sumSqDiff / allScores.size());
        
        if (sdAll == 0) return 0.0;
        
        // Proportion correct
        double p = (double) scoresCorrect.size() / allScores.size();
        double q = 1.0 - p;
        
        // Point-biserial formula
        double pbis = (meanCorrect - meanIncorrect) / sdAll * Math.sqrt(p * q);
        
        return pbis;
    }
    
    /**
     * Compute upper/lower 27% analysis for each item.
     * Split students by total score, compare % correct in upper 27% vs lower 27%.
     * 
     * @param historyArray History records
     * @param answerKey Answer key
     * @param filterExam Optional exam filter
     * @param filterSection Optional section filter
     * @param stats Map to update with upper/lower percentages
     */
    public static void computeUpperLower27(
            JSONArray historyArray,
            Map<Integer, String> answerKey,
            String filterExam,
            String filterSection,
            Map<Integer, QuestionStat> stats) {
        
        if (stats == null || stats.isEmpty()) return;
        
        // Build list of student results
        List<StudentResult> students = new ArrayList<>();
        
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
            
            String student = record.optString("student", "");
            JSONObject answers = record.optJSONObject("answers");
            if (answers == null) continue;
            
            Map<Integer, Boolean> itemScores = new HashMap<>();
            int totalScore = 0;
            
            for (Map.Entry<Integer, String> keyEntry : answerKey.entrySet()) {
                int q = keyEntry.getKey();
                String correctAnswer = keyEntry.getValue();
                String studentAnswer = answers.optString(String.valueOf(q), null);
                
                if (studentAnswer != null && !studentAnswer.isEmpty()) {
                    boolean correct = correctAnswer.equalsIgnoreCase(studentAnswer);
                    itemScores.put(q, correct);
                    if (correct) totalScore++;
                }
            }
            
            students.add(new StudentResult(student, totalScore, itemScores));
        }
        
        if (students.size() < 10) return; // Need reasonable sample size
        
        // Sort by total score descending
        Collections.sort(students, (a, b) -> Integer.compare(b.totalScore, a.totalScore));
        
        // Get top 27% and bottom 27%
        int n27 = Math.max(1, (int) Math.ceil(students.size() * 0.27));
        List<StudentResult> upperGroup = students.subList(0, Math.min(n27, students.size()));
        List<StudentResult> lowerGroup = students.subList(
            Math.max(0, students.size() - n27), students.size());
        
        // Compute % correct for each item in upper and lower groups
        for (Integer q : answerKey.keySet()) {
            QuestionStat stat = stats.get(q);
            if (stat == null) continue;
            
            // Upper group
            int upperCorrect = 0;
            int upperAttempt = 0;
            for (StudentResult sr : upperGroup) {
                Boolean correct = sr.itemScores.get(q);
                if (correct != null) {
                    upperAttempt++;
                    if (correct) upperCorrect++;
                }
            }
            stat.upperPercent = upperAttempt > 0 ? 100.0 * upperCorrect / upperAttempt : 0.0;
            
            // Lower group
            int lowerCorrect = 0;
            int lowerAttempt = 0;
            for (StudentResult sr : lowerGroup) {
                Boolean correct = sr.itemScores.get(q);
                if (correct != null) {
                    lowerAttempt++;
                    if (correct) lowerCorrect++;
                }
            }
            stat.lowerPercent = lowerAttempt > 0 ? 100.0 * lowerCorrect / lowerAttempt : 0.0;
            
            stat.upperLowerDelta = stat.upperPercent - stat.lowerPercent;
        }
    }
    
    /**
     * Compute reliability metrics (KR-20, Cronbach's alpha, KR-21) and SEM.
     * Updates the SectionSummary with reliability and SEM values.
     * 
     * @param historyArray History records
     * @param answerKey Answer key
     * @param filterExam Optional exam filter
     * @param filterSection Optional section filter
     * @param summary SectionSummary to update with reliability metrics
     */
    public static void computeReliability(
            JSONArray historyArray,
            Map<Integer, String> answerKey,
            String filterExam,
            String filterSection,
            SectionSummary summary) {
        
        if (summary == null || answerKey == null || answerKey.isEmpty()) return;
        
        // Collect all student results
        List<StudentResult> students = new ArrayList<>();
        
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
            
            JSONObject answers = record.optJSONObject("answers");
            if (answers == null) continue;
            
            Map<Integer, Boolean> itemScores = new HashMap<>();
            int totalScore = 0;
            
            for (Map.Entry<Integer, String> keyEntry : answerKey.entrySet()) {
                int q = keyEntry.getKey();
                String correctAnswer = keyEntry.getValue();
                String studentAnswer = answers.optString(String.valueOf(q), null);
                
                if (studentAnswer != null && !studentAnswer.isEmpty()) {
                    boolean correct = correctAnswer.equalsIgnoreCase(studentAnswer);
                    itemScores.put(q, correct);
                    if (correct) totalScore++;
                }
            }
            
            students.add(new StudentResult("", totalScore, itemScores));
        }
        
        if (students.size() < 2) return;
        
        int k = answerKey.size(); // number of items
        
        // Compute variance of total test scores
        List<Integer> totalScores = new ArrayList<>();
        for (StudentResult sr : students) {
            totalScores.add(sr.totalScore);
        }
        
        double meanTotal = 0;
        for (int s : totalScores) meanTotal += s;
        meanTotal /= totalScores.size();
        
        double varTotal = 0;
        for (int s : totalScores) {
            double diff = s - meanTotal;
            varTotal += diff * diff;
        }
        varTotal /= totalScores.size();
        
        // Compute item variances and mean difficulties
        double sumItemVar = 0.0;
        double sumP = 0.0;
        
        for (Integer q : answerKey.keySet()) {
            int correct = 0;
            int attempt = 0;
            
            for (StudentResult sr : students) {
                Boolean isCorrect = sr.itemScores.get(q);
                if (isCorrect != null) {
                    attempt++;
                    if (isCorrect) correct++;
                }
            }
            
            if (attempt > 0) {
                double p = (double) correct / attempt;
                double itemVar = p * (1.0 - p); // variance of dichotomous item
                sumItemVar += itemVar;
                sumP += p;
            }
        }
        
        // KR-20 = (k / (k-1)) * (1 - sum(item variances) / total variance)
        if (k > 1 && varTotal > 0) {
            summary.kr20 = (k / (double)(k - 1)) * (1.0 - sumItemVar / varTotal);
            summary.kr20 = Math.max(0.0, Math.min(1.0, summary.kr20)); // clamp [0,1]
        }
        
        // Cronbach's alpha (same as KR-20 for dichotomous items)
        summary.cronbachAlpha = summary.kr20;
        
        // KR-21 = (k / (k-1)) * (1 - (M * (k - M)) / (k * variance))
        if (k > 1 && varTotal > 0) {
            summary.kr21 = (k / (double)(k - 1)) * (1.0 - (meanTotal * (k - meanTotal)) / (k * varTotal));
            summary.kr21 = Math.max(0.0, Math.min(1.0, summary.kr21));
        }
        
        // SEM = SD * sqrt(1 - reliability)
        // Use KR-20 as primary reliability estimate
        double reliability = summary.kr20;
        if (reliability > 0 && varTotal > 0) {
            double sdTotal = Math.sqrt(varTotal);
            summary.sem = sdTotal * Math.sqrt(1.0 - reliability);
        }
    }
}
