package com.example.Student_management.controller;

import java.io.IOException;

import java.util.Optional;

import com.example.Student_management.service.ForgotPasswordService;
import com.example.Student_management.util.AlertUtil;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ForgetPassController {

	 @FXML private Button sendOTP;
	 
	 @FXML private TextField recipientEmailField;
	 @FXML private TextField typedOTPField;
	 @FXML private TextField newPassField;
	 @FXML private TextField confirmPassField;
	 
	 @FXML private Pane forgotPassPane;
	 @FXML private Pane resetPassPane;
	 
	 @FXML private Label countdownLabel;
	 
	 private ForgotPasswordService forgotPasswordService = new ForgotPasswordService();
	 private Timeline countdown;
	 private String currentEmail;
	 
	 @FXML
	 void sendOTPBtn(ActionEvent event) {
		 
	        String email = recipientEmailField.getText().trim().toLowerCase();
	        if (email == null || email.isEmpty()) {
	            AlertUtil.showAlert(Alert.AlertType.ERROR, "Email required", "Please enter your Email !");
	            return;
	        }

	        boolean sent = forgotPasswordService.sendOtp(email);
	        if (sent) {
	        	
	        	currentEmail = email.trim().toLowerCase();
	            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "OTP Sent", "OTP has been sent to your email.");
	            forgotPassPane.setVisible(false);
	            resetPassPane.setVisible(true);
	            
	            // Funct startCountdown() makes the label have an animation to count down the OTP expiration.
	            startCountdown();  
	        } else {
	            AlertUtil.showAlert(Alert.AlertType.ERROR, "Not Found", "No account associated with this email.");
	        }
	    }
	 
	 @FXML
	 void verifyOTPBtn(ActionEvent event) {
		 
	        String otp = typedOTPField.getText().trim();

	        if (forgotPasswordService.verifyOtp(currentEmail, otp)) {
	            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Verified", "OTP verified. You can now set a new password.");
	            newPassField.setDisable(false);
	            confirmPassField.setDisable(false);
	        } else {
	            AlertUtil.showAlert(Alert.AlertType.ERROR, "Invalid", "Invalid or expired OTP.");
	        }
	    }

	 @FXML
	 void saveNewPasswordBtn(ActionEvent event) {
		 
	        String email = recipientEmailField.getText();
	        String otp = typedOTPField.getText();
	        String newPass = newPassField.getText();
	        String confirmPass = confirmPassField.getText();

	        if (!newPass.equals(confirmPass)) {
	            AlertUtil.showAlert(Alert.AlertType.ERROR, "Mismatch", "Passwords do not match.");
	            return;
	        }

	        boolean success = forgotPasswordService.resetPassword(email, otp, newPass);
	        
	        if (success) {
	            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Success", "Password reset successful!");
	            try {
	            	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
	                Scene scene = new Scene(loader.load());
	                stage.setScene(scene);
	                stage.centerOnScreen();
				} catch (Exception e) {
					e.printStackTrace();
				}
	        } else {
	            AlertUtil.showAlert(Alert.AlertType.ERROR, "Failed", "OTP verification failed or email invalid.");
	        }
	    }
	 
	 @FXML
	 public void handleLogout(MouseEvent event) {
	 		
	        Optional<ButtonType> result = AlertUtil.showConfirmation(
	            "Logout Confirm", "Do you want to back to Login ?");
	
	        if (result.isPresent() && result.get() == ButtonType.OK) {
	            try {
	                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
	                Scene scene = new Scene(loader.load());
	                stage.setScene(scene);
	                stage.centerOnScreen();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	 
     // Funct startCountdown() makes the label have an animation to count down the OTP expiration.
	 private void startCountdown() {
		 
	        int[] timeLeft = {60};

	        countdownLabel.setText("OTP expires in: " + timeLeft[0] + "s");

	        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
	            timeLeft[0]--;
	            if (timeLeft[0] > 0) {
	                countdownLabel.setText("OTP expires in: " + timeLeft[0] + "s");
	            } else {
	                countdownLabel.setText("OTP expired!");
	                countdown.stop();
	            }
	        }));
	        countdown.setCycleCount(60);
	        countdown.play();
	    }
}





