package com.example.Student_management.dao;

import java.util.List;

import org.hibernate.Session;

import com.example.Student_management.util.HibernateUtil;
import com.example.Student_management.model.Department;


public class DepartmentDAO {
	
	 public static List<Department> getAllDepartments() {
	        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	            return session.createQuery("FROM Department", Department.class).list();
	        }
	    }
}
