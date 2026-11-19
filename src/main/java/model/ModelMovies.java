package model;

public class ModelMovies {
    private int movieId;
    private String title;
    private String description;
    private int durationMins;
    private String status = "SHOWING";     // Mặc định SHOWING giống schema
    private boolean isDeleted = false;     // Mặc định 0 giống schema
    private String createdAt;

    // 1. Constructor rỗng
    public ModelMovies() {
    }

    // 2. Constructor dùng khi tạo mới Movie (chưa có movieId, isDeleted và createdAt)
    public ModelMovies(String title, String description, int durationMins, String status) {
        this.title = title;
        this.description = description;
        this.durationMins = durationMins;
        this.status = status;
    }

    // 3. Constructor đầy đủ (dùng khi đọc dữ liệu từ MySQL lên)
    public ModelMovies(int movieId, String title, String description, int durationMins,
                       String status, boolean isDeleted, String createdAt) {
        this.movieId = movieId;
        this.title = title;
        this.description = description;
        this.durationMins = durationMins;
        this.status = status;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMins() {
        return durationMins;
    }

    public void setDurationMins(int durationMins) {
        this.durationMins = durationMins;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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