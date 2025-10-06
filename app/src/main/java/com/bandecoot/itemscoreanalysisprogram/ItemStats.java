package com.bandecoot.itemscoreanalysisprogram;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Compute summary statistics from the saved history JSON.
 */
public class ItemStats {

    public static class StudentResult {
        public final String student;
        public final int correct;
        public final int total;
        public final double percent;
        public final String date;

        public StudentResult(String student, int correct, int total, String date) {
            this.student = student;
            this.correct = correct;
            this.total = total;
            this.percent = total > 0 ? (100.0 * correct / total) : 0.0;
            this.date = date;
        }
    }

    public static class Stats {
        public String section;
        public String exam;
        public int N = 0;
        public double avg = 0.0;
        public double median = 0.0;
        public double stddev = 0.0;
        public double min = 0.0;
        public double max = 0.0;
        public double passRate = 0.0; // %
        public double passThresholdPct = 50.0;
        public List<StudentResult> top3 = new ArrayList<>();
        public List<StudentResult> bottom3 = new ArrayList<>();
        public List<StudentResult> all = new ArrayList<>();
    }

    /**
     * Compute summary stats for a given section/exam from your stored history JSON.
     * history JSON shape:
     * {
     *   "Section A": {
     *     "Exam 1": [
     *       {"student":"Alice","correct":"18","total":"20","date":"2025-09-01 10:00"},
     *       ...
     *     ]
     *   }
     * }
     */
    public static Stats computeExamStats(JSONObject history, String section, String exam) {
        Stats s = new Stats();
        s.section = section;
        s.exam = exam;
        s.passThresholdPct = 50.0;

        if (history == null) return s;
        if (!history.has(section)) return s;
        JSONObject sectionObj = history.optJSONObject(section);
        if (sectionObj == null || !sectionObj.has(exam)) return s;

        JSONArray arr = sectionObj.optJSONArray(exam);
        if (arr == null || arr.length() == 0) return s;

        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o == null) continue;
            String student = o.optString("student", "(unknown)");
            int correct = parseInt(o.optString("correct", "0"));
            int total = parseInt(o.optString("total", "0"));
            String date = o.optString("date", "");
            s.all.add(new StudentResult(student, correct, total, date));
        }

        s.N = s.all.size();
        if (s.N == 0) return s;

        List<StudentResult> sorted = new ArrayList<>(s.all);
        sorted.sort(Comparator.comparingDouble(r -> r.percent));

        s.min = sorted.get(0).percent;
        s.max = sorted.get(sorted.size() - 1).percent;

        // Median
        if (sorted.size() % 2 == 1) {
            s.median = sorted.get(sorted.size() / 2).percent;
        } else {
            double a = sorted.get(sorted.size() / 2 - 1).percent;
            double b = sorted.get(sorted.size() / 2).percent;
            s.median = (a + b) / 2.0;
        }

        // Average and stddev
        double sum = 0.0;
        for (StudentResult r : sorted) sum += r.percent;
        s.avg = sum / s.N;

        double var = 0.0;
        for (StudentResult r : sorted) {
            double d = r.percent - s.avg;
            var += d * d;
        }
        s.stddev = s.N > 1 ? Math.sqrt(var / (s.N - 1)) : 0.0;

        // Pass rate
        int pass = 0;
        for (StudentResult r : sorted) {
            if (r.percent >= s.passThresholdPct) pass++;
        }
        s.passRate = 100.0 * pass / s.N;

        // Top/bottom 3
        for (int i = 0; i < Math.min(3, sorted.size()); i++) s.bottom3.add(sorted.get(i));
        for (int i = 0; i < Math.min(3, sorted.size()); i++) s.top3.add(sorted.get(sorted.size() - 1 - i));

        return s;
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    @SuppressWarnings("unused")
    private static Date parseDate(String s) {
        try {
            if (s == null || s.isEmpty()) return null;
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(s);
        } catch (ParseException e) {
            return null;
        }
    }
}