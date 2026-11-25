package model;

public class ModelUser {
		public enum Role { USER, ADMIN }

		private int userId;
	    private String fullName;
	    private String email;
	    private String password_hash;
	    private Role role = Role.USER; // Mặc định là USER giống schema
	    private String createdAt;

	    // 1. Constructor rỗng
	    public ModelUser() {
	    }

	    // 2. Constructor dùng khi tạo mới User (chưa có userId và createdAt)
	    public ModelUser(String fullName, String email, String password_hash, Role role) {
	        this.fullName = fullName;
	        this.email = email;
	        this.password_hash = password_hash;
	        this.role = role;
	    }

	    // 3. Constructor đầy đủ (dùng khi đọc dữ liệu từ MySQL lên)
	    public ModelUser(int userId, String fullName, String email, String password_hash, Role role, String createdAt) {
	        this.userId = userId;
	        this.fullName = fullName;
	        this.email = email;
	        this.password_hash = password_hash;
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

	    public String getpassword_hash() {
	        return password_hash;
	    }

	    public void setpassword_hash(String password_hash) {
	        this.password_hash = password_hash;
	    }

	    public Role getRole() {
	        return role;
	    }

	    public void setRole(Role role) {
	        this.role = role;
	    }

	    public String getCreatedAt() {
	        return createdAt;
	    }

	    public void setCreatedAt(String createdAt) {
	        this.createdAt = createdAt;
	    }
}
