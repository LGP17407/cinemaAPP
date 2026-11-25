
package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import db.DB_CONNECTION;
import model.ModelUser;
import com.Cinema.cinemaProject.Session;

public class UserServices {

    static Scanner sc = new Scanner(System.in);

    public static final String LOGIN_FAILED = "failed";
    public static final String LOGIN_ERROR = "error";

    public static final String SIGNUP_OK = "ok";
    public static final String SIGNUP_EMAIL_EXISTS = "email_exists";
    public static final String SIGNUP_ERROR = "error";

    // Đăng ký không đối số (Console)
    public static void signUp() {

        DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
        dbConnect.createConnection();

        String fullName, email, password, passwordCheck;

        System.out.println("Nhập họ và tên: ");
        fullName = sc.nextLine();

        System.out.println("Nhập email: ");
        email = sc.nextLine();

        System.out.println("Nhập mật khẩu: ");
        password = sc.nextLine();

        System.out.println("Nhập lại mật khẩu: ");
        passwordCheck = sc.nextLine();

        while (!password.equals(passwordCheck)) {
            System.out.println("Mật khẩu nhập lại chưa đúng");
            System.out.println("Mời nhập lại: ");
            passwordCheck = sc.nextLine();
        }

        password = PasswordUtil.hash(password);

        String sql = "INSERT INTO users(full_name, email, password_hash) "
                   + "VALUES (?, ?, ?)";

        try (PreparedStatement pSt =
                dbConnect.getConnection().prepareStatement(sql)) {

            pSt.setString(1, fullName);
            pSt.setString(2, email);
            pSt.setString(3, password);

            pSt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    // Đăng ký có đối số (JavaFX)
    public static String signUp(String fullName, String email,
                                String password) {

        DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();

        Connection conn = dbConnect.createConnection();

        if (conn == null) {
            return SIGNUP_ERROR;
        }

        String sql = "INSERT INTO users(full_name, email, password_hash) "
                   + "VALUES (?, ?, ?)";

        try (PreparedStatement pSt = conn.prepareStatement(sql)) {

            pSt.setString(1, fullName);
            pSt.setString(2, email);
            pSt.setString(3, PasswordUtil.hash(password));

            pSt.executeUpdate();

            return SIGNUP_OK;

        } catch (SQLException ex) {

            if (ex.getErrorCode() == 1062) {
                return SIGNUP_EMAIL_EXISTS;
            }

            System.out.println("SQLException: " + ex.getMessage());
            return SIGNUP_ERROR;
        }
    }

    // Đăng nhập không đối số (Console)
    public static String signIn() {

        DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
        Connection conn = dbConnect.createConnection();

        if (conn == null) {
            return LOGIN_ERROR;
        }

        System.out.println("Nhập email: ");
        String email = sc.nextLine();

        System.out.println("Nhập mật khẩu: ");
        String password = sc.nextLine();

        String sql = "SELECT password_hash, role "
                   + "FROM users WHERE email = ?";

        try (PreparedStatement pSt = conn.prepareStatement(sql)) {

            pSt.setString(1, email);

            try (ResultSet rs = pSt.executeQuery()) {

                if (!rs.next()) {
                    return LOGIN_FAILED;
                }

                String storedHash = rs.getString("password_hash");
                String role = rs.getString("role");

                if (PasswordUtil.verify(password, storedHash)) {
                    System.out.println("Đăng nhập thành công");
                    return role;
                }

                return LOGIN_FAILED;
            }

        } catch (SQLException ex) {

            System.out.println("Lỗi CSDL: " + ex.getMessage());
            ex.printStackTrace();

            return LOGIN_ERROR;
        }
    }

    // Đăng nhập có đối số (JavaFX)
    public static String signIn(String email, String password) {

        DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
        Connection conn = dbConnect.createConnection();

        if (conn == null) {
            return LOGIN_ERROR;
        }

        String sql = "SELECT user_id, password_hash, role "
                   + "FROM users WHERE email = ?";

        try (PreparedStatement pSt = conn.prepareStatement(sql)) {

            pSt.setString(1, email.trim());

            try (ResultSet rs = pSt.executeQuery()) {

                if (!rs.next()) {
                    return LOGIN_FAILED;
                }

                int userId = rs.getInt("user_id");
                String storedHash = rs.getString("password_hash");
                String role = rs.getString("role");

                if (PasswordUtil.verify(password, storedHash)) {

                    Session.login(userId, email, role);

                    System.out.println("Đăng nhập thành công");

                    return role;
                }

                return LOGIN_FAILED;
            }

        } catch (SQLException ex) {

            System.out.println("Lỗi CSDL: " + ex.getMessage());
            ex.printStackTrace();

            return LOGIN_ERROR;
        }
    }

    // Lấy thông tin người dùng theo email
    public static ModelUser getUserByEmail(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        DB_CONNECTION dbConnect = DB_CONNECTION.getInstance();
        Connection conn = dbConnect.createConnection();

        if (conn == null) {
            return null;
        }

        String sql = "SELECT user_id, full_name, email, role, "
                   + "DATE_FORMAT(created_at, '%d/%m/%Y') AS created_at "
                   + "FROM users WHERE email = ?";

        try (PreparedStatement pSt = conn.prepareStatement(sql)) {

            pSt.setString(1, email.trim());

            try (ResultSet rs = pSt.executeQuery()) {

                if (rs.next()) {

                    return new ModelUser(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        null,
                        rs.getString("role"),
                        rs.getString("created_at")
                    );
                }
            }

        } catch (SQLException ex) {

            System.out.println("Lỗi CSDL: " + ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }
}
