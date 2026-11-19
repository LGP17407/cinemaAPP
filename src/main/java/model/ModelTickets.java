package model;

public class ModelTickets {
    private int ticketId;
    private int userId;
    private int showtimeId;
    private int seatId;
    private double price;
    private String createdAt;

    // 1. Constructor rỗng
    public ModelTickets() {
    }

    // 2. Constructor dùng khi tạo mới Ticket (chưa có ticketId và createdAt)
    public ModelTickets(int userId, int showtimeId, int seatId, double price) {
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seatId = seatId;
        this.price = price;
    }

    // 3. Constructor đầy đủ (dùng khi đọc dữ liệu từ MySQL lên)
    public ModelTickets(int ticketId, int userId, int showtimeId, int seatId, double price, String createdAt) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seatId = seatId;
        this.price = price;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}