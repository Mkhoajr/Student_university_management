package com.example.Student_management.socket;

import com.example.Student_management.controller.FormTeacherNotificationController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TeacherSocketListener extends Thread {
    private FormTeacherNotificationController controller;

    public TeacherSocketListener(FormTeacherNotificationController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
    	
        try (Socket socket = new Socket("localhost", 8900);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            System.out.println("TeacherSocketListener đã kết nối đến server");

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Teacher nhận được: " + line);
                if (line.startsWith("SEND")) {
                    controller.refreshInboxFromSocket();
                }
            }

            System.out.println("TeacherSocketListener kết thúc");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("TeacherSocketListener lỗi: " + e.getMessage());
        }
    }
}
