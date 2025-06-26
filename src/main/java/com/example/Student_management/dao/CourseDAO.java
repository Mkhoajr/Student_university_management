package com.example.Student_management.dao;

import java.util.List;

import org.hibernate.Session;

import com.example.Student_management.model.Course;
import com.example.Student_management.util.HibernateUtil;

public class CourseDAO {

		public static List<Course> getAllCourses() {
	        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	            return session.createQuery("FROM Course", Course.class).list();
	        }
	    }
		
		// Phương thức lấy thông tin sinh viên theo studentId
	    public static Course getCourseById(String courseId) {
	    	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        try {
	            return session.get(Course.class, courseId);  // Lấy thông tin sinh viên từ DB theo studentId
	        } finally {
	            session.close();
	        }
	}
}

