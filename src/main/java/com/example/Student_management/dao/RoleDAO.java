package com.example.Student_management.dao;

import org.hibernate.Session;

import com.example.Student_management.model.Role;
import com.example.Student_management.util.HibernateUtil;

public class RoleDAO {

	public static Role getRoleByName(String roleName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.createQuery("FROM Role WHERE roleName = :name", Role.class)
                          .setParameter("name", roleName)
                          .uniqueResult();
        } finally {
            session.close();
        }
    }
}
