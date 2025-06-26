package com.example.Student_management.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.Student_management.dao.CourseDAO;
import com.example.Student_management.dao.SubjectDAO;
import com.example.Student_management.dao.TeacherDAO;
import com.example.Student_management.model.Course;
import com.example.Student_management.model.Subject;
import com.example.Student_management.model.Teacher;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.util.HibernateUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

// Lớp học phần
public class FormAddCourseController {	

	private Course currentCourse;
	
	@FXML
	private TextField courseIDField;
	
	@FXML
	private ComboBox<Subject> courseSubjectNameComboBox;
	
	@FXML
	private ComboBox<Teacher> courseTeacherIDComboBox;
	
	@FXML
	private ComboBox<String> courseSemesterComboBox;
	
	@FXML
	private ComboBox<String> courseScheduleComboBox;
	
	@FXML
	private TextField courseRoomField;
	
	@FXML
	private TextField courseMaxStudentField;
	
	@FXML
	private Button courseAddButton;
	
	// Dùng List để lưu trữ lịch học đã chọn cho các course
	private List<String> selectedSchedules;

	
	@FXML
	public void initialize() {
		// Làm mới danh sách selectedSchedules và lấy lịch đã sử dụng từ DB
		selectedSchedules = getUsedSchedules();

		// Subject Name
		List<Subject> subjects = SubjectDAO.getAllSubjects();
		courseSubjectNameComboBox.setItems(FXCollections.observableArrayList(subjects));
		
		// Teacher ID
		List<Teacher> teachers = TeacherDAO.getAllTeachers();
		courseTeacherIDComboBox.setItems(FXCollections.observableArrayList(teachers));
		
		// Hiển thị "teacherId - teacherName" trong ComboBox
		courseTeacherIDComboBox.setCellFactory(cb -> new javafx.scene.control.ListCell<Teacher>() {
			@Override
			protected void updateItem(Teacher item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty || item == null ? null : item.getTeacherId() + " - " + item.getTeacherName());
			}
		});
		courseTeacherIDComboBox.setButtonCell(new javafx.scene.control.ListCell<Teacher>() {
			@Override
			protected void updateItem(Teacher item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty || item == null ? null : item.getTeacherId() + " - " + item.getTeacherName());
			}
		});
		
		// Semester
		courseSemesterComboBox.setItems(FXCollections.observableArrayList("Học Kỳ 1", "Học Kỳ 2"));
		
		// Cập nhật ComboBox lịch học
		updateScheduleChoices();
	}
	
		// Load từ DB lên Form thông tin khi bấm đúp trong hàng
		public void setCourseData(Course course) {
	        this.currentCourse = course;
			courseIDField.setEditable(false);

	        // Hiển thị dữ liệu lên các trường
	        courseIDField.setText(course.getCourseId());
	        courseSubjectNameComboBox.setValue(course.getSubject());
	        courseTeacherIDComboBox.setValue(course.getTeacher());
	        courseSemesterComboBox.setValue(course.getSemester());
	        courseScheduleComboBox.setValue(course.getSchedule());
	        courseRoomField.setText(course.getRoom());
	        courseMaxStudentField.setText(Integer.toString(course.getMaxStudents()));
	    }
	
	// Add
	@FXML
	private void handleAddCourse() {
		// Lấy Input
		String courseId = courseIDField.getText();  
		String courseRoom = courseRoomField.getText(); 
		String courseMaxStudentText = courseMaxStudentField.getText(); 
		String selectedSchedule = courseScheduleComboBox.getValue();
		Teacher selectedTeacher = courseTeacherIDComboBox.getValue(); 

		// Kiểm tra các trường bắt buộc
		if (courseId.isEmpty() || courseRoom.isEmpty() || courseMaxStudentText.isEmpty() || 
			courseSubjectNameComboBox.getValue() == null || courseTeacherIDComboBox.getValue() == null || 
			courseSemesterComboBox.getValue() == null || selectedSchedule == null) {
			AlertUtil.showAlert(AlertType.WARNING, "Thiếu Thông Tin", "Vui Lòng Điền Đầy Đủ Thông Tin !!!");
			return;
		}
		
		// Kiểm tra Khóa chính
		Course existingCourse = CourseDAO.getCourseById(courseId);
		if (existingCourse != null) {
			AlertUtil.showAlert(AlertType.WARNING, "Lỗi", "Course với ID " + courseId + " đã tồn tại !!!");
			return;
		}
		
		// Kiểm tra định dạng số lượng sinh viên
		int courseMaxStudent;
		try {
			courseMaxStudent = Integer.parseInt(courseMaxStudentText);
		} catch (NumberFormatException e) {
			AlertUtil.showAlert(AlertType.ERROR, "Lỗi định dạng", "Số lượng sinh viên phải là số nguyên.");
			return;
		}
		
		// Tạo Course mới
		Course newCourse = new Course();
		newCourse.setCourseId(courseId);
		newCourse.setSubject(courseSubjectNameComboBox.getValue());
		newCourse.setTeacher(selectedTeacher); // khóa ngoại - Teacher object
		newCourse.setSemester(courseSemesterComboBox.getValue());
		newCourse.setSchedule(selectedSchedule);
		newCourse.setRoom(courseRoom);
		newCourse.setMaxStudents(courseMaxStudent);
		
		// Lưu vào cơ sở dữ liệu
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();
			session.persist(newCourse); 
			transaction.commit(); 
			
			// Thêm lịch học vào danh sách chỉ khi lưu thành công
			selectedSchedules.add(selectedSchedule);
			updateScheduleChoices();
			
			AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Thêm Lớp học phần thành công !");
			
			// Đóng form
			courseAddButton.getScene().getWindow().hide();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm lớp học phần !");
		} finally {
			session.close();
		}   
	}
	
	
	// Update
	@FXML
	private void handleUpdateButton() {
		
	    Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận cập nhật", "Bạn có chắc chắn muốn cập nhật thông tin lớp học phần này không?");
	    if (result.isEmpty() || result.get() != ButtonType.OK) {
	        return;
	    }

	    if (currentCourse == null) {
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy môn học để cập nhật.");
	        return;
	    }

	    Session session = HibernateUtil.getSessionFactory().openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();

	        // Load lại đối tượng từ database
	        Course course = session.get(Course.class,currentCourse.getCourseId());

	        // Kiểm tra xem đối tượng có được tải thành công không
	        if (course == null) {
	            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không tìm thấy lớp học phần trong cơ sở dữ liệu.");
	            return;
	        }

	        // Lấy dữ liệu từ UI
	        String courseId = courseIDField.getText();  
			String courseRoom = courseRoomField.getText(); 
			String courseMaxStudentText = courseMaxStudentField.getText(); 
			String selectedSchedule = courseScheduleComboBox.getValue();
			Teacher selectedTeacher = courseTeacherIDComboBox.getValue();

			// Kiểm tra đầu vào
			if (courseId.isEmpty() || courseRoom.isEmpty() || courseMaxStudentText.isEmpty() || 
				courseSubjectNameComboBox.getValue() == null || courseTeacherIDComboBox.getValue() == null || 
				courseSemesterComboBox.getValue() == null || selectedSchedule == null) {
				AlertUtil.showAlert(AlertType.WARNING, "Thiếu Thông Tin", "Vui Lòng Điền Đầy Đủ Thông Tin !!!");
				return;
			}
	        
			
	        if (!courseId.equals(currentCourse.getCourseId())) {
	            AlertUtil.showAlert(AlertType.WARNING, "Cảnh báo", "Không thể thay đổi khóa chính (Course ID)!");
	            return;
	        }
	        
	        if (!courseSubjectNameComboBox.getValue().equals(currentCourse.getSubject())) {
	            AlertUtil.showAlert(AlertType.WARNING, "Cảnh báo", "Không thể thay đổi môn học!");
	            return;
	        }
	        
	        // Cập nhật lại thông tin 
	        course.setCourseId(courseId);
	        course.setSubject(courseSubjectNameComboBox.getValue());
	        course.setTeacher(selectedTeacher); // khóa ngoại - Teacher object
	        course.setSemester(courseSemesterComboBox.getValue());
	        course.setSchedule(selectedSchedule);
	        course.setRoom(courseRoom);
	        course.setMaxStudents(Integer.parseInt(courseMaxStudentText));


	        // Đồng bộ hóa các thay đổi với database
	        session.merge(course);
	        tx.commit();

	        AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Cập nhật thông tin lớp học phần thành công.");

	        // Đóng form
	        courseAddButton.getScene().getWindow().hide();

	    } catch (Exception e) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        System.err.println("Lỗi khi cập nhật lớp học phần !");
	        System.err.println("Exception class: " + e.getClass().getName());
	        e.printStackTrace();
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Cập nhật thất bại! ");
	    } finally {
	        session.close();
	    }
	}
	
	
	// Cập nhật lại ComboBox bằng cách loại bỏ những lịch đã được chọn
	private void updateScheduleChoices() {
		// Danh sách lịch học ban đầu
		List<String> availableSchedules = new ArrayList<>(List.of(
			"Thứ 2 (Tiết 1-3)",
			"Thứ 2 (Tiết 4-6)",
			"Thứ 3 (Tiết 1-3)",
			"Thứ 3 (Tiết 4-6)",
			"Thứ 4 (Tiết 1-3)",
			"Thứ 4 (Tiết 4-6)",
			"Thứ 5 (Tiết 1-3)",
			"Thứ 5 (Tiết 4-6)",
			"Thứ 6 (Tiết 1-3)",
			"Thứ 6 (Tiết 4-6)"
		));
		
		// Loại bỏ các lịch đã được chọn
		availableSchedules.removeAll(selectedSchedules);
		
		// Cập nhật ComboBox
		courseScheduleComboBox.setItems(FXCollections.observableArrayList(availableSchedules));
		
		// Xóa lựa chọn hiện tại
		courseScheduleComboBox.getSelectionModel().clearSelection();
		courseScheduleComboBox.setValue(null);
	}   
	
	// Lấy danh sách lịch học đã được sử dụng từ cơ sở dữ liệu
	private List<String> getUsedSchedules() {
		List<String> usedSchedules = new ArrayList<>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			List<Course> courses = session.createQuery("FROM Course", Course.class).getResultList();
			usedSchedules = courses.stream()
				.map(Course::getSchedule)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		} finally {
			session.close();
		}
		return usedSchedules;
	}
}