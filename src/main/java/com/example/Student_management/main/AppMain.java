package com.example.Student_management.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.example.Student_management.socket.AdminSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class AppMain extends Application {

    private static final int ADMIN_SOCKET_PORT = 8900;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Start AdminSocketServer once if port is not in use
            if (!isPortInUse(ADMIN_SOCKET_PORT)) {
                new Thread(() -> AdminSocketServer.startServer()).start();
            }

            // Load Login UI
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            primaryStage.setTitle("Chương Trình Quản Lý Sinh Viên");
            primaryStage.setScene(new Scene(root, 800, 500));
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPortInUse(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}