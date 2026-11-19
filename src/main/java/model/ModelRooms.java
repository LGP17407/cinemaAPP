package model;

public class ModelRooms {
    private int roomId;
    private String roomName;
    private int totalSeats;
    private String createdAt;

    // 1. Constructor rỗng
    public ModelRooms() {
    }

    // 2. Constructor dùng khi tạo mới Room (chưa có roomId và createdAt)
    public ModelRooms(String roomName, int totalSeats) {
        this.roomName = roomName;
        this.totalSeats = totalSeats;
    }

    // 3. Constructor đầy đủ (dùng khi đọc dữ liệu từ MySQL lên)
    public ModelRooms(int roomId, String roomName, int totalSeats, String createdAt) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.totalSeats = totalSeats;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}