package com.example.Student_management.controller;

import com.example.Student_management.dao.UserDAO;

import com.example.Student_management.model.User;
import com.example.Student_management.util.AlertUtil;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;

    @FXML private PasswordField passwordField;

    @FXML private CheckBox showPasswordCheckBox;
    
    @FXML private TextField visiblePasswordField;
    
    @FXML private RadioButton teacherRadio;
    @FXML private RadioButton studentRadio;
    
    @FXML private Label forgotLabel;
    
    private ToggleGroup roleToggleGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	
    	roleToggleGroup = new ToggleGroup();
        studentRadio.setToggleGroup(roleToggleGroup);
        teacherRadio.setToggleGroup(roleToggleGroup);
    	
    	// Đồng bộ dữ liệu giữa hai trường PasswordField và VisiblePasswordField
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Lắng nghe sự kiện khi checkbox thay đổi
        showPasswordCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
            } else {
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
            }
        });
    }

    @FXML
    private void handleForgotClick(MouseEvent event) {
        
    	try {
    		
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgetPass.fxml"));
            Parent root = loader.load();
            
            // Lấy stage hiện tại
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @FXML
    private void handleLogin() {
    	
    	// Lấy thông tin đăng nhập
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        RadioButton selectedRadio = (RadioButton) roleToggleGroup.getSelectedToggle();
        
        String selectedRole = (selectedRadio == null) ? "Admin" : selectedRadio.getText();
        
        // Kiểm tra tài khoản bằng cách gọi UserDAO (Check thông qua DB)
        User user = UserDAO.login(username, password, selectedRole);
        
        if (user != null) {
        	
        	// Lấy thông tin người dùng hiện tại login
        	UserDAO.setCurrentUser(user);
        	
        	// Lấy role sau khi đã lấy được thông tin user
            String role = user.getRole().toString();

            // Thông báo đăng nhập thành công nếu tài khoản hợp lệ
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Đăng nhập thành công !!!");
            alert.setHeaderText(null);
            alert.setContentText("Xin chào, " + user.getUserName() + " (" + role + ")");
            alert.showAndWait().ifPresent(response -> {
                
            	try {
                    String fxmlFile = "";
                    
                    // Sau khi lấy được role,dựa vào role đó chọn ra file role tương ứng  
                    switch (role) {
                        case "Admin":
                            fxmlFile = "/Admin.fxml";
                            break;
                        case "Teacher":
                            fxmlFile = "/Teacher.fxml";
                            break;
                        case "Student":
                            fxmlFile = "/Student.fxml";
                            break;
                        default:
                        	AlertUtil.showAlert(Alert.AlertType.ERROR, "Invalid Role", "Role not recognized as: " + role);
                            return;
                    }
                    
                    // Dựa vào file role đã chọn ở trên để chuyển sang giao diện tương ứng thông qua FXMLLoader
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                    Parent root = loader.load();
                    
                    // Lấy stage hiện tại
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    
                    // Đặt title theo role
                    switch (role) {
                        case "Admin":
                            stage.setTitle("Management System - Admin");
                            break;
                        case "Teacher":
                            stage.setTitle("Management System - Teacher");
                            break;
                        case "Student":
                            stage.setTitle("Management System - Student");
                            break;
                    }
                    
                    // Cập nhật lại giao diện (Thay scene mới)
                    stage.setScene(new Scene(root));
                    stage.centerOnScreen();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Account or Password !");
        }
    }
    

}
