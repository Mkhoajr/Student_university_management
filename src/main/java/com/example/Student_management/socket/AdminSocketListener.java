package com.example.Student_management.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import com.example.Student_management.controller.FormAdminNotificationController;

// Class Thread riêng
public class AdminSocketListener extends Thread {
	
    private FormAdminNotificationController controller;

    public AdminSocketListener(FormAdminNotificationController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
    	
        try (Socket socket = new Socket("localhost", 8900);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("SEND")) {
                    controller.refreshInboxFromSocket(); // Gọi hàm load lại table
                }
            }	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

