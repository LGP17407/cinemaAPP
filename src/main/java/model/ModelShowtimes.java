package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ModelShowtimes {
    private int showtimeId;
    private int movieId;
    private int roomId;
    private String startTime;
    private String endTime;
    private double basePrice;
    private boolean isDeleted = false; // Mặc định 0 giống schema
    private String createdAt;
    private String roomName;   // lấy từ JOIN rooms, chỉ để hiển thị

    // 1. Constructor rỗng
    public ModelShowtimes() {
    }

    // 2. Constructor dùng khi tạo mới Showtime (chưa có showtimeId, isDeleted và createdAt)
    public ModelShowtimes(int movieId, int roomId, String startTime, String endTime, double basePrice) {
        this.movieId = movieId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
    }

    // 3. Constructor đầy đủ (dùng khi đọc dữ liệu từ MySQL lên)
    public ModelShowtimes(int showtimeId, int movieId, int roomId, String startTime, String endTime,
                          double basePrice, boolean isDeleted, String createdAt) {
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

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    /** startTime lưu dạng String ("2026-11-25T19:30" hoặc "2026-11-25 19:30:00.0") -> LocalDateTime. */
    public LocalDateTime startDateTime() {
        return LocalDateTime.parse(startTime.trim().replace(' ', 'T'));
    }

    /** Giá vé 1 ghế dưới dạng BigDecimal (tính tiền không bị sai số). */
    public BigDecimal price() { return BigDecimal.valueOf(basePrice); }

    /** ComboBox hiển thị: "25/11/2026 19:30 - Phòng 1 - 90.000 đ". */
    @Override
    public String toString() {
        String t = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(startDateTime());
        String p = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(basePrice);
        return t + " - " + (roomName != null ? roomName : "Phòng " + roomId) + " - " + p + " đ";
    }
}