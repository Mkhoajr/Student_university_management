package com.example.Student_management.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.Student_management.model.Notification;
import com.example.Student_management.util.HibernateUtil;

public class NotificationDAO {

	public void saveNotification(Notification noti) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(noti);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

	// This Function just deletes from TableMessage, not in DB
	// SoftDelete Technical
	public void markAsDeletedBySender(Notification notification) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        Transaction tx = session.beginTransaction();
	        Notification fresh = session.get(Notification.class, notification.getId());
	        if (fresh != null) {
	            fresh.setDeletedBySender(true);
	            session.update(fresh);
	        }
	        tx.commit();
	    }
	}

	// This Function just deletes from TableMessage, not in DB
	public void markAsDeletedByReceiver(Notification notification) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        Transaction tx = session.beginTransaction();
	        Notification fresh = session.get(Notification.class, notification.getId());
	        if (fresh != null) {
	            fresh.setDeletedByReceiver(true);
	            session.update(fresh);
	        }
	        tx.commit();
	    }
	}

    // Lấy tất cả tin nhắn mà người dùng nhận được (ví dụ teacher hoặc student nhận từ admin)
	public List<Notification> getNotificationsForReceiver(String receiverRole) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        return session.createQuery(
	            "FROM Notification WHERE (receiverRole = :role OR receiverRole = 'all') " +
	            "AND isDeletedByReceiver = false ORDER BY id DESC", Notification.class)
	            .setParameter("role", receiverRole)
	            .list();
	    }
	}
	
	// Lấy tất cả tin nhắn mà người dùng đã gửi (ví dụ teacher hoặc student gửi đến admin)
	public List<Notification> getNotificationsFromSender(String senderRole) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        return session.createQuery(
	            "FROM Notification WHERE senderRole = :role AND isDeletedBySender = false ORDER BY id DESC",
	            Notification.class)
	            .setParameter("role", senderRole)
	            .list();
	    }
	}
	
    public List<Notification> getAllNotifications() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Notification ORDER BY id DESC", Notification.class).list();
        }
    }
}
