package com.example.Student_management.controller;

import java.io.File;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.Student_management.dao.DepartmentDAO;
import com.example.Student_management.dao.RoleDAO;
import com.example.Student_management.dao.TeacherDAO;
import com.example.Student_management.dao.UserDAO;
import com.example.Student_management.model.Department;
import com.example.Student_management.model.Role;
import com.example.Student_management.model.Teacher;
import com.example.Student_management.model.User;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.util.HibernateUtil;
import com.example.Student_management.util.PasswordUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class FormAddTeacherController {

	private String currentImagePath;
	
	private Teacher currentTeacher;
	
	private User currentUser;
	
	@FXML private TextField teacherUserIDField;
	
	@FXML private TextField teacherUserNameField;
	
	@FXML private TextField teacherPasswordField;
	
	@FXML private TextField teacherIDField;
	
	@FXML private TextField teacherNameField;	
	
	@FXML private DatePicker teacherBirthdayPicker;
	
	@FXML
	private ComboBox<Teacher.Gender> teacherGenderComboBox;
	
	@FXML
	private ComboBox<Department> teacherDepartmentComboBox;

	@FXML
	private ImageView teacherImageView;

	@FXML
	private Button btnImportTeacherImage;
	
	@FXML 
	private Button updateButton;
	
	private File selectedImageFile;

	@FXML
	public void initialize() {
		
		teacherGenderComboBox.setItems(FXCollections.observableArrayList(Teacher.Gender.values()));
		
		// Department: load từ database
	    List<Department> departments = DepartmentDAO.getAllDepartments();
	    teacherDepartmentComboBox.setItems(FXCollections.observableArrayList(departments));
	    
	    // Gợi ý năm 2000 khi mở DatePicker
	    teacherBirthdayPicker.setValue(LocalDate.of(2000, 1, 1));
		 
    }
	
	// Load từ DB lên Form thông tin khi bấm đúp trong hàng
	public void setTeacherData(Teacher teacher,User user) {
        this.currentTeacher = teacher;
        this.currentUser = user;

        teacherUserIDField.setEditable(false);
        // Hiển thị dữ liệu lên các trường
        teacherUserIDField.setText(user.getUserId());
        teacherUserNameField.setText(user.getUserName());
        teacherPasswordField.setText(user.getPassword());
        teacherIDField.setText(teacher.getTeacherId());
        teacherNameField.setText(teacher.getTeacherName());
        teacherBirthdayPicker.setValue(teacher.getDateOfBirth());
        teacherGenderComboBox.setValue(teacher.getGender());
        teacherDepartmentComboBox.setValue(teacher.getDepartment());
        updateTeacherImage(teacher.getImagePath());
        // Load ảnh nếu cần
    }
	
	@FXML
	// Button Add trong form Add Student
	private void handleAddTeacher() {
		
		// Lấy Input
	    String userId = teacherUserIDField.getText();  
	    String userName = teacherUserNameField.getText(); 
	    String password = teacherPasswordField.getText(); 
	    String teacherId = teacherIDField.getText();  
	    String teacherName = teacherNameField.getText();  
	    Department department = teacherDepartmentComboBox.getValue();  
	    LocalDate dob = teacherBirthdayPicker.getValue();  

	    
	    // Kiểm tra các trường bắt buộc
	    if (userId.isEmpty() || password.isEmpty() || teacherId.isEmpty() || teacherName.isEmpty() || department == null || dob == null || teacherGenderComboBox.getValue() == null) {
	        AlertUtil.showAlert(AlertType.WARNING,"Thiếu Thông Tin", "Vui Lòng Điền Đầy Đủ Thông Tin !!!");
	        return;
	    }
	    
	    // Kiểm tra Khóa chính có bị trùng hay không !!!
	    // Kiểm tra xem User đã tồn tại trong database chưa
	    User existingUser = UserDAO.getUserById(userId);
	    if (existingUser != null) {
	        // Nếu user đã tồn tại, thông báo cho người dùng
            AlertUtil.showAlert(AlertType.WARNING, "Lỗi", "User với ID " + userId + " đã tồn tại !!!");
	        return; // Kết thúc việc thêm 
	    }
	    
	    // Kiểm tra xem Student ID đã tồn tại trong DB chưa
	    Teacher existingTeacher = TeacherDAO.getTeacherById(teacherId);
	    if (existingTeacher != null) {
	        // Nếu user đã tồn tại, thông báo cho người dùng
            AlertUtil.showAlert(AlertType.WARNING, "Lỗi", "Giảng Viên với ID " + teacherId + " đã tồn tại !!!");
	        return; // Kết thúc việc thêm sinh viên
	    }
	    
	    // Tạo user mới và gán các trường giá trị đã get ở trên cho thông tin user bằng hàm set (Đã lấy thông tin user bởi hàm get ở trên).
 	    User newUser = new User();
        newUser.setUserId(userId);
        newUser.setUserName(userName);
        newUser.setPassword(password);
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        Role roleEntity = RoleDAO.getRoleByName("Teacher");
        if (roleEntity == null) {
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Role 'Teacher' không tồn tại !!!");
            return;
        }
        newUser.setRole(roleEntity);
        
	    // Tạo và lưu teacher mới
	    Teacher newTeacher = new Teacher();
	    newTeacher.setUser(newUser); 
	    newTeacher.setTeacherId(teacherId);
	    newTeacher.setTeacherName(teacherName);
	    newTeacher.setGender(teacherGenderComboBox.getValue());
	    newTeacher.setDepartment(department);
	    newTeacher.setDateOfBirth(dob);  // Set ngày sinh
	    newTeacher.setHireDate(LocalDate.now());
	    if (selectedImageFile != null) {
	    // Xử lý ảnh nếu có (lưu vào DB hoặc filesystem)
	    newTeacher.setImagePath(selectedImageFile.getAbsolutePath());
	    }

	    // Mở Session chuẩn bị làm việc với DB
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        
        try {
        	// Mở Transaction để bắt đầu thao tác với việc add dữ liệu 
            transaction = session.beginTransaction();
            session.persist(newUser); // Đánh dấu User cần lưu
            session.persist(newTeacher); // Đánh dấu Student cần lưu
            
            // Commit giúp ghi thông tin xuống DB khi đã lưu bởi hàm persist
            transaction.commit(); 
            AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Thêm giảng viên thành công !");
            
            // Đóng form
            btnImportTeacherImage.getScene().getWindow().hide();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm giảng viên ! ");
        } finally {
            session.close();
        }   
	}

	@FXML
	public void handleUpdateButton() {
		
	    Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận cập nhật", "Bạn có chắc chắn muốn cập nhật thông tin giảng viên này không?");
	    if (result.isEmpty() || result.get() != ButtonType.OK) {
	        return;
	    }

	    if (currentTeacher == null || currentUser == null) {
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy giảng viên để cập nhật.");
	        return;
	    }

	    Session session = HibernateUtil.getSessionFactory().openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();

	        // Load lại đối tượng từ database
	        Teacher teacher = session.get(Teacher.class, currentTeacher.getTeacherId());
	        User user = session.get(User.class, currentUser.getUserId());

	        // Kiểm tra xem đối tượng có được tải thành công không
	        if (teacher == null || user == null) {
	            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy giảng viên hoặc người dùng trong cơ sở dữ liệu.");
	            return;
	        }

	        // Lấy dữ liệu từ UI
	        String userId = teacherUserIDField.getText();
	        String teacherId = teacherIDField.getText();
	        String userName = teacherUserNameField.getText();
	        String password = teacherPasswordField.getText();
	        String teacherName = teacherNameField.getText();
	        LocalDate dob = teacherBirthdayPicker.getValue();
	        Teacher.Gender gender = teacherGenderComboBox.getValue();
	        Department department = teacherDepartmentComboBox.getValue();

	        // Kiểm tra dữ liệu đầu vào
	        if (userName == null || userName.trim().isEmpty() ||
	            password == null || password.trim().isEmpty() ||
	            teacherName == null || teacherName.trim().isEmpty() ||
	            dob == null || gender == null || department == null) {
	            AlertUtil.showAlert(AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin!");
	            return;
	        }
	        
	        // Kiểm tra xem userId hoặc studentId có bị thay đổi hay không
	        if (!userId.equals(currentUser.getUserId()) || !teacherId.equals(currentTeacher.getTeacherId())) {
	            AlertUtil.showAlert(AlertType.WARNING, "Cảnh báo", "Không thể thay đổi khóa chính (User ID hoặc Teacher ID)!");
	            return;
	        }
	        
	        // Cập nhật thông tin user
	        user.setUserName(userName);
	        user.setPassword(password);
	        user.setPasswordHash(PasswordUtil.hashPassword(password));

	        // Cập nhật thông tin student
	        teacher.setTeacherName(teacherName);
	        teacher.setDateOfBirth(dob);
	        teacher.setGender(gender);
	        teacher.setDepartment(department);

	        // Cập nhật ảnh nếu có
	        if (selectedImageFile != null) {
	        	teacher.setImagePath(selectedImageFile.getAbsolutePath());
	        } else if (teacher.getImagePath() != null) {
	        	teacher.setImagePath(teacher.getImagePath());
	        } else {
	        	teacher.setImagePath(null);
	        }

	        // Gắn lại liên kết
	        teacher.setUser(user);

	        // Đồng bộ hóa các thay đổi với database
	        session.merge(user);
	        session.merge(teacher);

	        tx.commit();

	        AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật thông tin sinh viên thành công.");

	        // Đóng form
	        btnImportTeacherImage.getScene().getWindow().hide();

	    } catch (Exception e) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        System.err.println("Lỗi khi cập nhật giảng viên: " + e.getMessage());
	        System.err.println("Exception class: " + e.getClass().getName());
	        e.printStackTrace();
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Cập nhật thất bại! ");
	    } finally {
	        session.close();
	    }
	}
	
	
	public void updateTeacherImage(String imagePath) {
	    try {
	        if (imagePath != null && !imagePath.isEmpty()) {
	            Image image = new Image("file:" + imagePath);
	            teacherImageView.setImage(image);
	        } else {
	            // Nếu không có ảnh, bạn có thể đặt ảnh mặc định
	        	teacherImageView.setImage(null);  // ảnh trong resource
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	@FXML
	private void handleImportButton() {
		
		// Tạo một hộp thoại chọn tệp tin
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Select Teacher Image");

	    // Chỉ cho phép chọn file ảnh có các đuôi như sau
	    fileChooser.getExtensionFilters().addAll(
	        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
	    );

	    
	    File file = fileChooser.showOpenDialog(btnImportTeacherImage.getScene().getWindow());

	    if (file != null) {
	    	
	        selectedImageFile = file;
	        currentImagePath = file.getAbsolutePath(); // Cập nhật ảnh hiện tại

	        // Tạo đối tượng Image từ file được chọn.
	        Image image = new Image(file.toURI().toString());
	        // Gán ảnh vào ImageView trong giao diện để hiển thị.
	        teacherImageView.setImage(image);
	        teacherImageView.setPreserveRatio(false);
	        teacherImageView.setFitWidth(140);
	        teacherImageView.setFitHeight(155);
	    }
	}
}
