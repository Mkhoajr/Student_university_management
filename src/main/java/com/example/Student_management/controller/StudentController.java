package com.example.Student_management.controller;

import java.awt.Desktop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.Student_management.dao.UserDAO;
import com.example.Student_management.model.Course;
import com.example.Student_management.model.Grade;
import com.example.Student_management.model.Student;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.util.GPACalculator;
import com.example.Student_management.util.GPASemesterDTO;
import com.example.Student_management.util.HibernateUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

public class StudentController {

	private ObservableList<Course> allAvailableCourses = FXCollections.observableArrayList();
	private ObservableList<Grade> allStudentGrades = FXCollections.observableArrayList();

    @FXML private TableView<Course> studentCoursesTableView;
    @FXML private TableView<Grade>  studentGradesTableView;
    @FXML private TableView<GPASemesterDTO>  gpaSummaryTableView;

    @FXML private TableColumn<Course, String> courseIDColumn;
    @FXML private TableColumn<Course, String> courseSubjectNameColumn;
    @FXML private TableColumn<Course, String> courseTeacherColumn;
    @FXML private TableColumn<Course, String> courseSemesterColumn;
    @FXML private TableColumn<Course, String> courseScheduleColumn;
    @FXML private TableColumn<Course, String> courseRoomColumn;
    @FXML private TableColumn<Course, Integer> courseAvailableSeatsColumn; // = MaxStudent

    @FXML private TableColumn<Grade, String> gradeCourseIDColumn;
    @FXML private TableColumn<Grade, String> gradeSubjectNameColumn;
    @FXML private TableColumn<Grade, String> gradeSemesterColumn;
    @FXML private TableColumn<Grade, Double> gradeAssignmentColumn;
    @FXML private TableColumn<Grade, Double> gradeMidtermColumn;
    @FXML private TableColumn<Grade, Double> gradeFinalColumn;
    @FXML private TableColumn<Grade, Double> gradeAverageColumn; // = totalScore
    @FXML private TableColumn<Grade, String> gradeLetterColumn;

    @FXML private TableColumn<GPASemesterDTO, String> gpaSemesterColumn;
    @FXML private TableColumn<GPASemesterDTO, Double> gpaCreditsColumn;
    @FXML private TableColumn<GPASemesterDTO, Double> gpa4PointColumn;
    @FXML private TableColumn<GPASemesterDTO, Double> gpa10PointColumn;
    @FXML private TableColumn<GPASemesterDTO, String> gpaClassificationColumn;
    
    @FXML private Button myRegisteredCoursesButton;
    @FXML private Button myCoursesButton;
    @FXML private Button myGradesButton;
    @FXML private Button logoutButton;
    @FXML private Button refreshButton;
    @FXML private Button searchButton;
    @FXML private Button exportButton;
    
    @FXML private TextField searchField;

    @FXML private Label coursesScoreLabel;
    @FXML private Label gpaLabel;

    private ObservableList<Course> courses = FXCollections.observableArrayList();

    private Student currentStudent;

    @FXML
    public void initialize() {
    	
        loadCurrentStudent();
        
        exportButton.setVisible(false);
    	coursesScoreLabel.setVisible(true);
        myRegisteredCoursesButton.setVisible(true);
        myRegisteredCoursesButton.setDisable(false);
        
        setupTableColumns();
        loadMyCourses();
        loadMyGrades();
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
	        handleSearch();
	    });
    }
    
    @FXML
    private void handleCourseRegistrationButton() {
    	
    	exportButton.setVisible(false);
    	coursesScoreLabel.setVisible(false);
    	gpaLabel.setVisible(false);
    	
    	studentCoursesTableView.setVisible(true);
        studentGradesTableView.setVisible(false);
        gpaSummaryTableView.setVisible(false);
        
        myRegisteredCoursesButton.setVisible(true);
        myRegisteredCoursesButton.setDisable(false);
        
        searchField.clear();
        studentCoursesTableView.setItems(courses);
    }
    
    @FXML
    private void handleCourseGradeButton() {
    	
    	exportButton.setVisible(true);
    	coursesScoreLabel.setVisible(true);
    	gpaLabel.setVisible(true);
    	
    	studentCoursesTableView.setVisible(false);
        studentGradesTableView.setVisible(true);
        gpaSummaryTableView.setVisible(true);
        
        myRegisteredCoursesButton.setVisible(false);
        myRegisteredCoursesButton.setDisable(true);
        
        searchField.clear();
        studentGradesTableView.setItems(FXCollections.observableArrayList(allStudentGrades));
        
        // Load và hiển thị GPA Summary
        loadGPASummary();
    }
    
    @FXML
    private void handleReceiveNotification() {
    	openForm("/FormStudentNotification.fxml", "Form Receive Notification - Student");
    }
    

    private void loadCurrentStudent() {
    	
        String currentUserId = UserDAO.getCurrentUser().getUserId();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.clear();
            List<Student> students = session.createQuery(
                "FROM Student s WHERE s.user.userId = :userId", Student.class)
                .setParameter("userId", currentUserId)
                .setCacheable(false)
                .list();

            if (!students.isEmpty()) {
                currentStudent = students.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(AlertType.ERROR, "Error", "Could not load student information.");
        }
    }

    private void setupTableColumns() {
    	
        // Columns for My Courses
        courseIDColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseSubjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        courseTeacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        courseSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        courseScheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        courseRoomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        courseAvailableSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("maxStudents"));

        // Columns for My Grades
        gradeCourseIDColumn.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().getCourse().getCourseId())
	    );
	    gradeSubjectNameColumn.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getCourse().getSubject().getSubjectName())
	    );
	    gradeSemesterColumn.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getSemester())
	    );
        gradeAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("assignmentScore"));
        gradeMidtermColumn.setCellValueFactory(new PropertyValueFactory<>("midtermScore"));
        gradeFinalColumn.setCellValueFactory(new PropertyValueFactory<>("finalScore"));
        gradeAverageColumn.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        gradeLetterColumn.setCellValueFactory(new PropertyValueFactory<>("letterGrade"));
        
        // Columns For GPA
        gpaSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        gpaCreditsColumn.setCellValueFactory(new PropertyValueFactory<>("totalCredits"));
        gpa4PointColumn.setCellValueFactory(new PropertyValueFactory<>("gpa4"));
        gpa10PointColumn.setCellValueFactory(new PropertyValueFactory<>("gpa10"));
        gpaClassificationColumn.setCellValueFactory(new PropertyValueFactory<>("classification"));

        
//        List<Grade> studentGrades = GradeDAO.getGradesByStudentId();
//        List<GPASemesterDTO> gpaList = GPACalculator.calculateGPABySemester(studentGrades);

        //gpaSummaryTableView.setItems(FXCollections.observableArrayList(gpaList));
        
    }

    // Load From DB
    private void loadMyCourses() {
        if (currentStudent == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.setHibernateFlushMode(FlushMode.ALWAYS);

            // Lấy danh sách tất cả các khóa học
            List<Course> allCourses = session.createQuery("FROM Course", Course.class).list();

            // Lấy lại thông tin student có danh sách khóa học
            Student student = session.get(Student.class, currentStudent.getStudentId());

            // Lọc ra các khóa học chưa đăng ký
            List<Course> available = allCourses.stream()
                .filter(course -> !student.getRegisteredCourses().contains(course))
                .toList();

            courses.setAll(available); // cập nhật lại danh sách
            allAvailableCourses.setAll(available);
            studentCoursesTableView.setItems(courses);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể tải danh sách lớp học phần.");
        }
    }


    private void loadMyGrades() {
    	
        if (currentStudent == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Grade> grades = session.createQuery(
                "FROM Grade g WHERE g.student.studentId = :studentId", Grade.class)
                .setParameter("studentId", currentStudent.getStudentId())
                .list();
            
            // Cập nhật cả studentGradesTableView và allStudentGrades
            ObservableList<Grade> gradesList = FXCollections.observableArrayList(grades);
            allStudentGrades.setAll(grades);
            studentGradesTableView.setItems(gradesList);	
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(AlertType.ERROR, "Error", "Could not load grades.");
        }
    }

    
    private void loadGPASummary() {
    	
        if (currentStudent == null || allStudentGrades.isEmpty()) return;
        
        try {
            // Sử dụng GPACalculator để tính GPA theo học kỳ
            List<GPASemesterDTO> gpaList = GPACalculator.calculateGPABySemester(allStudentGrades);
            
            // Hiển thị trên bảng GPA Summary
            ObservableList<GPASemesterDTO> gpaObservableList = FXCollections.observableArrayList(gpaList);
            gpaSummaryTableView.setItems(gpaObservableList);
            
            // Tính và hiển thị GPA tổng trên label
            if (!gpaList.isEmpty()) {
                double totalCredits = 0;
                double totalWeightedScore = 0;
                
                for (GPASemesterDTO gpa : gpaList) {
                    totalCredits += gpa.getTotalCredits();
                    totalWeightedScore += gpa.getGpa10() * gpa.getTotalCredits();
                }
                
                double overallGPA = totalCredits > 0 ? totalWeightedScore / totalCredits : 0;
                //gpaLabel.setText(String.format("Total GPA: %.2f", overallGPA));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(AlertType.ERROR, "Error", "Could not calculate GPA: " + e.getMessage());
        }
    }
    
    
    @FXML
    private void handleMyCoursesButton() {
        studentCoursesTableView.setVisible(true);
        studentGradesTableView.setVisible(false);
    }

    @FXML
    private void handleMyGradesButton() {
        studentCoursesTableView.setVisible(false);
        studentGradesTableView.setVisible(true);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        Optional<ButtonType> result = AlertUtil.showConfirmation(
            "Xác Nhận Đăng Xuất", "Bạn có muốn đăng xuất với vai trò Sinh Viên?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Nút đăng ký Lớp học phần
    @FXML
    private void handleRegisterButton(ActionEvent event) {

        Course selectedCourse = studentCoursesTableView.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            AlertUtil.showAlert(AlertType.ERROR,"Chưa chọn lớp học phần", "Vui lòng chọn một lớp học phần để đăng ký !");
            return;
        }

        Optional<ButtonType> result = AlertUtil.showConfirmation("Xác nhận đăng ký lớp học phần", "Bạn có chắc muốn đăng ký lớp học phần này không ?");
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Student student = session.get(Student.class, currentStudent.getStudentId());

            if (student.getRegisteredCourses().contains(selectedCourse)) {
                AlertUtil.showAlert(AlertType.ERROR,"Đã đăng ký", "Bạn đã đăng ký lớp học phần này !");
                return;
            }

            // Đăng ký khóa học
            student.getRegisteredCourses().add(selectedCourse);
            session.merge(student);  // cập nhật bảng student_course

            // Tạo điểm
            Grade grade = new Grade();
            grade.setStudent(student);
            grade.setCourse(selectedCourse);
            grade.setMidtermScore(null);
            grade.setFinalScore(null);
            grade.setAssignmentScore(null);
            grade.setLetterGrade(null);
            grade.setSemester(selectedCourse.getSemester());

            session.persist(grade);  // thêm bản ghi grade

            tx.commit();

            AlertUtil.showAlert(AlertType.INFORMATION,"Thành công", "Đăng ký khóa học thành công !");

            // Xóa khỏi danh sách
            courses.remove(selectedCourse);
            studentCoursesTableView.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Nút xem Lớp học phần đã đăng ký (Registered Courses)
    @FXML
    private void handleMyRegisteredCourses(ActionEvent event) {
    	
        if(studentCoursesTableView.isVisible()) {
        	 try {
        	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormRegisteredCourses.fxml"));
        	        Scene scene = new Scene(loader.load());

        	        FormRegisteredCoursesController controller = loader.getController();
        	        controller.setStudent(currentStudent); // truyền student đang đăng nhập
        	        
        	        // Đặt callback để refresh lại danh sách Course
                    controller.setCourseDropListener(() -> {
                        loadMyCourses(); // Gọi lại để cập nhật TableView
                    });

                    
        	        Stage stage = new Stage();
        	        stage.setTitle("Lớp học phần đã đăng ký");
        	        stage.setScene(scene);
        	        stage.setResizable(false);
        	        stage.show();
        	    } catch (Exception e) {
        	        e.printStackTrace();
        	    }
        }
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadCurrentStudent();
        loadMyCourses();
        loadMyGrades();
    }
    
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();
        
        if (studentCoursesTableView.isVisible()) {
            // Tìm kiếm trong danh sách khóa học có sẵn
            FilteredList<Course> filteredData = new FilteredList<>(allAvailableCourses, course ->
                course.getCourseId().toLowerCase().contains(keyword) ||
                course.getSubject().getSubjectName().toLowerCase().contains(keyword) ||
                course.getTeacher().getTeacherName().toLowerCase().contains(keyword) ||
                course.getSemester().toLowerCase().contains(keyword) ||
                course.getSchedule().toLowerCase().contains(keyword) ||
                course.getRoom().toLowerCase().contains(keyword)
            );
            studentCoursesTableView.setItems(filteredData);
        }
        else if (studentGradesTableView.isVisible()) {
            // Tìm kiếm trong danh sách điểm số
            FilteredList<Grade> filteredData = new FilteredList<>(allStudentGrades, grade ->
                grade.getCourse().getCourseId().toLowerCase().contains(keyword) ||
                grade.getCourse().getSubject().getSubjectName().toLowerCase().contains(keyword) ||
                grade.getSemester().toLowerCase().contains(keyword) ||
                (grade.getLetterGrade() != null && grade.getLetterGrade().toLowerCase().contains(keyword)) ||
                (grade.getAssignmentScore() != null && grade.getAssignmentScore().toString().contains(keyword)) ||
                (grade.getMidtermScore() != null && grade.getMidtermScore().toString().contains(keyword)) ||
                (grade.getFinalScore() != null && grade.getFinalScore().toString().contains(keyword)) ||
                (grade.getTotalScore() != null && grade.getTotalScore().toString().contains(keyword))
            );
            studentGradesTableView.setItems(filteredData);
        }
    }
    

    @FXML
    private void handleExportPDF(ActionEvent event) {
        if (currentStudent == null) {
            AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể tải thông tin sinh viên.");
            return;
        }

        if (allStudentGrades.isEmpty()) {
            AlertUtil.showAlert(AlertType.WARNING, "Cảnh báo", "Không có dữ liệu điểm để xuất.");
            return;
        }

        // Mở dialog chọn nơi lưu file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu bảng điểm PDF");
        fileChooser.setInitialDirectory(new File("E:\\\\Student_management"));
        fileChooser.setInitialFileName("BangDiem_" + currentStudent.getStudentId() + "_" + 
                                       new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                createPDFReport(file.getAbsolutePath());
                AlertUtil.showAlert(AlertType.INFORMATION, "Thành công", 
                                  "Đã xuất bảng điểm thành công!\nFile được lưu tại: " + file.getAbsolutePath());
              
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showAlert(AlertType.ERROR, "Lỗi", "Không thể xuất file PDF: " + e.getMessage());
            }
        }
    }
    
    private void createPDFReport(String filePath) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Font cho tiếng Việt
        BaseFont baseFont = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(baseFont, 18, Font.BOLD);
        Font headerFont = new Font(baseFont, 12, Font.BOLD);
        Font normalFont = new Font(baseFont, 10, Font.NORMAL);

        // Tiêu đề
        Paragraph title = new Paragraph("BẢNG ĐIỂM SINH VIÊN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Thông tin sinh viên
        Paragraph studentInfo = new Paragraph();
        studentInfo.add(new Chunk("Mã sinh viên: ", headerFont));
        studentInfo.add(new Chunk(currentStudent.getStudentId(), normalFont));
        studentInfo.add(Chunk.NEWLINE);
        
        if (currentStudent.getUser() != null) {
            studentInfo.add(new Chunk("Họ tên: ", headerFont));
            studentInfo.add(new Chunk(currentStudent.getStudentName(), normalFont));
            studentInfo.add(Chunk.NEWLINE);
        }
        
        studentInfo.add(new Chunk("Ngày xuất: ", headerFont));
        studentInfo.add(new Chunk(new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(new Date()), normalFont));
        studentInfo.setSpacingAfter(20);
        document.add(studentInfo);

        // Tạo bảng điểm
        PdfPTable table = new PdfPTable(8); // 8 cột
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // Thiết lập độ rộng cột
        float[] columnWidths = {3f, 3f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f};
        table.setWidths(columnWidths);

        // Header của bảng
        String[] headers = {"Mã môn học", "Tên môn học", "Học kỳ", "Điểm BT", "Điểm GK", "Điểm CK", "Điểm TB", "Điểm chữ"};
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // Dữ liệu điểm
        double totalScore = 0;
        int gradeCount = 0;
        
        for (Grade grade : allStudentGrades) {
            // Mã môn học
            table.addCell(createCell(grade.getCourse().getCourseId(), normalFont));
            
            // Tên môn học
            table.addCell(createCell(grade.getCourse().getSubject().getSubjectName(), normalFont));
            
            // Học kỳ
            table.addCell(createCell(grade.getSemester(), normalFont));
            
            // Điểm bài tập
            table.addCell(createCell(grade.getAssignmentScore() != null ? 
                                    String.format("%.1f", grade.getAssignmentScore()) : "--", normalFont));
            
            // Điểm giữa kỳ
            table.addCell(createCell(grade.getMidtermScore() != null ? 
                                    String.format("%.1f", grade.getMidtermScore()) : "--", normalFont));
            
            // Điểm cuối kỳ
            table.addCell(createCell(grade.getFinalScore() != null ? 
                                    String.format("%.1f", grade.getFinalScore()) : "--", normalFont));
            
            // Điểm trung bình
            String avgScore = "--";
            if (grade.getTotalScore() != null) {
                avgScore = String.format("%.1f", grade.getTotalScore());
                totalScore += grade.getTotalScore().doubleValue();
                gradeCount++;
            }
            table.addCell(createCell(avgScore, normalFont));
            
            // Điểm chữ
            table.addCell(createCell(grade.getLetterGrade() != null ? grade.getLetterGrade() : "--", normalFont));
        }

        document.add(table);

        // Tính GPA
        if (gradeCount > 0) {
            double gpa = totalScore / gradeCount;
            Paragraph gpaInfo = new Paragraph();
            gpaInfo.setSpacingBefore(20);
            gpaInfo.add(new Chunk("Điểm trung bình tích lũy (GPA): ", headerFont));
            gpaInfo.add(new Chunk(String.format("%.2f", gpa), headerFont));
            gpaInfo.setAlignment(Element.ALIGN_RIGHT);
            document.add(gpaInfo);
        }

        // Footer
        Paragraph footer = new Paragraph();
        footer.setSpacingBefore(30);
        footer.add(new Chunk("--- Hết ---", normalFont));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        
        // Mở file ngay sau khi xuất
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(filePath)); // Mở bằng ứng dụng mặc định (thường là Excel)
        }
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        return cell;
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
