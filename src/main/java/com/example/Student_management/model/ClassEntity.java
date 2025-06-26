package com.example.Student_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "classes")

public class ClassEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tạo Khóa chính
	@Column(name = "class_id")
	private int classId;
	
	@Column(name = "class_code")
	private String classCode;
	
	@ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
	
	@ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
	
	private int maxStudents;

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public int getMaxStudents() {
		return maxStudents;
	}

	public void setMaxStudents(int maxStudents) {
		this.maxStudents = maxStudents;
	}
	
}
