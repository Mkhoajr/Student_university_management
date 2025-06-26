package com.example.Student_management.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
	
	// Hàm để băm mật khẩu
	 public static String hashPassword(String password) {
	        try {
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] hashedBytes = digest.digest(password.getBytes());

	            StringBuilder sb = new StringBuilder();
	            for (byte b : hashedBytes) {
	                sb.append(String.format("%02x", b));
	            }
	            return sb.toString();
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException("Không thể hash password !!!", e);
	        }
	    }
	 
	 // Kiểm tra mật khẩu nhập vào so với mật khẩu đã hash
	  public static boolean checkPassword(String plainPassword, String hashedPassword) {
	        // Hash mật khẩu người dùng nhập vào và so sánh với mật khẩu đã hash trong cơ sở dữ liệu
	        String hashedInputPassword = hashPassword(plainPassword);
	        return hashedInputPassword.equals(hashedPassword);
	    }
	    
}
