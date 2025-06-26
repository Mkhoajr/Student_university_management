package com.example.Student_management.dao;

import java.util.List;

import org.hibernate.Session;

import com.example.Student_management.model.Teacher;
import com.example.Student_management.util.HibernateUtil;

public class TeacherDAO {

	    public static void saveTeacher(Teacher teacher) {
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        try {
	            session.beginTransaction();
	            session.persist(teacher); 
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
	    
	    public static List<Teacher> getAllTeachers() {
	    	try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	            return session.createQuery("FROM Teacher", Teacher.class).list();
	        }
	    }
	    
	    public static Teacher getTeacherById(String teacherId) {
	    	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        try {
	            return session.get(Teacher.class, teacherId);  
	        } finally {
	            session.close();
	        }
	    }
	    
	    
	}

