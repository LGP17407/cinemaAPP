package com.Cinema.cinemaProject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import service.UserServices;
import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Vui lòng nhập đủ email và mật khẩu.");
            return;
        }

        String result = UserServices.signIn(email, password);
        if (result.equals(UserServices.LOGIN_ERROR)) {
            messageLabel.setText("Lỗi hệ thống, vui lòng thử lại sau.");
            return;
        }
        if (result.equals(UserServices.LOGIN_FAILED)) {
            messageLabel.setText("Sai email hoặc mật khẩu.");
            passwordField.clear();
            return;
        }

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Đăng nhập thành công, role: " + result);
    }
    
    @FXML
    private void goToRegister() {
        try {
            App.setRoot("register");
        } catch (IOException e) {
            messageLabel.setText("Không mở được màn hình đăng ký.");
            e.printStackTrace();
        }
    }
    
    
}