package com.example.Student_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")

public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "department_id")
	private int departmentId;
	
	@Column(name = "department_name")
	private String departmentName;

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	@Override
    public String toString() {
        return departmentName; // Hiển thị tên trong ComboBox
    }
	
}
