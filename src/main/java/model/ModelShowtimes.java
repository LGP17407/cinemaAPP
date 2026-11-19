package model;

public class ModelShowtimes {
    private int showtimeId;
    private int movieId;
    private int roomId;
    private String startTime;
    private String endTime;
    private double basePrice;
    private boolean isDeleted = false; // Mặc định 0 giống schema
    private String createdAt;

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
}