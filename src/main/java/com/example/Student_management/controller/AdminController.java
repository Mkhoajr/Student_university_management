package com.example.Student_management.controller;

import java.io.IOException;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.example.Student_management.model.Course;
import com.example.Student_management.model.Student;
import com.example.Student_management.model.Subject;
import com.example.Student_management.model.Teacher;
import com.example.Student_management.model.User;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.util.HibernateUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class AdminController {
	
	private ObservableList<Student> allStudents = FXCollections.observableArrayList();
	private ObservableList<Teacher> allTeachers = FXCollections.observableArrayList();
	private ObservableList<Course> allCourses = FXCollections.observableArrayList();
	private ObservableList<Subject> allSubjects = FXCollections.observableArrayList();

	
	// @FXML cho Dashboard 
	@FXML private BarChart<String, Number> studentPerDepartmentChart;
	@FXML private PieChart userRolePieChart;

	@FXML private AnchorPane cardStatistic;
	@FXML private AnchorPane barChart;
	@FXML private AnchorPane pieChart;
	
	// TableColumn Student
	@FXML private TableColumn<Student, String> studentIDColumn;
	@FXML private TableColumn<Student, String> studentNameColumn;
	@FXML private TableColumn<Student, String> studentDOBColumn;
	@FXML private TableColumn<Student, String> studentGenderColumn;
	@FXML private TableColumn<Student, String> studentDepartmentColumn;
	@FXML private TableColumn<Student, String> studentEnrollColumn;
	
	// TableColumn Teacher
	@FXML private TableColumn<Teacher, String> teacherIDColumn;
	@FXML private TableColumn<Teacher, String> teacherNameColumn;
	@FXML private TableColumn<Teacher, String> teacherDOBColumn;
	@FXML private TableColumn<Teacher, String> teacherGenderColumn;
	@FXML private TableColumn<Teacher, String> teacherDepartmentColumn;
	@FXML private TableColumn<Teacher, String> teacherHireColumn;
	
	// TableColumn Course
	@FXML private TableColumn<Course, String> courseIDColumn;
	@FXML private TableColumn<Course, String> courseSubjectNameColumn;
	@FXML private TableColumn<Course, String> courseTeacherNameColumn;
	@FXML private TableColumn<Course, String> courseSemesterColumn;
	@FXML private TableColumn<Course, String> courseScheduleColumn;
	@FXML private TableColumn<Course, String> courseRoomColumn;
	@FXML private TableColumn<Course, String> courseMaxStudentColumn;
	
	
	// TableColumn Subject
	@FXML private TableColumn<Subject, String> subjectIDColumn;
	@FXML private TableColumn<Subject, String> subjectNameColumn;
	@FXML private TableColumn<Subject, Integer> subjectCreditColumn;
	
	
	// TableView cho các lớp model
    @FXML private TableView<Student> studentTableView;
    @FXML private TableView<Teacher> teacherTableView;
    @FXML private TableView<Course>  courseTableView;
    @FXML private TableView<Subject> subjectTableView;
    
    // Component
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button logoutButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;

    // Label
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalTeachersLabel;
    @FXML private Label totalDepartmentsLabel;
    
    @FXML
	public void initialize() {
		
    	// Dashboard Anchorpane
    	cardStatistic.setVisible(true);
        studentPerDepartmentChart.setVisible(true);
        barChart.setVisible(true);
        pieChart.setVisible(true);
        
    	addButton.setVisible(false);
    	deleteButton.setVisible(false);
    	searchField.setVisible(false);
    	searchButton.setVisible(false);
    	
    	studentTableView.setVisible(false);
        teacherTableView.setVisible(false);
        courseTableView.setVisible(false);
        subjectTableView.setVisible(false);
        
    	setupTableColumns();
		setupRowDoubleClickHandlers();
	    
		// Load Data From DB To Chart
		loadStudentPerDepartmentChart();
		loadUserRolePieChart();
		
		// Cập nhật các label thống kê
	    updateStatisticLabels();
		
	    // Load Data From DB
	    loadStudentsFromDatabase();
	    loadTeachersFromDatabase();
	    loadCoursesFromDatabase();
	    loadSubjectsFromDatabase();
	    
	    // Tính năng tìm kiếm real-time
	    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
	        handleSearch();
	    });
	
	}

    // Cập nhật các Label thống kê với dữ liệu từ DB
    private void updateStatisticLabels() {
        updateTotalStudents();
        updateTotalTeachers();
        updateTotalDepartments();
    }
    
    // Cập nhật Label Total Students
    private void updateTotalStudents() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "SELECT COUNT(s.id) FROM Student s";
            Long totalStudents = session.createQuery(hql, Long.class).uniqueResult();
            totalStudentsLabel.setText(totalStudents != null ? totalStudents.toString() : "0");
        } catch (Exception e) {
            e.printStackTrace();
            totalStudentsLabel.setText("0");
        } finally {
            session.close();
        }
    }
    
    // Cập nhật Label Total Teachers
    private void updateTotalTeachers() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "SELECT COUNT(t.id) FROM Teacher t";
            Long totalTeachers = session.createQuery(hql, Long.class).uniqueResult();
            totalTeachersLabel.setText(totalTeachers != null ? totalTeachers.toString() : "0");
        } catch (Exception e) {
            e.printStackTrace();
            totalTeachersLabel.setText("0");
        } finally {
            session.close();
        }
    }
    
    // Cập nhật Label Total Departments
    private void updateTotalDepartments() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "SELECT COUNT(DISTINCT d.id) FROM Department d";
            Long totalDepartments = session.createQuery(hql, Long.class).uniqueResult();
            totalDepartmentsLabel.setText(totalDepartments != null ? totalDepartments.toString() : "0");
        } catch (Exception e) {
            e.printStackTrace();
            totalDepartmentsLabel.setText("0");
        } finally {
            session.close();
        }
    }
    
    // Cập nhật làm mới biểu đồ
    private void refreshCharts() {
        // Xóa dữ liệu cũ
        studentPerDepartmentChart.getData().clear();
        userRolePieChart.getData().clear();
        
        // Load lại dữ liệu mới
        loadStudentPerDepartmentChart();
        loadUserRolePieChart();
    }
    
    // BarChart
    private void loadStudentPerDepartmentChart() {
    	
        Session session = HibernateUtil.getSessionFactory().openSession();
        String hql = "SELECT d.departmentName, COUNT(s.id) " +
                "FROM Student s JOIN s.department d " +
                "GROUP BY d.departmentName";
        // Kết quả truy xuất ở kiểu Object	
        List<Object[]> results = session.createQuery(hql, Object[].class).getResultList();
        session.close();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Students");

        for (Object[] row : results) {
            String departmentName = (String) row[0];
            Long studentCount = (Long) row[1];
            
            XYChart.Data<String, Number> data = new XYChart.Data<>(departmentName, studentCount);
            series.getData().add(data);
        }

        studentPerDepartmentChart.setTitle("Number of students per department");
        studentPerDepartmentChart.getData().add(series);
    }

    
    // PieChart
    private void loadUserRolePieChart() {
    	
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(u.userId) FROM User u WHERE u.role.roleName = :roleName";

            Query<Long> adminQuery = session.createQuery(hql, Long.class);
            adminQuery.setParameter("roleName", "ADMIN");
            Long adminCount = adminQuery.uniqueResult();

            Query<Long> teacherQuery = session.createQuery(hql, Long.class);
            teacherQuery.setParameter("roleName", "TEACHER");
            Long teacherCount = teacherQuery.uniqueResult();

            Query<Long> studentQuery = session.createQuery(hql, Long.class);
            studentQuery.setParameter("roleName", "STUDENT");
            Long studentCount = studentQuery.uniqueResult();

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Admin", adminCount != null ? adminCount : 0),
                new PieChart.Data("Teacher", teacherCount != null ? teacherCount : 0),
                new PieChart.Data("Student", studentCount != null ? studentCount : 0)
            );

            userRolePieChart.setData(pieChartData);
            userRolePieChart.setTitle("User Role Distribution");

            userRolePieChart.getData().forEach(data -> {
                data.nameProperty().bind(
                    javafx.beans.binding.Bindings.concat(
                        data.getName(), " - ",
                        javafx.beans.binding.Bindings.format("%.0f", data.pieValueProperty())
                    )
                );
            });
        }
    }

    private void setupTableColumns() {
    	
    	// Cấu hình Student TableView , String lấy từ lớp model
	    studentIDColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
	    studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
	    studentDOBColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
	    studentGenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
	    studentDepartmentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment().getDepartmentName()));
	    studentEnrollColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
	    
	    // Cấu hình Teacher TableView
	    teacherIDColumn.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
	    teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
	    teacherDOBColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
	    teacherGenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
	    teacherDepartmentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment().getDepartmentName()));
	    teacherHireColumn.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
	    
	    // Cấu hình Course TableView
	    courseIDColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
	    courseSubjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
		courseTeacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
		courseSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
		courseScheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
		courseRoomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
		courseMaxStudentColumn.setCellValueFactory(new PropertyValueFactory<>("maxStudents"));
	    
	    // Cấu hình Subject TableView
	    subjectIDColumn.setCellValueFactory(new PropertyValueFactory<>("subjectId"));
	    subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
	    subjectCreditColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));

    }

	// Phương thức để load dữ liệu từ cơ sở dữ liệu vào các TableView
    private void loadStudentsFromDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List<Student> studentList = session.createQuery("FROM Student", Student.class).list();
            allStudents.clear();
            allStudents.addAll(studentList);
            studentTableView.setItems(allStudents);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private void loadTeachersFromDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List<Teacher> teacherList = session.createQuery("FROM Teacher", Teacher.class).list();
            allTeachers.clear();
            allTeachers.addAll(teacherList);
            teacherTableView.setItems(allTeachers);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    
    private void loadCoursesFromDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List<Course> courseList = session.createQuery("FROM Course", Course.class).list();
            allCourses.clear();
            allCourses.addAll(courseList);
            courseTableView.setItems(allCourses);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
	
    
    private void loadSubjectsFromDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List<Subject> subjectList = session.createQuery("FROM Subject", Subject.class).list();
            allSubjects.clear();
            allSubjects.addAll(subjectList);
            subjectTableView.setItems(allSubjects);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    
    private void setupRowDoubleClickHandlers() {
        studentTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Student selected = studentTableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    // Lấy user tương ứng từ DB
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    try {
                        User user = selected.getUser(); 
                        openStudentDetailForm(selected,user);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy thông tin người dùng.");
                    } finally {
                        session.close();
                    }
                }
            }
        });

        teacherTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Teacher selected = teacherTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
            	Session session = HibernateUtil.getSessionFactory().openSession();
                try {
                    User user = selected.getUser(); 
                    openTeacherDetailForm(selected,user);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy thông tin người dùng.");
                } finally {
                    session.close();
                }
            }
            }
        });

        courseTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Course selected = courseTableView.getSelectionModel().getSelectedItem();
            	Session session = HibernateUtil.getSessionFactory().openSession();
                try {
                    openCourseDetailForm(selected);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy thông tin lớp học phần.");
                } finally {
                    session.close();
                }            }
        });

        subjectTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Subject selected = subjectTableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                	Session session = HibernateUtil.getSessionFactory().openSession();
                    try {
                        openSubjectDetailForm(selected);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy thông tin môn học.");
                    } finally {
                        session.close();
                    }
                }            
             }
        });
    }
    

    @FXML
    private void handleLogout(ActionEvent event) {
    	
    	 Optional<ButtonType> result = AlertUtil.showConfirmation("Xác Nhận Đăng Xuất","Bạn có muốn đăng xuất với vai trò Admin ?");
    	
    	// Xử lý sự kiện khi người dùng chọn "OK" hoặc "Cancel"
        if (result.isPresent() && result.get() == ButtonType.OK) {
          
            // Chuyển về Login.fxml
            try {
            	// Lấy stage hiện tại
            	 Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            	 // Nạp giao diện file Login.fxml
                 FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                 // Sau khi load tệp FXML, chúng ta tạo ra một Scene mới từ giao diện đã load
                 Scene scene = new Scene(loader.load());
                 stage.setScene(scene);
                 stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleDashboardButton() {
    	
    	addButton.setVisible(false);
    	deleteButton.setVisible(false);
    	searchField.setVisible(false);
    	searchButton.setVisible(false);
    	
    	// Dashboard Anchorpane
    	cardStatistic.setVisible(true);
        barChart.setVisible(true);
        pieChart.setVisible(true);
        
    	studentTableView.setVisible(false);
        teacherTableView.setVisible(false);
        courseTableView.setVisible(false);
        subjectTableView.setVisible(false);
        
        // Cập nhật lại thống kê khi quay về Dashboard
        updateStatisticLabels();
        
        // Cập nhật lại biểu đồ
        refreshCharts();
    }
    
    @FXML
    private void handleStudentsButton() {
    	
    	addButton.setVisible(true);
    	deleteButton.setVisible(true);
    	searchField.setVisible(true);
    	searchButton.setVisible(true);
    	
    	// TableView
        studentTableView.setVisible(true);
        teacherTableView.setVisible(false);
        courseTableView.setVisible(false);
        subjectTableView.setVisible(false);
        
        // Hide Dashboard Anchorpane
        cardStatistic.setVisible(false);
        barChart.setVisible(false);
        pieChart.setVisible(false);
        
        
        // Reset search field và hiển thị lại toàn bộ dữ liệu
        searchField.clear();
        studentTableView.setItems(allStudents);
        
    }
    
    // Bấm vào nút để hiển thị TableView tương ứng
    @FXML
    private void handleTeachersButton() {
    	
    	addButton.setVisible(true);
    	deleteButton.setVisible(true);
    	searchField.setVisible(true);
    	searchButton.setVisible(true);
    	
    	// Hide Dashboard Anchorpane
    	cardStatistic.setVisible(false);
        barChart.setVisible(false);
        pieChart.setVisible(false);
        
    	// Tableview
        studentTableView.setVisible(false);
        teacherTableView.setVisible(true);
        courseTableView.setVisible(false);
        subjectTableView.setVisible(false);
        
        // Reset search field và hiển thị lại toàn bộ dữ liệu
        searchField.clear();
        teacherTableView.setItems(allTeachers);
    }
    
    @FXML
    private void handleCoursesButton() {
    	
    	addButton.setVisible(true);
    	deleteButton.setVisible(true);
    	searchField.setVisible(true);
    	searchButton.setVisible(true);
    	
    	// Hide Dashboard Anchorpane
    	cardStatistic.setVisible(false);
        barChart.setVisible(false);
        pieChart.setVisible(false);
        
    	// Tableview
    	studentTableView.setVisible(false);
        teacherTableView.setVisible(false);
        courseTableView.setVisible(true);
        subjectTableView.setVisible(false);
        
        // Reset search field và hiển thị lại toàn bộ dữ liệu
        searchField.clear();
        courseTableView.setItems(allCourses);
    }
    
    @FXML
    private void handleSubjectsButton() {
    	
    	addButton.setVisible(true);
    	deleteButton.setVisible(true);
    	searchField.setVisible(true);
    	searchButton.setVisible(true);
    	
    	// Hide Dashboard Anchorpane
    	cardStatistic.setVisible(false);
        barChart.setVisible(false);
        pieChart.setVisible(false);
        
        
    	// Tableview
    	studentTableView.setVisible(false);
        teacherTableView.setVisible(false);
        courseTableView.setVisible(false);
        subjectTableView.setVisible(true);
        
        // Reset search field và hiển thị lại toàn bộ dữ liệu
        searchField.clear();
        subjectTableView.setItems(allSubjects);
    }
    
    @FXML
    private void handleSendNotificationButton() {
    	openForm("/FormAdminNotification.fxml", "Form Send Notification - Admin");
    }
    
    // Nút Add ngoài của trang Admin
    @FXML
    private void handleAddButton() {
    	
        if (studentTableView.isVisible()) {
            openForm("/FormAddStudent.fxml", "Form Add Student");
        } else if (teacherTableView.isVisible()) {
            openForm("/FormAddTeacher.fxml", "Form Add Teacher");
        } else if (courseTableView.isVisible()) {
        	openForm("/FormAddCourse.fxml", "Form Add Course");
        } else if (subjectTableView.isVisible()) {
        	openForm("/FormAddSubject.fxml", "Form Add Subject");
        }
    }

    @FXML
    private void handleDeleteButton() {
    	
    	// Student TableView
    	if (studentTableView.isVisible()) {
            Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();

            if (selectedStudent != null) {
                Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa sinh viên này?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction tx = session.beginTransaction();
                    
                    try {
                        // Truy vấn lại student từ session để nó được quản lý
                        Student student = session.get(Student.class, selectedStudent.getStudentId());

                        if (student != null) {                        	
                            session.remove(student); 
                            tx.commit();
                            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa sinh viên.");
                            loadStudentsFromDatabase();
                            
                        // Cập nhật thống kê sau khi xóa
	                        updateStatisticLabels();
	                        if (cardStatistic.isVisible()) {
	                            refreshCharts();
	                        }
                        }

                    } catch (Exception e) {
                        tx.rollback();
                        e.printStackTrace();
                        AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa sinh viên.");
                    } finally {
                        session.close();
                    }
                }
            } else {
                AlertUtil.showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn một sinh viên để xóa.");
            }   
            
        // Teacher TableView    
        } else if (teacherTableView.isVisible()) {
        	
        	Teacher selectedTeacher = teacherTableView.getSelectionModel().getSelectedItem();

            if (selectedTeacher != null) {
                Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa giảng viên này?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction tx = session.beginTransaction();
                    
                    try {
                    	
                        Teacher teacher = session.get(Teacher.class, selectedTeacher.getTeacherId());

                        if (teacher != null) {                        	
                            session.remove(teacher); 
                            tx.commit();
                            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa giảng viên.");
                            loadTeachersFromDatabase();
                        }

                    } catch (Exception e) {
                        tx.rollback();
                        e.printStackTrace();
                        AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa giảng viên.");
                        
                        // Cập nhật thống kê sau khi xóa
                        updateStatisticLabels();
                        if (cardStatistic.isVisible()) {
                            refreshCharts();
                        }
                        
                    } finally {
                        session.close();
                    }
                }
            } else {
                AlertUtil.showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn một giảng viên để xóa.");
            }   

        // Subject TableView
        } else if(subjectTableView.isVisible()) {
        	Subject selectedSubject = subjectTableView.getSelectionModel().getSelectedItem();

            if (selectedSubject != null) {
                Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa môn học này?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction tx = session.beginTransaction();
                    
                    try {
                    	
                        Subject subject = session.get(Subject.class, selectedSubject.getSubjectId());

                        if (subject != null) {                        	
                            session.remove(subject); 
                            tx.commit();
                            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa môn học.");
                            loadSubjectsFromDatabase();
                        }

                    } catch (Exception e) {
                        tx.rollback();
                        e.printStackTrace();
                        AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa môn học.");
                    } finally {
                        session.close();
                    }
                }
            } else {
                AlertUtil.showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn một môn học để xóa.");
            }   
            
        } else {
        	Course selectedCourse = courseTableView.getSelectionModel().getSelectedItem();

            if (selectedCourse != null) {
                Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận xóa", "Bạn có chắc chắn muốn xóa lớp học phần này?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                      
                    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    	Transaction tx = session.beginTransaction();
                    try {
                    	
                        Course course = session.get(Course.class, selectedCourse.getCourseId());

                        if (course != null) {                        	
                            session.remove(course); 
                            session.flush();
                            tx.commit();
                            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa lớp học phần.");
                            loadCoursesFromDatabase();
                        }
                    
                    } catch (Exception e) {
                        tx.rollback();
                        e.printStackTrace();
                        AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa lớp học phần.");
                    } finally {
                        session.close();
                    }
                }
            } else {
                AlertUtil.showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn một lớp học phần để xóa.");
            }   
        }
      }
    }
    	
    	private void openForm(String fxmlPath, String title) {
	    try {
	        URL fxmlUrl = getClass().getResource(fxmlPath);
	        if (fxmlUrl == null) {
	            System.out.println("Form not found: " + fxmlPath);
	            return;
	        }
	
	        FXMLLoader loader = new FXMLLoader(fxmlUrl);
	        Parent root = loader.load();
	
	        Stage stage = new Stage();
	        stage.setTitle(title);
	        stage.setScene(new Scene(root));
	        stage.centerOnScreen();
	        stage.show();
	        
	        // Sau khi form đóng, gọi lại load dữ liệu tương ứng
	        stage.setOnHiding(event -> {
	            if (fxmlPath.equals("/FormAddStudent.fxml")) {
	                loadStudentsFromDatabase();
	            } else if (fxmlPath.equals("/FormAddTeacher.fxml")) {
	                loadTeachersFromDatabase();
	            } else if (fxmlPath.equals("/FormAddCourse.fxml")) {
	                loadCoursesFromDatabase();
	            } else if (fxmlPath.equals("/FormAddSubject.fxml")) {
	                loadSubjectsFromDatabase();
	            }
	            
	            // Cập nhật lại thống kê sau khi có thay đổi
	            updateStatisticLabels();
	            
	            // Cập nhật lại biểu đồ nếu đang ở Dashboard
	            if (cardStatistic.isVisible()) {
	                refreshCharts();
	            }
	            
	        });
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

		// Khi bấm đúp vào một hàng trong TableView, sẽ hiện ra form thông tin 
    	private void openStudentDetailForm(Student student,User user) {
    		
    	    try {
    	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddStudent.fxml"));
    	        Parent root = loader.load();

    	        // Lấy controller và truyền dữ liệu vào
    	        FormAddStudentController controller = loader.getController();
    	        controller.setStudentData(student,user); // Phương thức bạn phải viết

    	        Stage stage = new Stage();
    	        stage.setTitle("Chi tiết Sinh viên");
    	        stage.setScene(new Scene(root));
    	        stage.centerOnScreen();
    	        stage.showAndWait(); // Đợi đến khi người dùng đóng form

    	        // Reload lại dữ liệu sau khi chỉnh sửa
    	        loadStudentsFromDatabase();

    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}
    	
    	
    	private void openTeacherDetailForm(Teacher teacher,User user) {
    		
    	    try {
    	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddTeacher.fxml"));
    	        Parent root = loader.load();

    	        // Lấy controller và truyền dữ liệu vào
    	        FormAddTeacherController controller = loader.getController();
    	        controller.setTeacherData(teacher,user); // Phương thức bạn phải viết ở trong "FormAddTeacher.fxml"

    	        Stage stage = new Stage();
    	        stage.setTitle("Chi tiết Giảng viên");
    	        stage.setScene(new Scene(root));
    	        stage.centerOnScreen();
    	        stage.showAndWait(); // Đợi đến khi người dùng đóng form

    	        // Reload lại dữ liệu sau khi chỉnh sửa
    	        loadTeachersFromDatabase();

    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}
    
    	private void openCourseDetailForm(Course course) {
    		
    	    try {
    	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddCourse.fxml"));
    	        Parent root = loader.load();

    	        // Lấy controller và truyền dữ liệu vào
    	        FormAddCourseController controller = loader.getController();
    	        controller.setCourseData(course); 

    	        Stage stage = new Stage();
    	        stage.setTitle("Chi tiết lớp học phần");
    	        stage.setScene(new Scene(root));
    	        stage.centerOnScreen();
    	        stage.showAndWait(); // Đợi đến khi người dùng đóng form

    	        // Reload lại dữ liệu sau khi chỉnh sửa
    	        loadCoursesFromDatabase();

    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}

    	private void openSubjectDetailForm(Subject subject) {
    		
    	    try {
    	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormAddSubject.fxml"));
    	        Parent root = loader.load();

    	        // Lấy controller và truyền dữ liệu vào
    	        FormAddSubjectController controller = loader.getController();
    	        controller.setSubjectData(subject); 

    	        Stage stage = new Stage();
    	        stage.setTitle("Chi tiết Môn học");
    	        stage.setScene(new Scene(root));
    	        stage.centerOnScreen();
    	        stage.showAndWait(); // Đợi đến khi người dùng đóng form

    	        // Reload lại dữ liệu sau khi chỉnh sửa
    	        loadSubjectsFromDatabase();

    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}
    	
    	
    	// SearchFunction
    	@FXML
    	private void handleSearch() {
    	    String keyword = searchField.getText().toLowerCase().trim();

    	    if (studentTableView.isVisible()) {
    	        FilteredList<Student> filteredData = new FilteredList<>(FXCollections.observableArrayList(allStudents), student ->
    	            student.getStudentId().toLowerCase().contains(keyword) ||
    	            student.getStudentName().toLowerCase().contains(keyword)
    	        );
    	        studentTableView.setItems(filteredData);
    	    }

    	    else if (teacherTableView.isVisible()) {
    	        FilteredList<Teacher> filteredData = new FilteredList<>(FXCollections.observableArrayList(allTeachers), teacher ->
    	            teacher.getTeacherId().toLowerCase().contains(keyword) ||
    	            teacher.getTeacherName().toLowerCase().contains(keyword)
    	        );
    	        teacherTableView.setItems(filteredData);
    	    }

    	    else if (courseTableView.isVisible()) {
    	        FilteredList<Course> filteredData = new FilteredList<>(FXCollections.observableArrayList(allCourses), course ->
    	            course.getCourseId().toLowerCase().contains(keyword) ||
    	            course.getSubject().getSubjectName().toLowerCase().contains(keyword)
    	        );
    	        courseTableView.setItems(filteredData);
    	    }

    	    else if (subjectTableView.isVisible()) {
    	        FilteredList<Subject> filteredData = new FilteredList<>(FXCollections.observableArrayList(allSubjects), subject ->
    	            subject.getSubjectId().toLowerCase().contains(keyword) ||
    	            subject.getSubjectName().toLowerCase().contains(keyword)
    	        );
    	        subjectTableView.setItems(filteredData);
    	    }
    	}

}
