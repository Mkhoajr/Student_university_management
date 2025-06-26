package com.example.Student_management.dao;

import org.hibernate.Session;

import com.example.Student_management.model.Student;
import com.example.Student_management.util.HibernateUtil;

public class StudentDAO {

	// Phương thức lưu thông tin sinh viên (có thể là mở rộng User)
    public static void saveStudent(Student student) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            session.persist(student);  // Lưu hoặc cập nhật thông tin sinh viên
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // Phương thức lấy thông tin sinh viên theo studentId
    public static Student getStudentById(String studentId) {
    	
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.get(Student.class, studentId);  // Lấy thông tin sinh viên từ DB theo studentId
        } finally {
            session.close();
        }
    }
}
