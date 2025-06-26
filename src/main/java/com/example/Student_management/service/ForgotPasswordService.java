package com.example.Student_management.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.example.Student_management.dao.UserDAO;
import com.example.Student_management.model.User;
import com.example.Student_management.util.PasswordUtil;

public class ForgotPasswordService {
	
	// Use Map Data Structure to save temporary OTP.
    private Map<String, OtpInfo> otpMap = new HashMap<>();
    private EmailService emailService = new EmailService();

    // This funct find user's Email and generate random OTP.
    public boolean sendOtp(String email) {
    	
        User user = UserDAO.findByEmail(email);
        if (user == null) return false;

        // funct generateOtp() generates random Otp.
        String otp = generateOtp();
        otpMap.put(email, new OtpInfo(otp, System.currentTimeMillis()));

        return emailService.sendOtpEmail(email, otp);
    }

    // Verify OTP
    public boolean verifyOtp(String email, String inputOtp) {
    	
        OtpInfo info = otpMap.get(email);
        if (info == null) return false;

        long now = System.currentTimeMillis();
        if (now - info.getTimestamp() > 1 * 60 * 1000) return false; // Giới hạn 1p.

        return info.getOtp().equals(inputOtp);
    }	

    
    public boolean resetPassword(String email, String inputOtp, String newPassword) {
        if (!verifyOtp(email, inputOtp)) return false;

        User user = UserDAO.findByEmail(email);
        if (user == null) return false;

        String hashed = PasswordUtil.hashPassword(newPassword);
        
        user.setPassword(newPassword);
        user.setPasswordHash(hashed);
        UserDAO.update(user);
        otpMap.remove(email);
        return true;
    }

    // Sinh OTP tự động
    private String generateOtp() {
    	
        Random rand = new Random();
        return String.valueOf(100000 + rand.nextInt(900000));
    }
}
