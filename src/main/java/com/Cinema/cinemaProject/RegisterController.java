package com.Cinema.cinemaProject;

import java.io.IOException;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import service.UserServices;

public class RegisterController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin.");
            return;
        }
        if (fullName.length() > 100) {
            showError("Họ tên tối đa 100 ký tự.");
            return;
        }
        if (email.length() > 150 || !EMAIL_PATTERN.matcher(email).matches()) {
            showError("Email không hợp lệ.");
            return;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            showError("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Mật khẩu nhập lại không khớp.");
            confirmPasswordField.clear();
            return;
        }

        String result = UserServices.signUp(fullName, email, password);
        switch (result) {
            case UserServices.SIGNUP_OK -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Đăng ký thành công! Hãy đăng nhập.");
                alert.setHeaderText(null);
                alert.showAndWait();
                goToLogin();
            }
            case UserServices.SIGNUP_EMAIL_EXISTS -> showError("Email này đã được đăng ký.");
            default -> showError("Lỗi hệ thống, vui lòng thử lại sau.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            showError("Không mở được màn hình đăng nhập.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
    }
}
