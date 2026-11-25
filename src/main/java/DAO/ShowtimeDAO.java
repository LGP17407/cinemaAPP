
package DAO;

import db.DB_CONNECTION;
import model.ModelShowtimes;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowtimeDAO {

    private int showtimeId;
    private int movieId;
    private int roomId;
    private String startTime;
    private String endTime;
    private double basePrice;
    private boolean isDeleted = false;
    private String createdAt;
    private String roomName;

    // 1. Constructor rỗng
    public ShowtimeDAO() {
    }

    // 2. Constructor dùng khi tạo mới Showtime
    public ShowtimeDAO(int movieId, int roomId, String startTime,
                       String endTime, double basePrice) {
        this.movieId = movieId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
    }

    // 3. Constructor đầy đủ
    public ShowtimeDAO(int showtimeId, int movieId, int roomId,
                       String startTime, String endTime,
                       double basePrice, boolean isDeleted,
                       String createdAt) {

        this.showtimeId = showtimeId;
        this.movieId = movieId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    /** Chuyển startTime thành LocalDateTime. */
    public LocalDateTime startDateTime() {
        return LocalDateTime.parse(
            startTime.trim().replace(' ', 'T')
        );
    }

    /** Giá vé một ghế dưới dạng BigDecimal. */
    public BigDecimal price() {
        return BigDecimal.valueOf(basePrice);
    }

    /**
     * Lấy danh sách suất chiếu sắp tới của một bộ phim.
     */
    public List<ModelShowtimes> findUpcomingByMovie(int movieId)
            throws SQLException {

        List<ModelShowtimes> list = new ArrayList<>();

        String sql = "SELECT showtime_id, movie_id, room_id, "
                   + "start_time, end_time, base_price, "
                   + "is_deleted, created_at "
                   + "FROM showtimes "
                   + "WHERE movie_id = ? "
                   + "AND is_deleted = 0 "
                   + "AND start_time > NOW() "
                   + "ORDER BY start_time ASC";

        try (Connection c = DB_CONNECTION.getInstance().createConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ModelShowtimes showtime = new ModelShowtimes(
                        rs.getInt("showtime_id"),
                        rs.getInt("movie_id"),
                        rs.getInt("room_id"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getDouble("base_price"),
                        rs.getBoolean("is_deleted"),
                        rs.getString("created_at")
                    );

                    list.add(showtime);
                }
            }
        }

        return list;
    }

    /** ComboBox hiển thị thông tin suất chiếu. */
    @Override
    public String toString() {

        String t = DateTimeFormatter
                .ofPattern("dd/MM/yyyy HH:mm")
                .format(startDateTime());

        String p = NumberFormat
                .getNumberInstance(Locale.forLanguageTag("vi-VN"))
                .format(basePrice);

        return t + " - "
                + (roomName != null ? roomName : "Phòng " + roomId)
                + " - " + p + " đ";
    }
}
