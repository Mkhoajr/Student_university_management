package com.example.Student_management.controller;

import java.awt.Desktop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

////import java.math.BigDecimal;
//import com.itextpdf.text.*;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.pdf.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.FlushMode;
import org.hibernate.Session;

import com.example.Student_management.dao.UserDAO;
import com.example.Student_management.model.Course;
import com.example.Student_management.model.Grade;
import com.example.Student_management.model.Teacher;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.util.HibernateUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TeacherController {
	
	private ObservableList<Course> allTeacherCourses = FXCollections.observableArrayList();
	private ObservableList<Grade> allStudentGrades = FXCollections.observableArrayList();
	
	// TableView and TableColumns for courses
	
	@FXML private ComboBox<Course> courseFilterComboBox;
	
    @FXML private TableView<Course> teacherCoursesTableView;
    @FXML private TableView<Grade> studentGradingTableView;

    // View Course
    @FXML private TableColumn<Course, String> courseIDColumn;
    @FXML private TableColumn<Course, String> courseSubjectNameColumn;
    @FXML private TableColumn<Course, String> courseSemesterColumn;
    @FXML private TableColumn<Course, String> courseScheduleColumn;
    @FXML private TableColumn<Course, String> courseRoomColumn;
    @FXML private TableColumn<Course, Integer> courseMaxStudentColumn;
    
    // View Student's grade
    @FXML private TableColumn<Grade, String> studentIDColumn;
    @FXML private TableColumn<Grade, String> studentNameColumn;
    @FXML private TableColumn<Grade, Double> assigmentScoreColumn;
    @FXML private TableColumn<Grade, Double> midtermScoreColumn	;
    @FXML private TableColumn<Grade, Double> finalScoreColumn;
    @FXML private TableColumn<Grade, Double> averageScoreColumn;
    @FXML private TableColumn<Grade, String> letterGradeColumn;
    
   
    @FXML private Button coursesButton;
    @FXML private Button studentGradingButton;
    @FXML private Button logoutButton;
    @FXML private Button refreshButton;
    @FXML private Button searchButton;
    @FXML private Button exportButton;
    
    @FXML private TextField searchField;
    // Reference to the current teacher
    private Teacher currentTeacher;

    @FXML
    public void initialize() {

    	setupRowDoubleClickHandlers();

    	// Get the current logged-in teacher
        loadCurrentTeacher();
        
        exportButton.setVisible(false);
        courseFilterComboBox.setVisible(false);
    	courseFilterComboBox.setDisable(true);
    	
        setupTableColumns();
        
        // Load courses for the current teacher
        loadTeacherCoursesFromDatabase();
        courseFilterComboBox.setOnAction(e -> {
            Course selectedCourse = courseFilterComboBox.getSelectionModel().getSelectedItem();
            if (selectedCourse != null) {
                loadStudentGradesForCourse(selectedCourse);
            }
        });
        
        teacherCoursesTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadStudentGradesForCourse(newSelection);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
	        handleSearch();
	    });
        
      }
    
    @FXML
    private void handleMyCoursesButton() {
    	
    	exportButton.setVisible(false);
    	teacherCoursesTableView.setVisible(true);
    	studentGradingTableView.setVisible(false);
    	courseFilterComboBox.setVisible(false);
    	courseFilterComboBox.setDisable(true);
    	
    	searchField.clear();
        teacherCoursesTableView.setItems(allTeacherCourses);
    }
    
    @FXML
    private void handleStudentGradingButton() {
    	
    	exportButton.setVisible(true);
    	teacherCoursesTableView.setVisible(false);
    	studentGradingTableView.setVisible(true);
    	courseFilterComboBox.setVisible(true);
    	courseFilterComboBox.setDisable(false);
    	
    	searchField.clear();
        Course selectedCourse = courseFilterComboBox.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            studentGradingTableView.setItems(allStudentGrades);
        }
    }
    
    @FXML
    private void handleReceiveNotification() {
    	openForm("/FormTeacherNotification.fxml", "Form Receive Notification - Teacher");
    }
    
    // Lấy giáo viên hiện tại
    private void loadCurrentTeacher() {
    	
        // Get current user ID from your authentication system
        String currentUserId = UserDAO.getCurrentUser().getUserId();
        
        // Use try-with-resources to ensure session is closed
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        	
        	session.clear(); // Clear first-level cache
        	
            // Query to find the teacher with the current user ID
            List<Teacher> teachers = session.createQuery(
                "FROM Teacher t WHERE t.user.userId = :userId", Teacher.class)
                .setParameter("userId", currentUserId)
                .setCacheable(false) // Tắt cache cho truy vấn này
                .list();
            
            if (!teachers.isEmpty()) {
                currentTeacher = teachers.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(AlertType.ERROR, "Error", "Could not load teacher information.");
        } 
    }
    
    private void setupTableColumns() {
    	
        // Set up the columns for the courses table
        courseIDColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseSubjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        courseSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        courseScheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        courseRoomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        courseMaxStudentColumn.setCellValueFactory(new PropertyValueFactory<>("maxStudents"));
        
        // Set up the columns for student's grade table
        studentIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudent().getStudentId()));
        studentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudent().getStudentName()));
        assigmentScoreColumn.setCellValueFactory(new PropertyValueFactory<>("assignmentScore"));
        midtermScoreColumn.setCellValueFactory(new PropertyValueFactory<>("midtermScore"));
        finalScoreColumn.setCellValueFactory(new PropertyValueFactory<>("finalScore"));
        averageScoreColumn.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        letterGradeColumn.setCellValueFactory(new PropertyValueFactory<>("letterGrade"));

    }
    
    private void loadTeacherCoursesFromDatabase() {
        if (currentTeacher == null) {
            return;
        }
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Đặt flush mode để đảm bảo dữ liệu mới nhất được lấy
            session.setHibernateFlushMode(FlushMode.ALWAYS);
            session.clear(); // Xóa cache cấp 1
            
            // Load lại giáo viên hiện tại từ DB
            Teacher freshTeacher = session.get(Teacher.class, currentTeacher.getTeacherId());
            
            if (freshTeacher == null) {
                System.out.println("Không tìm thấy giáo viên với ID: " + currentTeacher.getTeacherId());
                AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể tìm thấy giảng viên.");
                return;
            }
            
            // Query courses assigned to the current teacher
            List<Course> courses = session.createQuery(
                "FROM Course c WHERE c.teacher.teacherId = :teacherId", Course.class)
                .setParameter("teacherId", freshTeacher.getTeacherId())
                .setCacheable(false) // Tắt cache cho truy vấn này
                .list();
            
            // Lưu danh sách gốc
            allTeacherCourses.setAll(courses);
            
            // Cập nhật ComboBox với danh sách khóa học mới
            courseFilterComboBox.setItems(FXCollections.observableArrayList(courses));
            teacherCoursesTableView.setItems(FXCollections.observableArrayList(courses)); 

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(AlertType.ERROR, "Error", "Could not load courses!");
        }
    }
    
	
	private void loadStudentGradesForCourse(Course selectedCourse) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        List<Grade> grades = session.createQuery(
	            "FROM Grade g WHERE g.course.courseId = :courseId", Grade.class)
	            .setParameter("courseId", selectedCourse.getCourseId())
	            .list();
	
	        // Sắp xếp theo tên sinh viên (bảng chữ cái)
	        grades.sort((g1, g2) -> g1.getStudent().getStudentName().compareToIgnoreCase(g2.getStudent().getStudentName()));
	        
	        // Lưu danh sách gốc
	        allStudentGrades.setAll(grades);
	        
	        studentGradingTableView.setItems(FXCollections.observableArrayList(grades));
	    } catch (Exception e) {
	        e.printStackTrace();
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể tải danh sách sinh viên.");
	    }
	}

	// Thêm vào TeacherController một hàm để reload lại danh sách điểm của môn học hiện tại
	public void refreshStudentGradeTable() {
	    Course selectedCourse = courseFilterComboBox.getSelectionModel().getSelectedItem();
	    if (selectedCourse != null) {
	        loadStudentGradesForCourse(selectedCourse);
	    }
	}

	@FXML
    private void handleLogout(ActionEvent event) {
    	
    	 Optional<ButtonType> result = AlertUtil.showConfirmation("Xác Nhận Đăng Xuất","Bạn có muốn đăng xuất với vai trò Giảng Viên ?");
    	
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
	private void handleRefresh(ActionEvent event) {
	    loadCurrentTeacher(); 
		loadTeacherCoursesFromDatabase(); 
	}
	
	private void setupRowDoubleClickHandlers() { 
		
		studentGradingTableView.setOnMouseClicked(event -> {
		    if (event.getClickCount() == 2 && !studentGradingTableView.getSelectionModel().isEmpty()) {
		        Grade selectedGrade = studentGradingTableView.getSelectionModel().getSelectedItem();
		        if (selectedGrade != null) {
		            openGradingForm(selectedGrade);
		        }
		    }
		});
	}
	
	private void openGradingForm(Grade grade) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormGradeEntry.fxml"));
	        Scene scene = new Scene(loader.load());

	        // Truyền dữ liệu sang controller của form chấm điểm
	        FormStudentGradingController controller = loader.getController();
	        controller.setGrade(grade);
	        // Truyền TeacherController vào FormStudentGradingController khi mở form
	        controller.setTeacherController(this); 
	        
	        // Tạo stage mới cho form chấm điểm
	        Stage stage = new Stage();
	        stage.setScene(scene);
	        stage.setTitle("Chấm điểm sinh viên");
	        stage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể mở form chấm điểm.");
	    }
	}

	@FXML
	private void handleSearch() {
	    String keyword = searchField.getText().toLowerCase().trim();

	    if (teacherCoursesTableView.isVisible()) {
	        FilteredList<Course> filteredData = new FilteredList<>(FXCollections.observableArrayList(allTeacherCourses), course ->
	            course.getCourseId().toLowerCase().contains(keyword) ||
	            course.getSubject().getSubjectName().toLowerCase().contains(keyword) ||
	            course.getSemester().toLowerCase().contains(keyword) ||
	            course.getSchedule().toLowerCase().contains(keyword) ||
	            course.getRoom().toLowerCase().contains(keyword)
	        );
	        teacherCoursesTableView.setItems(filteredData);
	    }
	    else if (studentGradingTableView.isVisible()) {
	        FilteredList<Grade> filteredData = new FilteredList<>(FXCollections.observableArrayList(allStudentGrades), grade ->
	            grade.getStudent().getStudentId().toLowerCase().contains(keyword) ||
	            grade.getStudent().getStudentName().toLowerCase().contains(keyword) ||
	            (grade.getLetterGrade() != null && grade.getLetterGrade().toLowerCase().contains(keyword)) ||
	            (grade.getAssignmentScore() != null && grade.getAssignmentScore().toString().contains(keyword)) ||
	            (grade.getMidtermScore() != null && grade.getMidtermScore().toString().contains(keyword)) ||
	            (grade.getFinalScore() != null && grade.getFinalScore().toString().contains(keyword)) ||
	            (grade.getTotalScore() != null && grade.getTotalScore().toString().contains(keyword))
	        );
	        studentGradingTableView.setItems(filteredData);
	    }
	}
	
	// Phương thức xuất File 
	@FXML
	private void handleExportButton(ActionEvent event) {
	    Course selectedCourse = courseFilterComboBox.getSelectionModel().getSelectedItem();
	    
	    if (selectedCourse == null) {
	        AlertUtil.showAlert(AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một khóa học để xuất file Excel.");
	        return;
	    }
	    
	    if (allStudentGrades.isEmpty()) {
	        AlertUtil.showAlert(AlertType.WARNING, "Cảnh báo", "Không có dữ liệu điểm để xuất file.");
	        return;
	    }
	    
	    // Tạo FileChooser để người dùng chọn vị trí lưu file
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Lưu file Excel");
	    fileChooser.setInitialDirectory(new File("E:\\Student_management"));
	    
	    // Đặt tên file mặc định
	    String defaultFileName = String.format("BangDiem_%s_%s_%s.xlsx", 
	        selectedCourse.getSubject().getSubjectName().replaceAll("\\s+", "_"),
	        selectedCourse.getSemester(),
	        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")));
	    
	    fileChooser.setInitialFileName(defaultFileName);
	    
	    // Chỉ cho phép lưu file Excel
	    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
	    fileChooser.getExtensionFilters().add(extFilter);
	    
	    // Hiển thị dialog lưu file
	    Stage stage = (Stage) exportButton.getScene().getWindow();
	    File file = fileChooser.showSaveDialog(stage);
	    
	    if (file != null) {
	        exportToExcel(file, selectedCourse);
	    }
	}
	
	// Method thực hiện export dữ liệu ra Excel
	private void exportToExcel(File file, Course course) {
	    try (Workbook workbook = new XSSFWorkbook()) {
	        Sheet sheet = workbook.createSheet("Bảng Điểm");
	        
	        // Tạo style cho header
	        CellStyle headerStyle = workbook.createCellStyle();
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setFontHeightInPoints((short) 12);
	        headerStyle.setFont(headerFont);
	        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
	        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        headerStyle.setBorderBottom(BorderStyle.THIN);
	        headerStyle.setBorderTop(BorderStyle.THIN);
	        headerStyle.setBorderRight(BorderStyle.THIN);
	        headerStyle.setBorderLeft(BorderStyle.THIN);
	        headerStyle.setAlignment(HorizontalAlignment.CENTER);
	        
	        // Tạo style cho data cells
	        CellStyle dataStyle = workbook.createCellStyle();
	        dataStyle.setBorderBottom(BorderStyle.THIN);
	        dataStyle.setBorderTop(BorderStyle.THIN);
	        dataStyle.setBorderRight(BorderStyle.THIN);
	        dataStyle.setBorderLeft(BorderStyle.THIN);
	        
	        // Tạo style cho số điểm
	        CellStyle numberStyle = workbook.createCellStyle();
	        numberStyle.cloneStyleFrom(dataStyle);
	        numberStyle.setAlignment(HorizontalAlignment.RIGHT);
	        
	        int rowNum = 0;
	        
	        // Tạo tiêu đề thông tin khóa học
	        Row titleRow = sheet.createRow(rowNum++);
	        Cell titleCell = titleRow.createCell(0);
	        titleCell.setCellValue("BẢNG ĐIỂM SINH VIÊN");
	        titleCell.setCellStyle(headerStyle);
	        
	        // Merge cells cho tiêu đề
	        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));
	        
	        rowNum++; // Dòng trống
	        
	        // Thông tin khóa học
	        Row courseInfoRow1 = sheet.createRow(rowNum++);
	        courseInfoRow1.createCell(0).setCellValue("Mã khóa học:");
	        courseInfoRow1.createCell(1).setCellValue(course.getCourseId());
	        courseInfoRow1.createCell(3).setCellValue("Môn học:");
	        courseInfoRow1.createCell(4).setCellValue(course.getSubject().getSubjectName());
	        
	        Row courseInfoRow2 = sheet.createRow(rowNum++);
	        courseInfoRow2.createCell(0).setCellValue("Học kì:");
	        courseInfoRow2.createCell(1).setCellValue(course.getSemester());
	        courseInfoRow2.createCell(3).setCellValue("Phòng:");
	        courseInfoRow2.createCell(4).setCellValue(course.getRoom());
	        
	        Row courseInfoRow3 = sheet.createRow(rowNum++);
	        courseInfoRow3.createCell(0).setCellValue("Lịch học:");
	        courseInfoRow3.createCell(1).setCellValue(course.getSchedule());
	        courseInfoRow3.createCell(3).setCellValue("Giảng viên:");
	        courseInfoRow3.createCell(4).setCellValue(currentTeacher.getTeacherName());
	        
	        Row courseInfoRow4 = sheet.createRow(rowNum++);
	        courseInfoRow4.createCell(0).setCellValue("Ngày xuất:");
	        courseInfoRow4.createCell(1).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
	        
	        rowNum++; // Dòng trống
	        
	        // Tạo header row cho bảng điểm
	        Row headerRow = sheet.createRow(rowNum++);
	        String[] headers = {"STT", "Mã SV", "Họ tên", "Điểm BT", "Điểm GK", "Điểm CK", "Điểm TB", "Xếp loại"};
	        
	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	        }
	        
	        // Thêm dữ liệu điểm
	        int stt = 1;
	        for (Grade grade : allStudentGrades) {
	            Row row = sheet.createRow(rowNum++);
	            
	            // STT
	            Cell sttCell = row.createCell(0);
	            sttCell.setCellValue(stt++);
	            sttCell.setCellStyle(dataStyle);
	            
	            // Mã sinh viên
	            Cell studentIdCell = row.createCell(1);
	            studentIdCell.setCellValue(grade.getStudent().getStudentId());
	            studentIdCell.setCellStyle(dataStyle);
	            
	            // Tên sinh viên
	            Cell studentNameCell = row.createCell(2);
	            studentNameCell.setCellValue(grade.getStudent().getStudentName());
	            studentNameCell.setCellStyle(dataStyle);
	            
	            // Điểm bài tập
	            Cell assignmentCell = row.createCell(3);
	            if (grade.getAssignmentScore() != null) {
	                assignmentCell.setCellValue(grade.getAssignmentScore().doubleValue());
	            } else {
	                assignmentCell.setCellValue("--");
	            }
	            assignmentCell.setCellStyle(numberStyle);
	            
	            // Điểm giữa kì
	            Cell midtermCell = row.createCell(4);
	            if (grade.getMidtermScore() != null) {
	                midtermCell.setCellValue(grade.getMidtermScore().doubleValue());
	            } else {
	                midtermCell.setCellValue("--");
	            }
	            midtermCell.setCellStyle(numberStyle);
	            
	            // Điểm cuối kì
	            Cell finalCell = row.createCell(5);
	            if (grade.getFinalScore() != null) {
	                finalCell.setCellValue(grade.getFinalScore().doubleValue());
	            } else {
	                finalCell.setCellValue("--");
	            }
	            finalCell.setCellStyle(numberStyle);
	            
	            // Điểm trung bình
	            Cell totalCell = row.createCell(6);
	            if (grade.getTotalScore() != null) {
	                totalCell.setCellValue(grade.getTotalScore().doubleValue());
	            } else {
	                totalCell.setCellValue("--");
	            }
	            totalCell.setCellStyle(numberStyle);
	            
	            // Xếp loại
	            Cell gradeCell = row.createCell(7);
	            if (grade.getLetterGrade() != null) {
	                gradeCell.setCellValue(grade.getLetterGrade());
	            } else {
	                gradeCell.setCellValue("--");
	            }
	            gradeCell.setCellStyle(dataStyle);
	        }
	        
	        // Tự động điều chỉnh độ rộng cột
	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	            // Set minimum width
	            if (sheet.getColumnWidth(i) < 2000) {
	                sheet.setColumnWidth(i, 2000);
	            }
	        }
	        
	        // Lưu file
	        try (FileOutputStream fileOut = new FileOutputStream(file)) {
	            workbook.write(fileOut);
	        }
	        
	        // Mở file ngay sau khi xuất
	        if (Desktop.isDesktopSupported()) {
	            Desktop.getDesktop().open(file); // Mở bằng ứng dụng mặc định (thường là Excel)
	        }
	        
	        AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", 
	            "Đã xuất file Excel thành công!\nVị trí: " + file.getAbsolutePath());
	            
	    } catch (Exception e) {
	        e.printStackTrace();
	        AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể xuất file Excel: " + e.getMessage());
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
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
}
