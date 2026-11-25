package com.Cinema.cinemaProject;

/**
 * Lưu thông tin người dùng đang đăng nhập để mọi màn hình đều đọc được.
 * Ứng dụng desktop chỉ có một người dùng tại một thời điểm nên dùng biến static là đủ.
 */
public final class Session {

    private static String email;
    private static String role;

    private Session() {
    }

    public static void login(String email, String role) {
        Session.email = email;
        Session.role = role;
    }

    public static void logout() {
        email = null;
        role = null;
    }

    public static boolean isLoggedIn() {
        return role != null;
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public static String getEmail() {
        return email;
    }

    public static String getRole() {
        return role;
    }
}
