package com.example.Student_management.util;

public class GPASemesterDTO {

	private String semester;
    private double totalCredits;
    private double gpa4;
    private double gpa10;
    private String classification;

    public GPASemesterDTO(String semester, double totalCredits, double gpa4, double gpa10, String classification) {
        this.semester = semester;
        this.totalCredits = totalCredits;
        this.gpa4 = gpa4;
        this.gpa10 = gpa10;
        this.classification = classification;
    }

    // Getters & Setters
    public String getSemester() {
        return semester;
    }

    public double getTotalCredits() {
        return totalCredits;
    }

    public double getGpa4() {
        return gpa4;
    }

    public double getGpa10() {
        return gpa10;
    }

    public String getClassification() {
        return classification;
    }
}
