package com.example.Student_management.socket;

import java.io.*;
import java.net.*;
import java.util.*;

// This is a Server Socket.
public class AdminSocketServer {
	
    private static final int PORT = 8900;
    private static final List<PrintWriter> clientWriters = Collections.synchronizedList(new ArrayList<>());

    public static void startServer() {
    	
        System.out.println("AdminSocketServer đang chạy tại port " + PORT);

        // ServerSocket serverSocket để lắng nghe kết nối với PORT tương ứng
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
        	// Vòng lặp chờ client kết nối
            while (true) {
            	
            	// Socket clientSocket giúp chờ, kết nối và giữ kết nối khi real-time chat
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client đã kết nối: " + clientSocket);

                // Khi có client kết nối tạo thread xử lý riêng biệt
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();	
        }
    }

    private static void handleClient(Socket socket) {
        try (
        	// Đọc dữ liệu từ client
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); // socket.getInputStream() luồng đọc dữ liệu từ client
        	// Gửi dữ liệu đến client
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)
        ) {
        	// Thêm client vào danh sách ngay khi kết nối
            clientWriters.add(out);

            String line;
            // Vòng lặp đọc tin nhắn từ client
            while ((line = in.readLine()) != null) {
                System.out.println("Nhận từ client: " + line);

                if (line.startsWith("SEND:")) {
                    broadcast(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Client mất kết nối");
        }
    }

    // Gửi tin nhắn đến tất cả client đang kết nối
    private static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}