package com.example.Student_management.dao;

import java.util.List;

import org.hibernate.Session;

import com.example.Student_management.model.Subject;
import com.example.Student_management.util.HibernateUtil;

public class SubjectDAO {

	public static List<Subject> getAllSubjects() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Subject", Subject.class).list();
        }
    }
	
	// Phương thức lấy thông tin sinh viên theo studentId
    public static Subject getSubjectById(String subjectId) {
    	
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.get(Subject.class, subjectId);  // Lấy thông tin sinh viên từ DB theo studentId
        } finally {
            session.close();
        }
    }
}
