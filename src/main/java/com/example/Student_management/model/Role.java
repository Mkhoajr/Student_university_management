package com.example.Student_management.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")

public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tạo Khóa chính
	@Column(name = "role_id")
	private int roleId;
	
	@Column(name = "role_name")
	private String roleName;

	@OneToMany(mappedBy = "role")
	private List<User> users;
	
	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}
	
	 @Override
	    public String toString() {
	        return roleName; 
	    }

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}	
	
	
}
