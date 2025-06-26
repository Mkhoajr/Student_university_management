package com.example.Student_management.controller;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.Student_management.dao.DepartmentDAO;
import com.example.Student_management.dao.RoleDAO;
import com.example.Student_management.dao.StudentDAO;
//import com.example.Student_management.dao.StudentDAO;
import com.example.Student_management.dao.UserDAO;
import com.example.Student_management.model.Department;
import com.example.Student_management.model.Role;
import com.example.Student_management.model.Student;
import com.example.Student_management.model.User;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.util.HibernateUtil;
import com.example.Student_management.util.PasswordUtil;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class FormAddStudentController {

	private String currentImagePath;
	
	private Student currentStudent;
	
	private User currentUser;
	
	@FXML private TextField studentUserIDField;
	
	@FXML private TextField studentUserNameField;
	
	@FXML private TextField studentPasswordField;
	
	@FXML private TextField studentIDField;
	
	@FXML private TextField studentNameField;	
	
	@FXML private DatePicker studentBirthdayPicker;

	@FXML
	private ComboBox<Student.Gender> studentGenderComboBox;
	
	@FXML
	private ComboBox<Department> studentDepartmentComboBox;

	@FXML
	private ImageView studentImageView;

	@FXML
	private Button btnImportStudentImage;
	
	@FXML 
	private Button updateButton;
	
	private File selectedImageFile;
		

	@FXML
	public void initialize() {
		
		studentGenderComboBox.setItems(FXCollections.observableArrayList(Student.Gender.values()));
		
		// Department: load từ database
	    List<Department> departments = DepartmentDAO.getAllDepartments();
	    studentDepartmentComboBox.setItems(FXCollections.observableArrayList(departments));
	    
	    // Gợi ý năm 2006 khi mở DatePicker
	    studentBirthdayPicker.setValue(LocalDate.of(2006, 1, 1));
    }
	   

	public void setStudentData(Student student,User user) {
        this.currentStudent = student;
        this.currentUser = user;
        studentUserIDField.setEditable(false);
        
        // Hiển thị dữ liệu lên các trường
        studentUserIDField.setText(user.getUserId());
        studentUserNameField.setText(user.getUserName());
        studentPasswordField.setText(user.getPassword());
        studentIDField.setText(student.getStudentId());
        studentNameField.setText(student.getStudentName());
        studentBirthdayPicker.setValue(student.getDateOfBirth());
        studentGenderComboBox.setValue(student.getGender());
        studentDepartmentComboBox.setValue(student.getDepartment());
        updateStudentImage(student.getImagePath());
        // Load ảnh nếu cần
    }
	
	
	@FXML
	// Button Add trong form Add Student
	private void handleAddStudent() {
		
		// Lấy Input
	    String userId = studentUserIDField.getText();  // Lấy user ID
	    String userName = studentUserNameField.getText(); // Lấy username của Student để Login vào tk của SV
	    String password = studentPasswordField.getText();  // Lấy mật khẩu
	    String studentId = studentIDField.getText();  // Lấy mã sinh viên
	    String studentName = studentNameField.getText();  // Lấy tên sinh viên
	    Department department = studentDepartmentComboBox.getValue();  // Lấy phòng ban
	    LocalDate dob = studentBirthdayPicker.getValue();  // Lấy ngày sinh từ DatePicker

	    
	    // Kiểm tra các trường bắt buộc
	    if (userId.isEmpty() || password.isEmpty() || studentId.isEmpty() || studentName.isEmpty() || department == null || dob == null || studentGenderComboBox.getValue() == null) {
	        AlertUtil.showAlert(AlertType.WARNING,"Thiếu Thông Tin", "Vui Lòng Điền Đầy Đủ Thông Tin !!!");
	        return;
	    }
	    
	    // Kiểm tra Khóa chính (user_id,student_id)có bị trùng hay không !!!
	    // Kiểm tra xem User đã tồn tại trong database chưa
	    User existingUser = UserDAO.getUserById(userId);
	    if (existingUser != null) {
	        // Nếu user đã tồn tại, thông báo cho người dùng
            AlertUtil.showAlert(AlertType.WARNING, "Lỗi", "User với ID " + userId + " đã tồn tại !!!");
	        return; // Kết thúc việc thêm sinh viên
	    }
	    
	    // Kiểm tra xem Student ID đã tồn tại trong DB chưa
	    Student existingStudent = StudentDAO.getStudentById(studentId);
	    if (existingStudent != null) {
	        // Nếu user đã tồn tại, thông báo cho người dùng
            AlertUtil.showAlert(AlertType.WARNING, "Lỗi", "Sinh Viên với ID " + studentId + " đã tồn tại !!!");
	        return; // Kết thúc việc thêm sinh viên
	    }
	    
	    // Tạo user mới và gán các trường giá trị đã get ở trên cho thông tin user bằng hàm set (Đã lấy thông tin user bởi hàm get ở trên).
 	    User newUser = new User();
        newUser.setUserId(userId);
        newUser.setUserName(userName);
        newUser.setPassword(password);
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        Role roleEntity = RoleDAO.getRoleByName("Student");
        if (roleEntity == null) {
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Role 'Student' không tồn tại !!!");
            return;
        }
        newUser.setRole(roleEntity);
        
	    // Tạo và lưu student mới
	    Student newStudent = new Student();
	    newStudent.setUser(newUser);  // Liên kết student với user
	    newStudent.setStudentId(studentId);
	    newStudent.setStudentName(studentName);
	    newStudent.setGender(studentGenderComboBox.getValue());
	    newStudent.setDepartment(department);
	    newStudent.setDateOfBirth(dob);  // Set ngày sinh
	    newStudent.setEnrollmentDate(LocalDate.now());
	    if (selectedImageFile != null) {
	    // Xử lý ảnh nếu có (lưu vào DB hoặc filesystem)
	    newStudent.setImagePath(selectedImageFile.getAbsolutePath());
	    }

	    // Mở Session chuẩn bị làm việc với DB
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        
        try {
        	// Mở Transaction để bắt đầu thao tác với việc add dữ liệu 
            transaction = session.beginTransaction();
            session.persist(newUser); // Đánh dấu User cần lưu
            session.persist(newStudent); // Đánh dấu Student cần lưu
            
            // Commit giúp ghi thông tin xuống DB khi đã lưu bởi hàm persist
            transaction.commit(); 
            AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Thêm sinh viên thành công !");
            
            // Đóng form
            btnImportStudentImage.getScene().getWindow().hide();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm sinh viên ! ");
        } finally {
            session.close();
        }   
	}

	
	@FXML
	public void handleUpdateButton() {
		
	    Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận cập nhật", "Bạn có chắc chắn muốn cập nhật thông tin sinh viên này không?");
	    if (result.isEmpty() || result.get() != ButtonType.OK) {
	        return;
	    }

	    if (currentStudent == null || currentUser == null) {
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy sinh viên để cập nhật.");
	        return;
	    }

	    Session session = HibernateUtil.getSessionFactory().openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();

	        // Load lại đối tượng từ database
	        Student student = session.get(Student.class, currentStudent.getStudentId());
	        User user = session.get(User.class, currentUser.getUserId());

	        // Kiểm tra xem đối tượng có được tải thành công không
	        if (student == null || user == null) {
	            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy sinh viên hoặc người dùng trong cơ sở dữ liệu.");
	            return;
	        }

	        // Lấy dữ liệu từ UI
	        String userId = studentUserIDField.getText();
	        String studentId = studentIDField.getText();
	        String userName = studentUserNameField.getText();
	        String password = studentPasswordField.getText();
	        String studentName = studentNameField.getText();
	        LocalDate dob = studentBirthdayPicker.getValue();
	        Student.Gender gender = studentGenderComboBox.getValue();
	        Department department = studentDepartmentComboBox.getValue();

	        // Kiểm tra dữ liệu đầu vào
	        if (userName == null || userName.trim().isEmpty() ||
	            password == null || password.trim().isEmpty() ||
	            studentName == null || studentName.trim().isEmpty() ||
	            dob == null || gender == null || department == null) {
	            AlertUtil.showAlert(AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin!");
	            return;
	        }
	        
	        // Kiểm tra xem userId hoặc studentId có bị thay đổi hay không
	        if (!userId.equals(currentUser.getUserId()) || !studentId.equals(currentStudent.getStudentId())) {
	            AlertUtil.showAlert(AlertType.WARNING, "Cảnh báo", "Không thể thay đổi khóa chính (User ID hoặc Student ID)!");
	            return;
	        }
	        
	        // Cập nhật thông tin user
	        user.setUserName(userName);
	        user.setPassword(password);
	        user.setPasswordHash(PasswordUtil.hashPassword(password));

	        // Cập nhật thông tin student
	        student.setStudentName(studentName);
	        student.setDateOfBirth(dob);
	        student.setGender(gender);
	        student.setDepartment(department);

	        // Cập nhật ảnh nếu có
	        if (selectedImageFile != null) {
	            student.setImagePath(selectedImageFile.getAbsolutePath());
	        } else if (student.getImagePath() != null) {
	            student.setImagePath(student.getImagePath());
	        } else {
	            student.setImagePath(null);
	        }

	        // Gắn lại liên kết
	        student.setUser(user);

	        // Đồng bộ hóa các thay đổi với database
	        session.merge(user);
	        session.merge(student);

	        tx.commit();

	        AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật thông tin sinh viên thành công.");

	        // Đóng form
	        btnImportStudentImage.getScene().getWindow().hide();

	    } catch (Exception e) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        System.err.println("Lỗi khi cập nhật sinh viên: " + e.getMessage());
	        System.err.println("Exception class: " + e.getClass().getName());
	        e.printStackTrace();
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Cập nhật thất bại: " + e.getMessage());
	    } finally {
	        session.close();
	    }
	}
	
	
	public void updateStudentImage(String imagePath) {
	    try {
	        if (imagePath != null && !imagePath.isEmpty()) {
	            Image image = new Image("file:" + imagePath);
	            studentImageView.setImage(image);
	        } else {
	            // Nếu không có ảnh, bạn có thể đặt ảnh mặc định
	            studentImageView.setImage(null);  // ảnh trong resource
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	@FXML
	private void handleImportButton() {
		
		// Tạo một hộp thoại chọn tệp tin
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Select Student Image");

	    // Chỉ cho phép chọn file ảnh có các đuôi như sau
	    fileChooser.getExtensionFilters().addAll(
	        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
	    );
	    
	    File file = fileChooser.showOpenDialog(btnImportStudentImage.getScene().getWindow());

	    if (file != null) {
	        selectedImageFile = file;
	        currentImagePath = file.getAbsolutePath(); // Cập nhật ảnh hiện tại

	        // Tạo đối tượng Image từ file được chọn.
	        Image image = new Image(file.toURI().toString());
	        // Gán ảnh vào ImageView trong giao diện để hiển thị.
	        studentImageView.setImage(image);
	        studentImageView.setPreserveRatio(false);
	        studentImageView.setFitWidth(140);
	        studentImageView.setFitHeight(155);
	    }
	}
}


