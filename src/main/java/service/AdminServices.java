package service;

import db.DB_CONNECTION;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import model.*;
import java.util.ArrayList;
import java.util.Scanner;


public class AdminServices {
	private Connection conn;
	static Scanner sc = new Scanner(System.in);

	public AdminServices () {
		conn = DB_CONNECTION.getInstance().createConnection();
	}

	//1. lấy thông tin user
	public ArrayList<ModelUser> getUserList () throws SQLException {
		ArrayList<ModelUser> userList = new ArrayList();
		String sql = "SELECT userid, fullname, email, password, role, DATE_FORMAT(createdat, '%d-%m-%Y') as ngay_tao from users";
		PreparedStatement pSt = null;
		ResultSet rs = null;

		pSt = conn.prepareStatement(sql);
		rs = pSt.executeQuery();

		while (rs.next()) {
			int userId = rs.getInt(1);
			String fullName = rs.getString(2);
			String email = rs.getString(3);
			String password = rs.getString(4);
			String role = rs.getString(5);
			String createdAt = rs.getString(6);
			ModelUser data = new ModelUser(userId, fullName, email, password, role, createdAt);
			userList.add(data);
		}

		return userList;
	}

	public static void addMovies() {		//Admin có thể thêm phim
		DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
		dbConnect.createConnection();
		String title, description, status;
		int duration;

		while (true) {
			System.out.println("mời nhập title: ");
			title = sc.nextLine().trim();
			if (!title.isEmpty())
				break;
			System.out.println("title không được để trống.");
		}
		System.out.println("mời nhập description: ");
		description = sc.nextLine();

		while (true) {
			System.out.println("mời nhập status(UPCOMING, SHOWING, STOPPED): ");
			status = sc.nextLine().trim().toUpperCase();
			if (status.equals("UPCOMING") || status.equals("SHOWING") || status.equals("STOPPED"))
				break;
			System.out.println("status không hợp lệ, vui lòng nhập lại.");
		}


		while (true) {
			System.out.println("mời nhập thời lượng phim(phút): ");
			try {
				duration = Integer.parseInt(sc.nextLine().trim());
				if (duration > 0)
					break;
				System.out.println("thời lượng phải lớn hơn 0.");
			} catch (NumberFormatException e) {
				System.out.println("vui lòng nhập một số nguyên.");
			}
		}

		String sql = "insert into movies(title, description, duration_mins, status) values(?, ?, ?, ?);";
		try (PreparedStatement pSt = dbConnect.getConnection().prepareStatement(sql)) {
			pSt.setString(1, title);
			pSt.setString(2, description);
			pSt.setInt(3, duration);
			pSt.setString(4, status);
			pSt.executeUpdate();	      
			System.out.println("đã thêm thành công.");
		} catch (SQLException ex) {
			System.out.println("Lỗi CSDL: " + ex.getMessage());
			ex.printStackTrace();
		}

	}







}
