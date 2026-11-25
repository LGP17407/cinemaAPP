
package com.Cinema.cinemaProject;

/**
 * Lưu thông tin người dùng đang đăng nhập.
 */
public final class Session {

    private static int userId;
    private static String email;
    private static String role;

    private Session() {
    }

    // Đăng nhập
    public static void login(int userId, String email, String role) {

        Session.userId = userId;
        Session.email = email;
        Session.role = role;
    }

    // Đăng xuất
    public static void logout() {

        userId = 0;
        email = null;
        role = null;
    }

    // Kiểm tra đăng nhập
    public static boolean isLoggedIn() {
        return userId > 0 && email != null && role != null;
    }

    // Kiểm tra quyền ADMIN
    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    // Lấy ID người dùng
    public static int getUserId() {
        return userId;
    }

    // Lấy email
    public static String getEmail() {
        return email;
    }

    // Lấy role
    public static String getRole() {
        return role;
    }
}
