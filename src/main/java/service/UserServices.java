package service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import db.DB_CONNECTION;

public class UserServices {

	public static void signUp() {
		DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
		dbConnect.createConnection();
		String fullName, email, password, passwordCheck;
		Scanner sc = new Scanner(System.in);
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

}
