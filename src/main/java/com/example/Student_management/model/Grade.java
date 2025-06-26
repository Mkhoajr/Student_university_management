package com.example.Student_management.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "grades")

public class Grade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tạo Khóa chính
	@Column(name = "grade_id")
	private int gradeId;
	
	@ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

	@ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    
    @PrePersist
    @PreUpdate
    // Tính tổng trước khi lưu hoặc cập nhật vào DB
    private void preSave() {
     calculateTotalScore();
    }
    
    @Column(name = "semester")
	private String semester;
    
    @Column(name = "midterm_score", nullable = true)
	private BigDecimal midtermScore;	
    
    @Column(name = "final_score", nullable = true)
	private BigDecimal finalScore;
    
	@Column(name = "assignment_score", nullable = true)
	private BigDecimal assignmentScore;
	
	@Column(name = "total_score", nullable = true)
	private BigDecimal totalScore;
	
	@Column(name = "letter_score", nullable = true)
	private String letterGrade;
	
	public String getLetterGrade() {
		return letterGrade;
	}

	public void setLetterGrade(String letterGrade) {
		this.letterGrade = letterGrade;
	}


	public int getGradeId() {
		return gradeId;
	}

	public void setGradeId(int gradeId) {
		this.gradeId = gradeId;
	}


	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public BigDecimal getMidtermScore() {
		return midtermScore;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public void setMidtermScore(BigDecimal midtermScore) {
		this.midtermScore = midtermScore;
	}

	public BigDecimal getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(BigDecimal finalScore) {
		this.finalScore = finalScore;
	}

	public BigDecimal getAssignmentScore() {
		return assignmentScore;
	}

	public void setAssignmentScore(BigDecimal assignmentScore) {
		this.assignmentScore = assignmentScore;
	}

	public BigDecimal getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}
	
	@SuppressWarnings("deprecation")
	private void calculateTotalScore() {
	    if (midtermScore != null && finalScore != null && assignmentScore != null) {
	        BigDecimal sum = midtermScore.add(finalScore).add(assignmentScore);
	        this.totalScore = sum.divide(BigDecimal.valueOf(3), 2, BigDecimal.ROUND_HALF_UP);
	        this.letterGrade = convertToLetterGrade(this.totalScore); // Gán điểm chữ
	    } else {
	        this.totalScore = null; // hoặc BigDecimal.ZERO tùy yêu cầu
	    }
	}

	private String convertToLetterGrade(BigDecimal score) {
	    if (score == null) return null;

	    double s = score.doubleValue();
	    if (s >= 8.5) return "A";
	    else if (s >= 7.0) return "B";
	    else if (s >= 5.5) return "C";
	    else if (s >= 4.0) return "D";
	    else return "F";
	}

}
