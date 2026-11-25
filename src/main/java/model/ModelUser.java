
package model;

public class ModelUser {

    private int userId;
    private String fullName;
    private String email;
    private String password_hash;
    private String role = "USER";
    private String createdAt;

    // Constructor rỗng
    public ModelUser() {
    }

    // Constructor tạo User mới
    public ModelUser(String fullName, String email,
                     String password_hash, String role) {

        this.fullName = fullName;
        this.email = email;
        this.password_hash = password_hash;
        this.role = role;
    }

    // Constructor đầy đủ
    public ModelUser(int userId, String fullName, String email,
                     String password_hash, String role,
                     String createdAt) {

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
