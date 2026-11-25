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
	/*public ArrayList<ModelUser> getUserList () throws SQLException {
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
	}*/

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

	public static boolean deleteMovie(int movieId) {      // admin có thể xóa phim (xóa mềm)
		DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();

		String sql = "update movies set status = 'STOPPED' where movie_id = ? and status <> 'STOPPED'";
		try (PreparedStatement pSt = dbConnect.getConnection().prepareStatement(sql)) {
			pSt.setInt(1, movieId);
			int rows = pSt.executeUpdate();
			if (rows == 0) {
				System.out.println("Không tìm thấy phim (hoặc phim đã ở trạng thái STOPPED).");
				return false;
			}
			System.out.println("Đã xóa phim.");
			return true;
		} catch (SQLException ex) {
			System.out.println("Lỗi CSDL: " + ex.getMessage());
			return false;
		}
	}

	public static boolean alterMoviesTitle() {			// chỉnh sửa title phim
		Connection conn = DB_CONNECTION.getInstance().createConnection();

		int idFilm;
		while (true) {
			System.out.println("mời nhập id phim: ");
			try {
				idFilm = Integer.parseInt(sc.nextLine().trim());
				break;
			} catch (NumberFormatException e) {
				System.out.println("id phải là một số nguyên.");
			}
		}

		String newTitle;
		while (true) {
			System.out.println("mời nhập title phim mới: ");
			newTitle = sc.nextLine().trim();
			if (!newTitle.isEmpty())
				break;
			System.out.println("title mới không được để trống.");
		}

		String sql = "update movies set title = ? where movie_id = ?";
		try (PreparedStatement pSt = conn.prepareStatement(sql)) {
			pSt.setString(1, newTitle);
			pSt.setInt(2, idFilm);
			int rows = pSt.executeUpdate();
			if (rows == 0) {
				System.out.println("Không tìm thấy phim có id " + idFilm + " (hoặc phim đã bị xóa).");
				return false;
			}
			System.out.println("Đã đổi title thành công.");
			return true;
		} catch (SQLException ex) {
			System.out.println("Lỗi CSDL: " + ex.getMessage());
			return false;
		}
	}

	public static boolean alterMoviesDescription() {			// chỉnh sửa description phim 
		Connection conn = DB_CONNECTION.getInstance().createConnection();

		int idFilm;
		while (true) {
			System.out.println("mời nhập id phim: ");
			try {
				idFilm = Integer.parseInt(sc.nextLine().trim());
				break;
			} catch (NumberFormatException e) {
				System.out.println("id phải là một số nguyên.");
			}
		}

		String newDescription;
		while (true) {
			System.out.println("mời nhập description phim mới: ");
			newDescription = sc.nextLine().trim();
			if (!newDescription.isEmpty())
				break;
			System.out.println("Description mới không được để trống.");
		}

		String sql = "update movies set description = ? where movie_id = ?";
		try (PreparedStatement pSt = conn.prepareStatement(sql)) {
			pSt.setString(1, newDescription);
			pSt.setInt(2, idFilm);
			int rows = pSt.executeUpdate();
			if (rows == 0) {
				System.out.println("Không tìm thấy phim có id " + idFilm + " (hoặc phim đã bị xóa).");
				return false;
			}
			System.out.println("Đã đổi description thành công.");
			return true;
		} catch (SQLException ex) {
			System.out.println("Lỗi CSDL: " + ex.getMessage());
			return false;
		}
	}

	public static boolean alterMoviesPosterUrl() {			// chỉnh sửa PosterUrl phim 
		Connection conn = DB_CONNECTION.getInstance().createConnection();

		int idFilm;
		while (true) {
			System.out.println("mời nhập id phim: ");
			try {
				idFilm = Integer.parseInt(sc.nextLine().trim());
				break;
			} catch (NumberFormatException e) {
				System.out.println("id phải là một số nguyên.");
			}
		}

		String newPosterUrl;  
		System.out.println("mời nhập posterUrl phim mới: ");
		newPosterUrl = sc.nextLine().trim();


		String sql = "update movies set poster_url = ? where movie_id = ?";
		try (PreparedStatement pSt = conn.prepareStatement(sql)) {
			pSt.setString(1, newPosterUrl);
			pSt.setInt(2, idFilm);
			int rows = pSt.executeUpdate();
			if (rows == 0) {
				System.out.println("Không tìm thấy phim có id " + idFilm + " (hoặc phim đã bị xóa).");
				return false;
			}
			System.out.println("Đã đổi posterUrl thành công.");
			return true;
		} catch (SQLException ex) {
			System.out.println("Lỗi CSDL: " + ex.getMessage());
			return false;
		}
	}

	public static boolean alterMoviesDuration() {			// chỉnh sửa duration phim
		Connection conn = DB_CONNECTION.getInstance().createConnection();

		int idFilm;
		while (true) {
			System.out.println("mời nhập id phim: ");
			try {
				idFilm = Integer.parseInt(sc.nextLine().trim());
				break;
			} catch (NumberFormatException e) {
				System.out.println("id phải là một số nguyên.");
			}
		}

		int newDuration;
		while (true) {
			System.out.println("mời nhập thời lượng phim(phút): ");
			try {
				newDuration = Integer.parseInt(sc.nextLine().trim());
				if (newDuration > 0)
					break;
				System.out.println("thời lượng phải lớn hơn 0.");
			} catch (NumberFormatException e) {
				System.out.println("vui lòng nhập một số nguyên.");
			}
		}

		String sql = "update movies set duration_mins = ? where movie_id = ?";
		try (PreparedStatement pSt = conn.prepareStatement(sql)) {
			pSt.setInt(1, newDuration);
			pSt.setInt(2, idFilm);
			int rows = pSt.executeUpdate();
			if (rows == 0) {
				System.out.println("Không tìm thấy phim có id " + idFilm + " (hoặc phim đã bị xóa).");
				return false;
			}
			System.out.println("Đã đổi duration mới thành công.");
			return true;
		} catch (SQLException ex) {
			System.out.println("Lỗi CSDL: " + ex.getMessage());
			return false;
		}
	}

	public static boolean alterMoviesStatus() {			// chỉnh sửa status phim
		Connection conn = DB_CONNECTION.getInstance().createConnection();

		int idFilm;
		while (true) {
			System.out.println("mời nhập id phim: ");
			try {
				idFilm = Integer.parseInt(sc.nextLine().trim());
				break;
			} catch (NumberFormatException e) {
				System.out.println("id phải là một số nguyên.");
			}
		}

		String newStatus;
		while (true) {
			System.out.println("mời nhập status(UPCOMING, SHOWING, STOPPED): ");
			newStatus = sc.nextLine().trim().toUpperCase();
			if (newStatus.equals("UPCOMING") || newStatus.equals("SHOWING") || newStatus.equals("STOPPED"))
				break;
			System.out.println("status không hợp lệ, vui lòng nhập lại.");
		}

		String sql = "update movies set status = ? where movie_id = ?";
		try (PreparedStatement pSt = conn.prepareStatement(sql)) {
			pSt.setString(1, newStatus);
			pSt.setInt(2, idFilm);
			int rows = pSt.executeUpdate();
			if (rows == 0) {
				System.out.println("Không tìm thấy phim có id " + idFilm + " (hoặc phim đã bị xóa).");
				return false;
			}
			System.out.println("Đã đổi status mới thành công.");
			return true;
		} catch (SQLException ex) {
			System.out.println("Lỗi CSDL: " + ex.getMessage());
			return false;
		}
	}




}
