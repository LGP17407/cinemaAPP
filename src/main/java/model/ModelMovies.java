package model;

import java.time.LocalDateTime;

public class ModelMovies {
    public enum Status { UPCOMING, SHOWING, STOPPED }

    private int movieId;
    private String title;
    private String description;
    private int durationMins;
    private Status status = Status.SHOWING;
    private String genre;
    private String posterUrl;
    private boolean deleted = false;
    private LocalDateTime createdAt;

    public ModelMovies() {}

    // Tạo mới (chưa có id, createdAt)
    public ModelMovies(String title, String description, int durationMins,
                 Status status, String genre, String posterUrl) {
        this.title = title;
        this.description = description;
        this.durationMins = durationMins;
        this.status = status;
        this.genre = genre;
        this.posterUrl = posterUrl;
    }

    // Đầy đủ (đọc từ DB)
    public ModelMovies(int movieId, String title, String description, int durationMins,
                 Status status, String genre, String posterUrl,
                 boolean deleted, LocalDateTime createdAt) {
        this(title, description, durationMins, status, genre, posterUrl);
        this.movieId = movieId;
        this.deleted = deleted;
        this.createdAt = createdAt;
    }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDurationMins() { return durationMins; }
    public void setDurationMins(int durationMins) { this.durationMins = durationMins; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public boolean isNowShowing() { return status == Status.SHOWING; }

    @Override
    public String toString() {
        return title + " (" + durationMins + " phút)";
    }
}