package com.example.Student_management.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	 private static final SessionFactory sessionFactory;

	    static {
	        try {
	            sessionFactory = new Configuration().configure().buildSessionFactory(); // đọc file hibernate.cfg.xml
	        } catch (Throwable e) {
	            System.err.println("Lỗi khởi tạo Hibernate: " + e);
	            throw new ExceptionInInitializerError(e);
	        }
	    }

	    public static SessionFactory getSessionFactory() {
	        return sessionFactory;
	    }
	    
}
