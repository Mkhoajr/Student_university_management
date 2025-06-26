package com.example.Student_management.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "teachers")
public class Teacher {

	@Id
	@Column(name = "teacher_id", length = 10)
	private String teacherId;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_teachers_users"))
	private User user;
	
	@Column(name= "teacher_name")
	private String teacherName;
	
	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;
	
	@Column(name = "hire_date")
	private LocalDate hireDate;
	
	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;

	@Column(name = "image_path")
	private String imagePath;
	
	 @OneToMany(mappedBy = "teacher")
	 private List<ClassEntity> classes;

	 
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public enum Gender {
		Nam, Nữ
	}
	
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDate getHireDate() {
		return hireDate;
	}

	public void setHireDate(LocalDate hireDate) {
		this.hireDate = hireDate;
	}

	public List<ClassEntity> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassEntity> classes) {
		this.classes = classes;
	}


	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
    public String toString() {
        return teacherId + "-" +teacherName; 
    }
	
}
