package com.example.Student_management.controller;

import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.Student_management.dao.SubjectDAO;
import com.example.Student_management.model.Subject;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.util.HibernateUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class FormAddSubjectController {

	private Subject currentSubject;
	
	@FXML private TextField subjectIDField;
	
	@FXML private TextField subjectNameField;
	
	@FXML
	private ComboBox<Integer> subjectCreditComboBox;
	
	@FXML
	private Button btnAddForClose;
	
	
	@FXML
	public void initialize() {
		// Credit
		subjectCreditComboBox.setItems(FXCollections.observableArrayList(1,2,3));
    }
	
	// Load từ DB lên Form thông tin khi bấm đúp trong hàng
	public void setSubjectData(Subject subject) {
        this.currentSubject = subject;
        subjectIDField.setEditable(false);
        // Hiển thị dữ liệu lên các trường
        subjectIDField.setText(subject.getSubjectId());
        subjectNameField.setText(subject.getSubjectName());
        subjectCreditComboBox.setValue(subject.getCredits());
    }
	
	
	@FXML
	// Button Add trong form Add Student
	private void handleAddSubject() {
		
		// Lấy Input
	    String subjectId = subjectIDField.getText();  
	    String subjectName = subjectNameField.getText(); 
	    //Integer subjectCredit = subjectCreditComboBox.getValue();  
	    
	    // Kiểm tra các trường bắt buộc
	    if (subjectId.isEmpty() || subjectName.isEmpty() || subjectCreditComboBox.getValue() == null) {
	        AlertUtil.showAlert(AlertType.WARNING,"Thiếu Thông Tin", "Vui Lòng Điền Đầy Đủ Thông Tin !!!");
	        return;
	    }
	    
	    // Kiểm tra Khóa chính có bị trùng hay không !!!
	    Subject existingSubject = SubjectDAO.getSubjectById(subjectId);
	    if (existingSubject != null) {
            AlertUtil.showAlert(AlertType.WARNING, "Lỗi", "Subject với ID " + subjectId + " đã tồn tại !!!");
	        return; // Kết thúc việc thêm 
	    }
	    
	    // Tạo subject mới và gán các trường giá trị đã get ở trên 
 	    Subject newSubject = new Subject();
 	   newSubject.setSubjectId(subjectId);
	   newSubject.setSubjectName(subjectName);
	   newSubject.setCredits(subjectCreditComboBox.getValue());
 
	    // Mở Session chuẩn bị làm việc với DB
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        
        try {
        	// Mở Transaction để bắt đầu thao tác với việc add dữ liệu 
            transaction = session.beginTransaction();
            session.persist(newSubject); 
            
            // Commit giúp ghi thông tin xuống DB khi đã lưu bởi hàm persist
            transaction.commit(); 
            AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Thêm môn học thành công !");
            
            // Đóng form
            btnAddForClose.getScene().getWindow().hide();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm môn học ! ");
        } finally {
            session.close();
        }   
	}

	@FXML
	public void handleUpdateButton() {
		
	    Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận cập nhật", "Bạn có chắc chắn muốn cập nhật thông tin môn học này không?");
	    if (result.isEmpty() || result.get() != ButtonType.OK) {
	        return;
	    }

	    if (currentSubject == null) {
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy môn học để cập nhật.");
	        return;
	    }

	    Session session = HibernateUtil.getSessionFactory().openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();

	        // Load lại đối tượng từ database
	        Subject subject = session.get(Subject.class,currentSubject.getSubjectId());

	        // Kiểm tra xem đối tượng có được tải thành công không
	        if (subject == null) {
	            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy môn học trong cơ sở dữ liệu.");
	            return;
	        }

	        // Lấy dữ liệu từ UI
	        String subjectId = subjectIDField.getText();
	        String subjectName = subjectNameField.getText(); 
	        Integer Credit = subjectCreditComboBox.getValue();

	        // Kiểm tra dữ liệu đầu vào
	        if (subjectId == null || subjectName.trim().isEmpty() || Credit == null) {
	            AlertUtil.showAlert(AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin!");
	            return;
	        }
	        
	        // Kiểm tra xem userId hoặc studentId có bị thay đổi hay không
	        if (!subjectId.equals(currentSubject.getSubjectId())) {
	            AlertUtil.showAlert(AlertType.WARNING, "Cảnh báo", "Không thể thay đổi khóa chính (Subject ID)!");
	            return;
	        }
	        
	        // Cập nhật lại thông tin 
	        subject.setSubjectId(subjectId);
	        subject.setSubjectName(subjectName);
	        subject.setCredits(Credit);


	        // Đồng bộ hóa các thay đổi với database
	        session.merge(subject);
	        tx.commit();

	        AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật thông tin môn học thành công.");

	        // Đóng form
	        btnAddForClose.getScene().getWindow().hide();

	    } catch (Exception e) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        System.err.println("Lỗi khi cập nhật môn học !");
	        System.err.println("Exception class: " + e.getClass().getName());
	        e.printStackTrace();
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Cập nhật thất bại! ");
	    } finally {
	        session.close();
	    }
	}
	
}
