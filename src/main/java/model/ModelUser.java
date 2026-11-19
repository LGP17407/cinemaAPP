package model;

public class ModelUser {
	 	private int userId;
	    private String fullName;
	    private String email;
	    private String password;
	    private String role = "USER"; // Mặc định là USER giống schema
	    private String createdAt;

	    // 1. Constructor rỗng
	    public ModelUser() {
	    }

	    // 2. Constructor dùng khi tạo mới User (chưa có userId và createdAt)
	    public ModelUser(String fullName, String email, String password, String role) {
	        this.fullName = fullName;
	        this.email = email;
	        this.password = password;
	        this.role = role;
	    }

	    // 3. Constructor đầy đủ (dùng khi đọc dữ liệu từ MySQL lên)
	    public ModelUser(int userId, String fullName, String email, String password, String role, String createdAt) {
	        this.userId = userId;
	        this.fullName = fullName;
	        this.email = email;
	        this.password = password;
	        this.role = role;
	        this.createdAt = createdAt;
	    }

	    // Getters & Setters
	    public int getUserId() {
	        return userId;
	    }

	    public void setUserId(int userId) {
	        this.userId = userId;
	    }

	    public String getFullName() {
	        return fullName;
	    }

	    public void setFullName(String fullName) {
	        this.fullName = fullName;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public String getRole() {
	        return role;
	    }

	    public void setRole(String role) {
	        this.role = role;
	    }

	    public String getCreatedAt() {
	        return createdAt;
	    }

	    public void setCreatedAt(String createdAt) {
	        this.createdAt = createdAt;
	    }
}
