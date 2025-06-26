package com.example.Student_management.controller;

import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.Session;

import com.example.Student_management.model.Course;
import com.example.Student_management.model.Grade;
import com.example.Student_management.model.Student;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.util.HibernateUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

public class FormRegisteredCoursesController {

    @FXML private TableView<Course> registeredCoursesTableView;

 	@FXML private TableColumn<Course, String> regCourseIDColumn;
    @FXML private TableColumn<Course, String> regCourseSubjectNameColumn;
    @FXML private TableColumn<Course, String> regCourseTeacherColumn;
    @FXML private TableColumn<Course, String> regCourseSemesterColumn;
    @FXML private TableColumn<Course, String> regCourseScheduleColumn;
    @FXML private TableColumn<Course, String> regCourseRoomColumn;
    
    @FXML	
	private Button dropCourseButton;
   // @FXML private TableColumn<Course, Integer> courseAvailableSeatsColumn; // = MaxStudent
    
	private Student currentStudent; // biến để lưu student được truyền vào
	
	private TeacherController teacherController;
	
	private CourseDropListener dropListener;
	
    private ObservableList<Course> courses = FXCollections.observableArrayList();

    public interface CourseDropListener {
        void onCourseDropped();
    }

    public void setCourseDropListener(CourseDropListener listener) {
        this.dropListener = listener;
    }

    @FXML
    public void initialize() {
    	
    	setupTableColumns();
        loadMyCourses();   
    }
    
    private void setupTableColumns() {
        // Columns for My Courses
    	regCourseIDColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
    	regCourseSubjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
    	regCourseTeacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
    	regCourseSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
    	regCourseScheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
    	regCourseRoomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
    }
    
	private void loadMyCourses() {
        if (currentStudent == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.setHibernateFlushMode(FlushMode.ALWAYS);

            // Lấy lại thông tin student có danh sách khóa học
            Student student = session.get(Student.class, currentStudent.getStudentId());

            // Lấy danh sách các khóa học ĐÃ đăng ký
            List<Course> registered = student.getRegisteredCourses();
            
            courses.setAll(registered); // cập nhật lại danh sách
            registeredCoursesTableView.setItems(courses);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể tải danh sách lớp học phần.");
        }
    }
	
	@FXML
	private void handleDropCourse() {
	    Course selectedCourse = registeredCoursesTableView.getSelectionModel().getSelectedItem();

	    if (selectedCourse == null) {
	        AlertUtil.showAlert(AlertType.WARNING, "Cảnh báo", "Vui lòng chọn lớp học phần để huỷ.");
	        return;
	    }

	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        session.beginTransaction();

	        Student student = session.get(Student.class, currentStudent.getStudentId());

	        String hql = "SELECT g.totalScore FROM Grade g WHERE g.student = :student AND g.course = :course";
	        Object result = session.createQuery(hql)
	            .setParameter("student", student)
	            .setParameter("course", selectedCourse)
	            .uniqueResult();

	        if (result != null) {
	            AlertUtil.showAlert(AlertType.ERROR, "Không thể huỷ", "Lớp học phần đã được chấm điểm, không thể huỷ.");
	            return;
	        }

	        // Xoá Grade nếu tồn tại
	        String hqlDelete = "FROM Grade g WHERE g.student = :student AND g.course = :course";
	        Grade grade = session.createQuery(hqlDelete, Grade.class)
	            .setParameter("student", student)
	            .setParameter("course", selectedCourse)
	            .uniqueResult();

	        if (grade != null) {
	            session.remove(grade);
	        }

	        student.getRegisteredCourses().remove(selectedCourse);
	        session.merge(student);

	        session.getTransaction().commit();

	        AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", "Đã huỷ đăng ký lớp học phần.");
	        loadMyCourses();
	        
	        if (dropListener != null) {
	            dropListener.onCourseDropped(); // Gọi StudentController cập nhật lại danh sách
	        }

	        // Kiểm tra teacherController trước khi gọi
	        if (teacherController != null) {
	            teacherController.refreshStudentGradeTable();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể huỷ lớp học phần.");
	    }
	}

	public void setStudent(Student student) {
	    this.currentStudent = student;
	    loadMyCourses();
	}

	public void setTeacherController(TeacherController teacherController) {
	    this.teacherController = teacherController;
	}
	
}
