package model;

public class ModelSeats {
    private int seatId;
    private int roomId;
    private String seatRow;
    private int seatNumber;
    private String seatType = "NORMAL";   // Mặc định NORMAL giống schema
    private String status = "AVAILABLE";  // Mặc định AVAILABLE giống schema

    // 1. Constructor rỗng
    public ModelSeats() {
    }

    // 2. Constructor dùng khi tạo mới Seat (chưa có seatId)
    public ModelSeats(int roomId, String seatRow, int seatNumber, String seatType, String status) {
        this.roomId = roomId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.status = status;
    }

    // 3. Constructor đầy đủ (dùng khi đọc dữ liệu từ MySQL lên)
    public ModelSeats(int seatId, int roomId, String seatRow, int seatNumber, String seatType, String status) {
        this.seatId = seatId;
        this.roomId = roomId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.status = status;
    }

    // Getters & Setters
    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}