package com.example.Student_management.socket;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class AdminClient {
	
    private Socket socket;
    private PrintWriter out;

    public AdminClient() {
        try {
            socket = new Socket("localhost", 8900);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            e.printStackTrace();
        }	
    }

    public void sendNotification(String role, String title, String content) {
        if (out != null) {	
            out.println("SEND:" + role + ":" + title + ":" + content);
        } else {
            System.err.println("Không gửi được vì kết nối không thành công.");
        }
    }
}

