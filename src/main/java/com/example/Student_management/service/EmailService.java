package com.example.Student_management.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

		// Send Email Belong to OTP
		// smtp : simple mail transfer protocol
	
        final String fromEmail = "";
        final String password = "";

        public boolean sendOtpEmail(String toEmail, String otp) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            try {
            	
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(
                        Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Mã OTP đặt lại mật khẩu");
                message.setText("Mã OTP của bạn là: " + otp);

                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
