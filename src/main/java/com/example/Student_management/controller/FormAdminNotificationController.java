package com.example.Student_management.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.Student_management.dao.NotificationDAO;
import com.example.Student_management.model.Notification;
import com.example.Student_management.socket.AdminClient;
import com.example.Student_management.socket.AdminSocketListener;
import com.example.Student_management.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FormAdminNotificationController {

    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField titleField;
    @FXML private TextArea messageArea;
    
    @FXML private TableView<Notification> sentMessageTable;
    @FXML private TableColumn<Notification, String> sentTitleColumn;
    @FXML private TableColumn<Notification, String> sentRecipientColumn;
    @FXML private TableColumn<Notification, LocalDate> sentDateColumn;
    @FXML private TableColumn<Notification, LocalTime> sentTimeColumn;
    @FXML private Button sendBtn, clearSendNotificationBtn;

    // Tab 2 - Received Messages
    @FXML private TableView<Notification> receivedMessageTable;
    @FXML private TableColumn<Notification, String> receivedTitleColumn;
    @FXML private TableColumn<Notification, String> receivedSenderColumn;
    @FXML private TableColumn<Notification, String> receivedSenderTypeColumn;
    @FXML private TableColumn<Notification, LocalDate> receivedDateColumn;
    @FXML private TableColumn<Notification, LocalTime> receivedTimeColumn;
    
    @FXML private TextArea sentNotiContentArea;
    @FXML private TextArea receivedMessageContentArea;
    
    @FXML private Label tab1DateLabel;
    @FXML private Label receivedDetailTitleLabel;
    
    @FXML private Button tab1DeleteNotiDetailBtn, tab2DeleteReceivedBtn;
    @FXML private Button tab1ClearNotiDetailBtn, tab2ClearBtn;
    
    private NotificationDAO notificationDAO;
    private AdminClient adminClient;

    private final String currentSenderRole = "admin";

    @FXML
    public void initialize() {
    	
        notificationDAO = new NotificationDAO();
        adminClient = new AdminClient();

        // Real-time
        new AdminSocketListener(this).start();
        
        roleComboBox.setItems(FXCollections.observableArrayList("All", "All Teacher", "All Student"));
        
        setupTables();
        loadSentMessages();
        loadInbox();

        sentMessageTable.setOnMouseClicked(e -> tab1ShowMessageDetails());
        receivedMessageTable.setOnMouseClicked(e -> tab2ShowMessageDetails());
        
        sendBtn.setOnAction(e -> handleSendBtn());
        clearSendNotificationBtn.setOnAction(e -> handleClearForm());
        
        tab1DeleteNotiDetailBtn.setOnAction(e -> handleDeleteSent());
        tab2DeleteReceivedBtn.setOnAction(e -> handleDeleteReceived());
        tab1ClearNotiDetailBtn.setOnAction(e -> tab1ClearNotificationDetail());
        tab2ClearBtn.setOnAction(e -> tab2ClearNotificationDetail());
    }
    
    public void refreshInboxFromSocket() {
        javafx.application.Platform.runLater(() -> {
            loadInbox();
        });
    }

    private void setupTables() {
        // Tab 1 - Sent messages table
        sentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        sentRecipientColumn.setCellValueFactory(new PropertyValueFactory<>("receiverRole"));
        sentDateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
        sentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sendTime"));

        // Tab 2 - Received messages table
        receivedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        receivedSenderColumn.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        receivedSenderTypeColumn.setCellValueFactory(new PropertyValueFactory<>("senderRole"));
        receivedDateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
        receivedTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sendTime"));
    }

    private void loadSentMessages() {
        List<Notification> list = notificationDAO.getNotificationsFromSender(currentSenderRole);
        sentMessageTable.setItems(FXCollections.observableArrayList(list));
    }
    
    private void loadInbox() {
        List<Notification> inboxList = notificationDAO.getNotificationsForReceiver(currentSenderRole);
        
        // Lọc bỏ những thông báo mà người gửi cũng là admin
        List<Notification> filtered = inboxList.stream()
            .filter(n -> !n.getSenderRole().equals(currentSenderRole))  // Bỏ thông báo do chính admin gửi
            .toList();
        
        receivedMessageTable.setItems(FXCollections.observableArrayList(filtered));
        //receivedMessageTable.setItems(FXCollections.observableArrayList(inboxList));
    }

    // Tab 1 - Show sent message details
    private void tab1ShowMessageDetails() {
        Notification selected = sentMessageTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tab1DateLabel.setText(selected.getSendDate() + " " + selected.getSendTime());
            sentNotiContentArea.setText(selected.getContent());
        }
    }
    
    // Tab 2 - Show received message details
    private void tab2ShowMessageDetails() {
        Notification selected = receivedMessageTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            receivedDetailTitleLabel.setText(selected.getSendDate() + " " + selected.getSendTime());
            receivedMessageContentArea.setText(selected.getContent());
        }
    }

    @FXML
    private void handleSendBtn() {
        String roleDisplay = roleComboBox.getValue();
        String title = titleField.getText();
        String content = messageArea.getText();

        if (roleDisplay == null || title.isEmpty() || content.isEmpty()) {
            AlertUtil.showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all information.");
            return;
        }

        String mappedRole;
        switch (roleDisplay) {
        case "All":
            mappedRole = "all";
            break;
        case "All Teacher":
            mappedRole = "teacher";
            break;
        case "All Student":
            mappedRole = "student";
            break;
        default:
            mappedRole = "unknown";
            break;
        }

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSenderRole(currentSenderRole);
        notification.setReceiverRole(mappedRole);
        notification.setSendDate(LocalDate.now());
        notification.setSendTime(LocalTime.now());

        // Save Message/Notification in DB
        notificationDAO.saveNotification(notification);
        adminClient.sendNotification(mappedRole, title, content);

        loadSentMessages();
        handleClearForm();
    }
    
    @FXML
    private void handleClearForm() {
        titleField.clear();
        messageArea.clear();
        roleComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleDeleteSent() {
        Notification selectedNotification = sentMessageTable.getSelectionModel().getSelectedItem();

        if (selectedNotification == null) {
            AlertUtil.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a notification to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this notification?");
        confirm.setContentText("Title: " + selectedNotification.getTitle());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                notificationDAO.markAsDeletedBySender(selectedNotification);
                loadSentMessages();
                loadInbox();
                AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Delete Successfully", "Notification deleted successfully.");
            }
        });
    }

    private void handleDeleteReceived() {
        Notification selected = receivedMessageTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a message to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this message?");
        confirm.setContentText("Title: " + selected.getTitle());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                notificationDAO.markAsDeletedByReceiver(selected);
                loadInbox();
                //receivedMessageContentArea.clear();
                receivedDetailTitleLabel.setText("Date/Time");
                AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Delete Successfully", "Message deleted.");
            }
        });
    }

    private void tab1ClearNotificationDetail() {
        tab1DateLabel.setText("Date/Time");
        sentNotiContentArea.clear();
    }
    
    private void tab2ClearNotificationDetail() {
        receivedDetailTitleLabel.setText("Date/Time");
        receivedMessageContentArea.clear();
    }
}