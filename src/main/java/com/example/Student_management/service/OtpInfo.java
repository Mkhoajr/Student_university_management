package com.example.Student_management.service;

public class OtpInfo {

	private String otp;
    private long timestamp;

    public OtpInfo(String otp, long timestamp) {
        this.otp = otp;
        this.timestamp = timestamp;
    }

    public String getOtp() {
        return otp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
