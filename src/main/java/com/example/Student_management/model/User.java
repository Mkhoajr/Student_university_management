package com.example.Student_management.model;

import jakarta.persistence.*;
@Entity
@Table(name = "users")

public class User {

	@Id
	@Column(name = "user_id", length = 11)
	private String userId;
	
	@ManyToOne(fetch = FetchType.EAGER) // hoặc LAZY nếu muốn tải sau
	@JoinColumn(name = "role_id") // Khóa ngoại
	private Role role;
	
	@Column(name = "username")
	private String userName;
	
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "password")
	private String password;
	
	@Column(name = "email", unique = true)
	private String email;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
}
