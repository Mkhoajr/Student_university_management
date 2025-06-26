package com.example.Student_management.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.example.Student_management.model.User;

// Import class HibernateUtil và PasswordUtil bên Util package
import com.example.Student_management.util.HibernateUtil;
import com.example.Student_management.util.PasswordUtil;

public class UserDAO {
	
	 public static User login(String username, String password, String roleName) {
		 
	        // Hash password đầu vào qua class PasswordUtil
	        //String hashedPassword = PasswordUtil.hashPassword(password);

	        // Mở Session từ Hibernate qua class HibernateUtil để giao tiếp với DB
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        try {
	            
	        	// Truy vấn User từ cơ sở dữ liệu
	            Query<User> query = session.createQuery(
	            	"FROM User WHERE userName = :userName AND role.roleName = :roleName", User.class);
	            query.setParameter("userName", username);
	            query.setParameter("roleName", roleName);
	            
	            // Lấy user duy nhất và đầu tiên tìm thấy
	            User user = query.uniqueResult();
	            
	            if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {
	                return user; // Trả về user nếu mật khẩu đúng
	            } else {
	                return null; // Nếu không tìm thấy hoặc mật khẩu sai
	            }
	        } finally {
	            session.close();
	        }
	    }

	public static User findByEmail(String email) {
		    Session session = HibernateUtil.getSessionFactory().openSession();
		    try {
		        Query<User> query = session.createQuery(
		            "FROM User WHERE email = :email", User.class);
		        query.setParameter("email", email);
		        return query.uniqueResult();
		    } finally {
		        session.close();
		    }
		}

	 
	 	// Phương thức lưu thông tin user (bao gồm sinh viên, giảng viên, admin)
	    public static void saveUser(User user) {
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        try {
	            session.beginTransaction();
	            session.persist(user);  // = SAVE, Lưu hoặc cập nhật thông tin user
	            session.getTransaction().commit(); // Commit giao dịch
	        } catch (Exception e) {
	            if (session.getTransaction() != null) {
	                session.getTransaction().rollback(); // Rollback nếu có lỗi
	            }
	            e.printStackTrace();
	        } finally {
	            session.close();
	        }
	    }
	    
	    // Phương thức Update thông tin User khi đã thay đổi MK mới = OTP
	    public static void update(User user) {
	    	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        try {
	            session.beginTransaction();
	            session.merge(user); // Cập nhật thông tin user
	            session.flush();
	            session.getTransaction().commit();
	        } catch (Exception e) {
	            if (session.getTransaction() != null) session.getTransaction().rollback();
	            e.printStackTrace();
	        } finally {
	            session.close();
	        }
	    }


	    // Phương thức lấy thông tin user theo userId
	    public static User getUserById(String userId) {
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        try {
	            return session.get(User.class, userId);  // Lấy thông tin user từ DB theo userId
	        } finally {
	            session.close();
	        }
	    }
	  
	    
	    // Thêm biến static để lưu User hiện tại
	    private static User currentUser;

	    // Gán user hiện tại sau khi đăng nhập
	    public static void setCurrentUser(User user) {
	        currentUser = user;
	    }

	    // Lấy user hiện tại ở bất kỳ nơi nào khác
	    public static User getCurrentUser() {
	        return currentUser;
	    }
	    
}
