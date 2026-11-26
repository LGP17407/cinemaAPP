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
    private int availableSeats = -1; // số ghế trống của suất chiếu (-1 nếu chưa tính)

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

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    /** startTime lưu dạng String ("2026-11-25T19:30" hoặc "2026-11-25 19:30:00.0") -> LocalDateTime. */
    public LocalDateTime startDateTime() {
        return parseDateTime(startTime);
    }

    /** endTime -> LocalDateTime (có thể null nếu dữ liệu chưa có). */
    public LocalDateTime endDateTime() {
        return parseDateTime(endTime);
    }

    private static LocalDateTime parseDateTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(raw.trim().replace(' ', 'T'));
    }

    /** Giá vé 1 ghế dưới dạng BigDecimal (tính tiền không bị sai số). */
    public BigDecimal price() { return BigDecimal.valueOf(basePrice); }

    /** ComboBox hiển thị: "25/11/2026 19:30 - Room 01 - 90.000 đ (50 ghế trống)". */
    @Override
    public String toString() {
        LocalDateTime start = startDateTime();
        String t = start != null
                ? DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(start)
                : (startTime != null ? startTime : "—");
        String p = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(basePrice);
        String room = (roomName != null && !roomName.isBlank()) ? roomName : "Phòng " + roomId;
        String seatsInfo = availableSeats >= 0 ? " (" + availableSeats + " ghế trống)" : "";
        return t + " - " + room + " - " + p + " đ" + seatsInfo;
    }
}