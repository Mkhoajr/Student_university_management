package com.example.Student_management.util;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.example.Student_management.model.Grade;

public class GPACalculator {

    public static List<GPASemesterDTO> calculateGPABySemester(List<Grade> grades) {
    	
        // Nhóm theo học kỳ
        Map<String, List<Grade>> groupedBySemester = grades.stream()
                .collect(Collectors.groupingBy(Grade::getSemester));

        List<GPASemesterDTO> result = new ArrayList<>();

        for (Map.Entry<String, List<Grade>> entry : groupedBySemester.entrySet()) {
            String semester = entry.getKey();
            List<Grade> semesterGrades = entry.getValue();

            double totalCredits = 0;
            double totalWeightedScore4 = 0;
            double totalWeightedScore10 = 0;

            for (Grade grade : semesterGrades) {
                int credits = grade.getCourse().getSubject().getCredits();
                BigDecimal totalScore = grade.getTotalScore();
                if (totalScore == null) continue;

                double score10 = totalScore.doubleValue();
                double score4 = convert10To4(score10);

                totalCredits += credits;
                totalWeightedScore10 += score10 * credits;
                totalWeightedScore4 += score4 * credits;
            }

            double gpa10 = totalCredits > 0 ? totalWeightedScore10 / totalCredits : 0;
            double gpa4 = totalCredits > 0 ? totalWeightedScore4 / totalCredits : 0;
            String classification = classifyGPA(gpa4);

            result.add(new GPASemesterDTO(semester, totalCredits, round(gpa4), round(gpa10), classification));
        }

        return result;
    }

    private static double convert10To4(double score10) {
        if (score10 >= 8.5) return 4.0;
        else if (score10 >= 7.0) return 3.0;
        else if (score10 >= 5.5) return 2.0;
        else if (score10 >= 4.0) return 1.0;
        else return 0.0;
    }

    private static String classifyGPA(double gpa4) {
        if (gpa4 >= 3.6) return "Excellent";
        else if (gpa4 >= 3.2) return "Very Good";
        else if (gpa4 >= 2.5) return "Good";
        else if (gpa4 >= 2.0) return "Average";
        else return "Poor";
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
