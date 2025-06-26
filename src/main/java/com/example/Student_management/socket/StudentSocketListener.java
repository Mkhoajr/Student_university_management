package com.example.Student_management.socket;

import com.example.Student_management.controller.FormStudentNotificationController;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class StudentSocketListener extends Thread {
    private FormStudentNotificationController controller;
    
    public StudentSocketListener(FormStudentNotificationController controller) {
        this.controller = controller;
    }
    
    @Override
    public void run() {
    	
        try (Socket socket = new Socket("localhost", 8900);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            
            System.out.println("StudentSocketListener đã kết nối đến server");
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Student nhận được: " + line);
                if (line.startsWith("SEND")) {
                    controller.refreshInboxFromSocket();
                }
            }
            System.out.println("StudentSocketListener kết thúc");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("StudentSocketListener lỗi: " + e.getMessage());
        }
    }
}