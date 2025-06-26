package com.example.Student_management.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.Student_management.model.Course;
import com.example.Student_management.model.Grade;
import com.example.Student_management.model.Student;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.util.HibernateUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormStudentGradingController {

	private TeacherController teacherController;

    @FXML private ComboBox<Student> studentComboBox;
    @FXML private ComboBox<Course> courseComboBox; // bạn chưa đặt fx:id, nên thêm vào FXML

    @FXML private TextField assignmentTextField;
    @FXML private TextField midtermTextField;
    @FXML private TextField finalTextField;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private Grade currentGrade;

    
    @FXML
    public void initialize() {
    	
    	studentComboBox.setVisible(true);
    	studentComboBox.setMouseTransparent(true);  // không nhận sự kiện chuột
    	studentComboBox.setFocusTraversable(false); // không nhận focus bàn phím

    	courseComboBox.setVisible(true);
    	courseComboBox.setMouseTransparent(true);
    	courseComboBox.setFocusTraversable(false);

    }
    
	public void setGrade(Grade grade) {
	    this.currentGrade = grade;

	    // Gán dữ liệu sinh viên
        Student student = grade.getStudent();
        studentComboBox.setValue(student); // ComboBox sẽ hiển thị sinh viên đó

        // Gán dữ liệu khóa học
        Course course = grade.getCourse();
        courseComboBox.setValue(course);
        
        // Gán điểm hiện tại vào các TextField
        if (grade.getAssignmentScore() != null)
            assignmentTextField.setText(grade.getAssignmentScore().toString());
        if (grade.getMidtermScore() != null)
            midtermTextField.setText(grade.getMidtermScore().toString());
        if (grade.getFinalScore() != null)
            finalTextField.setText(grade.getFinalScore().toString());
	}	
	
	// Cho phép FormStudentGradingController giao tiếp với TeacherController
	public void setTeacherController(TeacherController controller) {
	    this.teacherController = controller;
	}
	
	@FXML
	private void handleSaveButton() {
        try {
            BigDecimal assignment = new BigDecimal(assignmentTextField.getText());
            BigDecimal midterm = new BigDecimal(midtermTextField.getText());
            BigDecimal finalExam = new BigDecimal(finalTextField.getText());

            currentGrade.setAssignmentScore(assignment);
            currentGrade.setMidtermScore(midterm);
            currentGrade.setFinalScore(finalExam);

            Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận cập nhật điểm học phần", "Bạn có chắc muốn cập nhật điểm không ?");
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
            
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                session.update(currentGrade);  // update hoặc saveOrUpdate đều được
                tx.commit();
            }

            // Cập nhật ngay TableView khi đóng form
            if (teacherController != null) {
                teacherController.refreshStudentGradeTable(); 
            }
            
            AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Điểm đã được lưu thành công.");
            
            closeWindow();
        } catch (NumberFormatException e) {
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Vui lòng nhập đúng định dạng số.");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể lưu điểm.");
        }
    }
	
	@FXML
	 private void handleCancelButton() {
	        closeWindow();
	    }

	 private void closeWindow() {
	        Stage stage = (Stage) saveButton.getScene().getWindow();
	        stage.close();
	    }
	 
	 
}
