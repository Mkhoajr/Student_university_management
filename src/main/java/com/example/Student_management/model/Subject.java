package com.example.Student_management.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")

public class Subject {

	@Id
	@Column(name = "subject_id", length = 10)
	private String subjectId;
	
	@Column(name = "subject_name", length = 50)
	private String subjectName;
	
	@Column(name = "credits")
	private int credits;

	 @OneToMany(mappedBy = "subject")
	 private List<ClassEntity> classes;
	 
	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}
	
	@Override
    public String toString() {
        return subjectName; // Hiển thị tên trong ComboBox
    }
	
}
