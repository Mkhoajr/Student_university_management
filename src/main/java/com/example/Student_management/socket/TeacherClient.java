package com.example.Student_management.socket;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TeacherClient {
    private Socket socket;
    private PrintWriter out;

    public TeacherClient() {
        try {
            socket = new Socket("localhost", 8900); // Cùng port với admin server đang lắng nghe
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(String receiverRole, String title, String content) {
    	if (out != null) {
            out.println("SEND:" + receiverRole + ":" + title + ":" + content);
            System.out.println("Đã gửi đến admin: " + title);
        }
    	else {
    		System.err.println("Socket chưa kết nối!.");
    	}
    }
}
