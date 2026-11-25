package service;

import db.DB_CONNECTION;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import model.Record;


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

	// =========================================================================
	// Các phương thức phục vụ giao diện Admin (dùng model.Record)
	// =========================================================================

	private static Connection requireConnection() throws SQLException {
		Connection connection = DB_CONNECTION.getInstance().createConnection();
		if (connection == null) {
			throw new SQLException("Không thể kết nối cơ sở dữ liệu.");
		}
		return connection;
	}

	private static void validateMovieInput(String title, int durationMins, String status) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Tên phim không được để trống.");
		}
		if (durationMins <= 0) {
			throw new IllegalArgumentException("Thời lượng phim phải lớn hơn 0 phút.");
		}
		if (!"UPCOMING".equals(status) && !"SHOWING".equals(status) && !"STOPPED".equals(status)) {
			throw new IllegalArgumentException("Trạng thái phim phải là UPCOMING, SHOWING hoặc STOPPED.");
		}
	}

	public static List<Record.movies> getAllMovies() throws SQLException {
		Connection connection = requireConnection();
		String sql = "SELECT movie_id, title, description, duration_mins, poster_url, status, created_at "
				+ "FROM movies ORDER BY movie_id DESC";
		List<Record.movies> movies = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Timestamp ts = rs.getTimestamp("created_at");
				String status = rs.getString("status");
				movies.add(new Record.movies(
						rs.getInt("movie_id"),
						rs.getString("title"),
						rs.getString("description"),
						rs.getInt("duration_mins"),
						rs.getString("poster_url"),
						status,
						ts != null ? ts.toLocalDateTime() : null));
			}
		}
		return movies;
	}

	public static void addMovie(String title, String description, int durationMins,
								String posterUrl, String status) throws SQLException {
		validateMovieInput(title, durationMins, status);
		Connection connection = requireConnection();
		String sql = "INSERT INTO movies (title, description, duration_mins, poster_url, status) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, title.trim());
			ps.setString(2, description == null ? "" : description.trim());
			ps.setInt(3, durationMins);
			ps.setString(4, posterUrl == null || posterUrl.isBlank() ? null : posterUrl.trim());
			ps.setString(5, status);
			ps.executeUpdate();
		}
	}

	public static void updateMovie(int movieId, String title, String description, int durationMins,
								   String posterUrl, String status) throws SQLException {
		if (movieId <= 0) {
			throw new IllegalArgumentException("Phim không hợp lệ.");
		}
		validateMovieInput(title, durationMins, status);
		Connection connection = requireConnection();
		String sql = "UPDATE movies SET title = ?, description = ?, duration_mins = ?, poster_url = ?, status = ? "
				+ "WHERE movie_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, title.trim());
			ps.setString(2, description == null ? "" : description.trim());
			ps.setInt(3, durationMins);
			ps.setString(4, posterUrl == null || posterUrl.isBlank() ? null : posterUrl.trim());
			ps.setString(5, status);
			ps.setInt(6, movieId);
			if (ps.executeUpdate() == 0) {
				throw new SQLException("Không tìm thấy phim cần cập nhật.");
			}
		}
	}

	public static void stopMovie(int movieId) throws SQLException {
		if (movieId <= 0) {
			throw new IllegalArgumentException("Phim không hợp lệ.");
		}
		Connection connection = requireConnection();
		String sql = "UPDATE movies SET status = 'STOPPED' WHERE movie_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, movieId);
			if (ps.executeUpdate() == 0) {
				throw new SQLException("Không tìm thấy phim.");
			}
		}
	}

	public static List<Record.showTimes> getAllShowtimes() throws SQLException {
		Connection connection = requireConnection();
		String sql = "SELECT showtime_id, movie_id, room_id, start_time, end_time, base_price, "
				+ "status, is_deleted, created_at "
				+ "FROM showtimes WHERE is_deleted = 0 "
				+ "ORDER BY start_time DESC, showtime_id DESC";
		List<Record.showTimes> showtimes = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Timestamp createdTs = rs.getTimestamp("created_at");
				showtimes.add(new Record.showTimes(
						rs.getInt("showtime_id"),
						rs.getInt("movie_id"),
						rs.getInt("room_id"),
						rs.getTimestamp("start_time").toLocalDateTime(),
						rs.getTimestamp("end_time").toLocalDateTime(),
						rs.getDouble("base_price"),
						rs.getString("status"),
						rs.getBoolean("is_deleted"),
						createdTs != null ? createdTs.toLocalDateTime() : null));
			}
		}
		return showtimes;
	}

	public static Map<Integer, Integer> getBookedTicketCountsByShowtime() throws SQLException {
		Connection connection = requireConnection();
		String sql = "SELECT showtime_id, COUNT(*) AS booked_count FROM tickets GROUP BY showtime_id";
		Map<Integer, Integer> counts = new HashMap<>();
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				counts.put(rs.getInt("showtime_id"), rs.getInt("booked_count"));
			}
		}
		return counts;
	}

	private static int getMovieDuration(Connection connection, int movieId) throws SQLException {
		String sql = "SELECT duration_mins FROM movies WHERE movie_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, movieId);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					throw new IllegalArgumentException("Không tìm thấy phim đã chọn.");
				}
				return rs.getInt("duration_mins");
			}
		}
	}

	private static void ensureNoRoomOverlap(Connection connection, int roomId,
											LocalDateTime startTime, LocalDateTime endTime,
											Integer excludeShowtimeId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM showtimes "
				+ "WHERE room_id = ? AND is_deleted = 0 AND status = 'ACTIVE' "
				+ "AND start_time < ? AND end_time > ?"
				+ (excludeShowtimeId != null ? " AND showtime_id <> ?" : "");
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, roomId);
			ps.setTimestamp(2, Timestamp.valueOf(endTime));
			ps.setTimestamp(3, Timestamp.valueOf(startTime));
			if (excludeShowtimeId != null) {
				ps.setInt(4, excludeShowtimeId);
			}
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					throw new IllegalArgumentException(
							"Phòng chiếu đã có suất chiếu khác trùng khung giờ này.");
				}
			}
		}
	}

	public static void addShowtime(int movieId, int roomId, LocalDateTime startTime,
								   double basePrice) throws SQLException {
		if (movieId <= 0 || roomId <= 0) {
			throw new IllegalArgumentException("Vui lòng chọn phim và phòng chiếu.");
		}
		if (startTime == null) {
			throw new IllegalArgumentException("Vui lòng nhập giờ bắt đầu hợp lệ.");
		}
		if (basePrice < 0) {
			throw new IllegalArgumentException("Giá vé cơ bản không được âm.");
		}

		Connection connection = requireConnection();
		int durationMins = getMovieDuration(connection, movieId);
		LocalDateTime endTime = startTime.plusMinutes(durationMins);
		ensureNoRoomOverlap(connection, roomId, startTime, endTime, null);

		String sql = "INSERT INTO showtimes (movie_id, room_id, start_time, end_time, base_price, status) "
				+ "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, movieId);
			ps.setInt(2, roomId);
			ps.setTimestamp(3, Timestamp.valueOf(startTime));
			ps.setTimestamp(4, Timestamp.valueOf(endTime));
			ps.setDouble(5, basePrice);
			ps.executeUpdate();
		}
	}

	public static void updateShowtime(int showtimeId, int movieId, int roomId,
									  LocalDateTime startTime, double basePrice) throws SQLException {
		if (showtimeId <= 0 || movieId <= 0 || roomId <= 0) {
			throw new IllegalArgumentException("Thông tin suất chiếu không hợp lệ.");
		}
		if (startTime == null) {
			throw new IllegalArgumentException("Vui lòng nhập giờ bắt đầu hợp lệ.");
		}
		if (basePrice < 0) {
			throw new IllegalArgumentException("Giá vé cơ bản không được âm.");
		}

		Connection connection = requireConnection();

		// Chặn sửa nếu suất chiếu đã hủy hoặc đã có khách đặt vé
		String checkSql = "SELECT status, "
				+ "(SELECT COUNT(*) FROM tickets t WHERE t.showtime_id = st.showtime_id) AS booked "
				+ "FROM showtimes st WHERE st.showtime_id = ? AND st.is_deleted = 0";
		try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
			ps.setInt(1, showtimeId);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					throw new SQLException("Không tìm thấy suất chiếu.");
				}
				if ("CANCELLED".equals(rs.getString("status"))) {
					throw new IllegalStateException("Suất chiếu đã hủy, không thể chỉnh sửa.");
				}
				int booked = rs.getInt("booked");
				if (booked > 0) {
					throw new IllegalStateException(
							"Suất chiếu đã có " + booked + " vé được đặt, không thể sửa để tránh làm lệch vé của khách.");
				}
			}
		}

		int durationMins = getMovieDuration(connection, movieId);
		LocalDateTime endTime = startTime.plusMinutes(durationMins);
		ensureNoRoomOverlap(connection, roomId, startTime, endTime, showtimeId);

		String updateSql = "UPDATE showtimes SET movie_id = ?, room_id = ?, start_time = ?, end_time = ?, base_price = ? "
				+ "WHERE showtime_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
			ps.setInt(1, movieId);
			ps.setInt(2, roomId);
			ps.setTimestamp(3, Timestamp.valueOf(startTime));
			ps.setTimestamp(4, Timestamp.valueOf(endTime));
			ps.setDouble(5, basePrice);
			ps.setInt(6, showtimeId);
			ps.executeUpdate();
		}
	}

	public static void cancelShowtime(int showtimeId) throws SQLException {
		if (showtimeId <= 0) {
			throw new IllegalArgumentException("Suất chiếu không hợp lệ.");
		}
		Connection connection = requireConnection();
		String sql = "UPDATE showtimes SET status = 'CANCELLED' WHERE showtime_id = ? AND is_deleted = 0";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, showtimeId);
			if (ps.executeUpdate() == 0) {
				throw new SQLException("Không tìm thấy suất chiếu.");
			}
		}
	}

	public static List<Record.rooms> getAllRooms() throws SQLException {
		Connection connection = requireConnection();
		String sql = "SELECT room_id, room_name, total_seats, created_at FROM rooms ORDER BY room_id";
		List<Record.rooms> rooms = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Timestamp ts = rs.getTimestamp("created_at");
				rooms.add(new Record.rooms(
						rs.getInt("room_id"),
						rs.getString("room_name"),
						rs.getInt("total_seats"),
						ts != null ? ts.toLocalDateTime() : null));
			}
		}
		return rooms;
	}

	public static Map<Integer, int[]> getSeatStatusCountsByRoom() throws SQLException {
		Connection connection = requireConnection();
		String sql = "SELECT room_id, "
				+ "SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) AS avail_cnt, "
				+ "SUM(CASE WHEN status = 'MAINTENANCE' THEN 1 ELSE 0 END) AS maint_cnt, "
				+ "SUM(CASE WHEN status = 'UNAVAILABLE' THEN 1 ELSE 0 END) AS unavail_cnt "
				+ "FROM seats GROUP BY room_id";
		Map<Integer, int[]> counts = new HashMap<>();
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				counts.put(rs.getInt("room_id"), new int[] {
						rs.getInt("avail_cnt"),
						rs.getInt("maint_cnt"),
						rs.getInt("unavail_cnt")
				});
			}
		}
		return counts;
	}

	public static List<Record.seats> getSeatsByRoom(int roomId) throws SQLException {
		Connection connection = requireConnection();
		String sql = "SELECT seat_id, room_id, seat_row, seat_number, seat_type, status "
				+ "FROM seats WHERE room_id = ? ORDER BY seat_row, seat_number";
		List<Record.seats> seats = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, roomId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					seats.add(new Record.seats(
							rs.getInt("seat_id"),
							rs.getInt("room_id"),
							rs.getString("seat_row"),
							rs.getInt("seat_number"),
							rs.getString("seat_type"),
							rs.getString("status")));
				}
			}
		}
		return seats;
	}

	public static void updateSeatStatus(int seatId, String status) throws SQLException {
		if (seatId <= 0) {
			throw new IllegalArgumentException("Ghế không hợp lệ.");
		}
		if (!"AVAILABLE".equals(status) && !"MAINTENANCE".equals(status) && !"UNAVAILABLE".equals(status)) {
			throw new IllegalArgumentException("Trạng thái ghế không hợp lệ.");
		}
		Connection connection = requireConnection();
		String sql = "UPDATE seats SET status = ? WHERE seat_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setInt(2, seatId);
			if (ps.executeUpdate() == 0) {
				throw new SQLException("Không tìm thấy ghế cần cập nhật.");
			}
		}
	}

}
