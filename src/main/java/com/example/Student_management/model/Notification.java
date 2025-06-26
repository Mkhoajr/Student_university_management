package com.example.Student_management.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "sender_role")
    private String senderRole;
    
    @Column(name = "sender_name")
    private String senderName;

	@Column(name = "receiver_role")
    private String receiverRole;

    @Column(name = "send_date")
    private LocalDate sendDate;

    @Column(name = "send_time")
    private LocalTime sendTime;
    
    @Column(name = "is_deleted_by_sender")
    private boolean isDeletedBySender;

    @Column(name = "is_deleted_by_receiver")
    private boolean isDeletedByReceiver;

	public boolean isDeletedBySender() {
		return isDeletedBySender;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSenderRole() {
		return senderRole;
	}

	public void setSenderRole(String senderRole) {
		this.senderRole = senderRole;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public String getReceiverRole() {
		return receiverRole;
	}

	public void setReceiverRole(String receiverRole) {
		this.receiverRole = receiverRole;
	}

	public LocalDate getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDate sendDate) {
		this.sendDate = sendDate;
	}

	public LocalTime getSendTime() {
		return sendTime;
	}

	public void setSendTime(LocalTime sendTime) {
		this.sendTime = sendTime;
	}
	
	public void setDeletedBySender(boolean isDeletedBySender) {
		this.isDeletedBySender = isDeletedBySender;
	}

	public boolean isDeletedByReceiver() {
		return isDeletedByReceiver;
	}

	public void setDeletedByReceiver(boolean isDeletedByReceiver) {
		this.isDeletedByReceiver = isDeletedByReceiver;
	}
    
}

