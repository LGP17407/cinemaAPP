package service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import db.DB_CONNECTION;

public class UserServices {
	static Scanner sc = new Scanner(System.in);

	public static void signUp() {								
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
	
	public static boolean signIn() {
		DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
		dbConnect.createConnection();
		String email, password;
		System.out.println("nhập gmail: ");
		email = sc.nextLine();
		System.out.println("nhập mật khẩu: ");
		password = sc.nextLine();
		
		try { 
			PreparedStatement pSt = null;	
			ResultSet rs = null;
			String sql = "select password_hash from users where email = ?;";
			pSt = dbConnect.getConnection().prepareStatement(sql);
			pSt.setString(1, email);
			rs = pSt.executeQuery();
			rs.next();
			// sử lý lỗi khi bảng rỗng
			String passwordDB = rs.getString(1);
			password = PasswordUtil.hash(password);
			if (passwordDB.equals(password)) {
				System.out.println("Đăng nhập thành công");
				return true;
			}
			else {
				System.out.println("Đăng nhập thất bại, sai email hoặc password");
				return false;
			}
				
		}
		catch (SQLException ex) {
			
			System.out.println("SQLException: " + ex.getMessage() + ex.getStackTrace().toString());
			
		}
		return false;
	}
	
	

}
