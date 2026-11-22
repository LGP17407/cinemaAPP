package service;

import db.DB_CONNECTION;

import model.ModelTickets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import java.util.Scanner;
import java.util.Arrays;

public class BookingService {

    public static final int MAX_SEATS_PER_BOOKING = 10;

    private final Connection conn;

    public BookingService() {
        conn = DB_CONNECTION.getInstance().createConnection();
    }
    

    public List<ModelTickets> bookTickets(
        int userId,
        int showtimeId,
        List<String> seatCodes
    ) throws SQLException {

	    //kiểm tra dữ liệu đầu vào
	    if (userId <= 0) {
	        throw new IllegalArgumentException("User không hợp lệ.");
	    }
	
	    if (seatCodes == null || seatCodes.isEmpty()) {
	        throw new IllegalArgumentException("Bạn chưa chọn ghế.");
	    }
	    
	    //chuẩn hóa và loại bỏ ghế trùng
	    Set<String> seats = new LinkedHashSet<>();
	 
	    for (String code : seatCodes) {
	        if (code == null || code.isBlank()) {
	            continue;
	        }
	
	        seats.add(code.trim().toUpperCase());
	    }
	
	    if (seats.isEmpty()) {
	        throw new IllegalArgumentException("Bạn chưa chọn ghế.");
	    }
	
	    if (seats.size() > MAX_SEATS_PER_BOOKING) {
	        throw new IllegalArgumentException(
	                "Mỗi lần đặt tối đa " + MAX_SEATS_PER_BOOKING + " ghế."
	        );
	    }
	
	    //bắt đầu transaction
	    boolean oldAutoCommit = conn.getAutoCommit();
	
	    try {
	        conn.setAutoCommit(false);
	
	        // Lấy thông tin suất chiếu và khóa dòng
	        String showtimeSql =
	                "SELECT room_id, start_time, base_price " +
	                "FROM showtimes " +
	                "WHERE showtime_id = ? AND is_deleted = 0 " +
	                "FOR UPDATE";
	        	
	        
	        
	        int roomId;
	        LocalDateTime startTime;
	        double price;
	
	        try (PreparedStatement ps =
	                     conn.prepareStatement(showtimeSql)) {
	
	            ps.setInt(1, showtimeId);
	
	            try (ResultSet rs = ps.executeQuery()) {
	                if (!rs.next()) {
	                    throw new SQLException("Không tìm thấy suất chiếu.");
	                }
	
	                roomId = rs.getInt("room_id");
	
	                startTime = rs.getTimestamp("start_time")
	                              .toLocalDateTime();
	
	                price = rs.getDouble("base_price");
	            }
	        }
	
	        
	        
	        
	        //không cho đặt suất chiếu đã bắt đầu
	        if (!startTime.isAfter(LocalDateTime.now())) {
	            throw new SQLException(
	                    "Suất chiếu đã bắt đầu, không thể đặt vé."
	            );
	        }
	
	        List<ModelTickets> createdTickets = new ArrayList<>();        
	        
	        
	        
	///////////////////////////////////////////////	        
	/// 
	/// 
	        //kiểm tra từng ghế và tạo vé
	        for (String code : seats) {
	
	            //tách mã ghế, ví dụ A12 -> hàng A, số 12
	            if (!code.matches("[A-Z]\\d+")) {
	                throw new SQLException("Mã ghế không hợp lệ: " + code);
	            }
	
	            String seatRow = code.substring(0, 1);
	            int seatNumber = Integer.parseInt(code.substring(1));
	
	            int seatId;
	
	            //tìm ghế thuộc phòng của suất chiếu
	            String seatSql =
	                    "SELECT seat_id " +
	                    "FROM seats " +
	                    "WHERE room_id = ? " +
	                    "AND seat_row = ? " +
	                    "AND seat_number = ? " +
	                    "AND status = 'AVAILABLE' " +
	                    "FOR UPDATE";
	
	            try (PreparedStatement ps =
	                         conn.prepareStatement(seatSql)) {
	
	                ps.setInt(1, roomId);
	                ps.setString(2, seatRow);
	                ps.setInt(3, seatNumber);
	
	                try (ResultSet rs = ps.executeQuery()) {
	                    if (!rs.next()) {
	                        throw new SQLException(
	                                "Ghế " + code +
	                                " không tồn tại hoặc không khả dụng."
	                        );
	                    }
	
	                    seatId = rs.getInt("seat_id");
	                }
	            }
	
	            //kiểm tra ghế đã được đặt cho suất này chưa
	            String checkSql =
	                    "SELECT ticket_id FROM tickets " +
	                    "WHERE showtime_id = ? AND seat_id = ?";
	
	            try (PreparedStatement ps =
	                         conn.prepareStatement(checkSql)) {
	
	                ps.setInt(1, showtimeId);
	                ps.setInt(2, seatId);
	
	                try (ResultSet rs = ps.executeQuery()) {
	                    if (rs.next()) {
	                        throw new SQLException(
	                                "Ghế " + code +
	                                " đã được đặt cho suất chiếu này."
	                        );
	                    }
	                }
	            }
	
	            // Thêm vé vào database
	            String insertSql =
	                    "INSERT INTO tickets " +
	                    "(user_id, showtime_id, seat_id, price) " +
	                    "VALUES (?, ?, ?, ?)";
	
	            try (PreparedStatement ps =
	                         conn.prepareStatement(insertSql)) {
	
	                ps.setInt(1, userId);
	                ps.setInt(2, showtimeId);
	                ps.setInt(3, seatId);
	                ps.setDouble(4, price);
	
	                ps.executeUpdate();
	            }
	
	            // Tạo object ModelTickets tương ứng
	            ModelTickets ticket = new ModelTickets(
	                    userId,
	                    showtimeId,
	                    seatId,
	                    price
	            );
	
	            createdTickets.add(ticket);
	        }
	////
	/// 
	/// 
	//////////////////////////////////////////////////
	
	        // Nếu tất cả ghế hợp lệ thì commit
	        conn.commit();
	
	        return createdTickets;
	
	    } catch (Exception e) {
	
	        // Nếu một ghế lỗi, rollback toàn bộ các vé vừa đặt
	        conn.rollback();
	
	        if (e instanceof SQLException) {
	            throw (SQLException) e;
	        }
	
	        throw new SQLException("Đặt vé thất bại.", e);
	
	    } finally {
	        conn.setAutoCommit(oldAutoCommit);
	    }
	}
    

	private static final Scanner scanner = new Scanner(System.in);
	
	public List<ModelTickets> bookTickets() throws SQLException {
	
	    System.out.println("===== BOOK TICKETS =====");
	
	    System.out.print("Nhap User ID: ");
	    int userId = Integer.parseInt(scanner.nextLine());
	
	    System.out.print("Nhap Showtime ID: ");
	    int showtimeId = Integer.parseInt(scanner.nextLine());
	
	    System.out.print("Nhap ma ghe (vd: A1,A2,A3): ");
	    String inputSeats = scanner.nextLine();
	
	    // Chuyển chuỗi ghế thành danh sách
	    List<String> seatCodes =
	            Arrays.asList(inputSeats.split(","));
	
	    // Gọi hàm xử lý đặt vé đã viết trước đó
	    return bookTickets(userId, showtimeId, seatCodes);
}



}
