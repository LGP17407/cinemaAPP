package service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import db.DB_CONNECTION;

public class UserServices {
	static Scanner sc = new Scanner(System.in);
	public static final String LOGIN_FAILED = "failed";
	public static final String LOGIN_ERROR = "error";
	public static final String SIGNUP_OK = "ok";
	public static final String SIGNUP_EMAIL_EXISTS = "email_exists";
	public static final String SIGNUP_ERROR = "error";
	
	
	public static void signUp() {												//đăng ký ko đối số
		DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
		dbConnect.createConnection();
		String fullName, email, password, passwordCheck;
		System.out.println("nhập họ và tên: ");
		fullName = sc.nextLine();
		System.out.println("nhập email: ");
		email = sc.nextLine();
		System.out.println("nhập mật khẩu: ");
		password = sc.nextLine();
		System.out.println("nhập lại mật khẩu: ");
		passwordCheck = sc.nextLine();
		while (!password.equals(passwordCheck)) {
			System.out.println("mật khẩu nhập lại chưa đúng");
			System.out.println("mời nhập lại: ");
			passwordCheck = sc.nextLine();
		}

		password = PasswordUtil.hash(password);

		try {
			PreparedStatement pSt = null;		
			String sql = "insert into users(full_name, email, password_hash) values (?, ?, ? );";
			pSt = dbConnect.getConnection().prepareStatement(sql);
			pSt.setString(1, fullName);
			pSt.setString(2, email);
			pSt.setString(3, password);
			pSt.executeUpdate();
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}
	
	public static String signUp(String fullName, String email, String password) { 		//đăng ký có đối số
	    DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
	    if (dbConnect.createConnection() == null) {
	        return SIGNUP_ERROR;
	    }

	    String sql = "insert into users(full_name, email, password_hash) values (?, ?, ?)";
	    try (PreparedStatement pSt = dbConnect.getConnection().prepareStatement(sql)) {
	        pSt.setString(1, fullName);
	        pSt.setString(2, email);
	        pSt.setString(3, PasswordUtil.hash(password));
	        pSt.executeUpdate();
	        return SIGNUP_OK;
	    } catch (SQLException ex) {
	        if (ex.getErrorCode() == 1062) {      // 1062 = trùng giá trị UNIQUE (email)
	            return SIGNUP_EMAIL_EXISTS;
	        }
	        System.out.println("SQLException: " + ex.getMessage());
	        return SIGNUP_ERROR;
	    }
	}
	
	public static String signIn() {			// signIn khi đăng nhập thành công thì return role của user, không thành công return "failed"
	    DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
	    dbConnect.createConnection();

	    System.out.println("nhập gmail: ");
	    String email = sc.nextLine();
	    System.out.println("nhập mật khẩu: ");
	    String password = sc.nextLine();

	    String sql = "select password_hash, role from users where email = ?;";

	    try (PreparedStatement pSt = dbConnect.getConnection().prepareStatement(sql)) {
	        pSt.setString(1, email);

	        try (ResultSet rs = pSt.executeQuery()) {
	            if (!rs.next()) {
	                // email không tồn tại
	                System.out.println("Đăng nhập thất bại, sai email hoặc password");
	                return LOGIN_FAILED;
	            }

	            String storedHash = rs.getString("password_hash");
	            String role = rs.getString("role");

	            if (PasswordUtil.verify(password, storedHash)) {
	                System.out.println("Đăng nhập thành công");
	                return role;
	            } else {
	                System.out.println("Đăng nhập thất bại, sai email hoặc password");
	                return LOGIN_FAILED;
	            }
	        }
	    } catch (SQLException ex) {
	        System.out.println("Lỗi CSDL: " + ex.getMessage());
	        ex.printStackTrace();
	    }
	    return "failed";
	}
	
	public static String signIn(String email, String password) {			// signIn khi đăng nhập thành công thì return role của user, không thành công return "failed"
	    DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
	    dbConnect.createConnection();
	    String sql = "select password_hash, role from users where email = ?;";

	    try (PreparedStatement pSt = dbConnect.getConnection().prepareStatement(sql)) {
	        pSt.setString(1, email);

	        try (ResultSet rs = pSt.executeQuery()) {
	            if (!rs.next()) {
	                // email không tồn tại
	                System.out.println("Đăng nhập thất bại, sai email hoặc password");
	                return LOGIN_FAILED;
	            }

	            String storedHash = rs.getString("password_hash");
	            String role = rs.getString("role");

	            if (PasswordUtil.verify(password, storedHash)) {
	                System.out.println("Đăng nhập thành công");
	                return role;
	            } else {
	                System.out.println("Đăng nhập thất bại, sai email hoặc password");
	                return LOGIN_FAILED;
	            }
	        }
	    } catch (SQLException ex) {
	        System.out.println("Lỗi CSDL: " + ex.getMessage());
	        ex.printStackTrace();
	    }
	    return "failed";
	}
	
	

}
