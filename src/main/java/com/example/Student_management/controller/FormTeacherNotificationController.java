package com.example.Student_management.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.Student_management.dao.NotificationDAO;
import com.example.Student_management.dao.UserDAO;
import com.example.Student_management.model.Notification;
import com.example.Student_management.util.AlertUtil;
import com.example.Student_management.socket.TeacherClient;
import com.example.Student_management.socket.TeacherSocketListener;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FormTeacherNotificationController {

    @FXML private TableView<Notification> messageTable;
    @FXML private TableColumn<Notification, String> titleColumn;
    @FXML private TableColumn<Notification, LocalDate> dateColumn;
    @FXML private TableColumn<Notification, LocalTime> timeColumn;
    @FXML private TableColumn<Notification, String> senderColumn;

    // Tab1
    @FXML private TextArea messageContentArea;
    @FXML private TextArea composeMessageArea;
    // Tab2
    @FXML private TextArea messageSendContentArea;
    
    @FXML private Label tab1DetailDateLabel;
    @FXML private Label tab2DetailDateLabel;
    
    // Tab1
    @FXML private Button deleteBtn; // ClearForm Btn
    @FXML private Button clearBtnTab1; // Clear Btn
    
    // Tab2
    @FXML private Button sendToAdminBtn, clearFormBtn;
    @FXML private Button deleteSentBtn;
    @FXML private Button clearBtnTab2;

    @FXML private TextField subjectField;

    @FXML private TableView<Notification> sentMessagesTable;
    @FXML private TableColumn<Notification, String> sentSubjectColumn;
    @FXML private TableColumn<Notification, LocalDate> sentDateColumn;
    @FXML private TableColumn<Notification, LocalTime> sentTimeColumn;

    private NotificationDAO notificationDAO;
    private TeacherClient teacherClient;

    private final String currentSenderRole = "teacher"; // Hoặc "student" nếu là bên sinh viên
    private final String currentSenderName = UserDAO.getCurrentUser().getUserName(); 
    
    @FXML
    public void initialize() {
    	
        notificationDAO = new NotificationDAO();
        teacherClient = new TeacherClient(); // Socket gửi thông báo real-time (nếu có)

        // Real-time
        new TeacherSocketListener(this).start();

        setupTables();
        loadInbox();
        loadSentMessages();

        messageTable.setOnMouseClicked(e -> tab1ShowMessageDetails());
        sentMessagesTable.setOnMouseClicked(e -> tab2ShowMessageDetails());
        
        deleteBtn.setOnAction(e -> handleDeleteReceived());
        deleteSentBtn.setOnAction(e -> handleDeleteSent()); //delete Sent Message Button
        
        sendToAdminBtn.setOnAction(e -> handleSendToAdmin());
        
        clearFormBtn.setOnAction(e -> clearComposeForm()); // Tab2
        
        clearBtnTab1.setOnAction(e -> tab1ClearNotificationDetail());
        clearBtnTab2.setOnAction(e -> tab2ClearNotificationDetail());
    }
    
    public void refreshInboxFromSocket() {
        javafx.application.Platform.runLater(() -> {
            loadInbox();
        });
    }

    private void setupTables() {
    	
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("sendTime"));
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("senderRole"));

        sentSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        sentDateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
        sentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sendTime"));
    }

    private void loadInbox() {
        List<Notification> inboxList = notificationDAO.getNotificationsForReceiver(currentSenderRole);
        messageTable.setItems(FXCollections.observableArrayList(inboxList));
    }

    private void loadSentMessages() {
        List<Notification> sentList = notificationDAO.getNotificationsFromSender(currentSenderRole);
        sentMessagesTable.setItems(FXCollections.observableArrayList(sentList));
    }

    // ShowMessageDetails in Tab1 When click a row in Message Table
    private void tab1ShowMessageDetails() {
        Notification selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
        	tab1DetailDateLabel.setText(selected.getSendDate() + " " + selected.getSendTime());
            messageContentArea.setText(selected.getContent());
        }
    }
    
    // ShowMessageDetails in Tab2 When click a row in Message Table
    private void tab2ShowMessageDetails() {
        Notification selected = sentMessagesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
        	tab2DetailDateLabel.setText(selected.getSendDate() + " " + selected.getSendTime());
        	messageSendContentArea.setText(selected.getContent());
        }
    }

    private void handleSendToAdmin() {
		
	    String subject = subjectField.getText();
	    String content = composeMessageArea.getText();
	
	    if (subject.isEmpty() || content.isEmpty()) {
	        AlertUtil.showAlert(Alert.AlertType.WARNING, "Lack Of Information", "Please enter the Title and Content.");
	        return;
	    }
	
	    Notification notification = new Notification();
	    notification.setTitle(subject);
	    notification.setContent(content);
	    notification.setSenderRole(currentSenderRole);
	    notification.setSenderName(currentSenderName);
	    notification.setReceiverRole("admin");
	    notification.setSendDate(LocalDate.now());
	    notification.setSendTime(LocalTime.now());
	
	    notificationDAO.saveNotification(notification);
	    teacherClient.sendNotification("admin", subject, content); // Gửi socket nếu cần
	
	    loadSentMessages();
	    clearComposeForm();
	    AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Send Successfully", "Message sent to Admin.");
	}

	// Handle Delete Received Message
    private void handleDeleteReceived() {
    	
        Notification selected = messageTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showAlert(Alert.AlertType.WARNING, "No Messages Selected Yet", "Please choose a Message to Delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this Message ?");
        confirm.setContentText("Title: " + selected.getTitle());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                notificationDAO.markAsDeletedByReceiver(selected);
                loadInbox();
                messageContentArea.clear();
                tab1DetailDateLabel.setText("");
                AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Delete Successfully", "Message Deleted.");
            }
        });
    }
    
 // Handle Delete Sent Message
    private void handleDeleteSent() {
    	
        Notification selected = sentMessagesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showAlert(Alert.AlertType.WARNING, "No Messages Selected Yet", "Please choose a Message to Delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this Message ?");
        confirm.setContentText("Title: " + selected.getTitle());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                notificationDAO.markAsDeletedBySender(selected);
                loadSentMessages();
                loadInbox();
                messageSendContentArea.clear();
                tab2DetailDateLabel.setText("");
                AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Delete Successfully", "Message Deleted.");
            }
        });
    }

    // "ClearForm" Button in Tab 2 (Send message to Admin)
	private void clearComposeForm() {
	    subjectField.clear();
	    composeMessageArea.clear();
	}

	// "Clear" Button in Tab 1 (Inbox) 
    private void tab1ClearNotificationDetail() {
		tab1DetailDateLabel.setText("Date/Time");
		messageContentArea.clear();
    }
    
    // "Clear" Button in Tab 2 (Send message to Admin)
	private void tab2ClearNotificationDetail() {
		tab2DetailDateLabel.setText("Date/Time");
		messageSendContentArea.clear();
	}
    
}
