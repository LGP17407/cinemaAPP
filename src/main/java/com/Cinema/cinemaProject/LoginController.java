package com.Cinema.cinemaProject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
        messageLabel.setStyle("-fx-text-fill: red;");
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
     // result lúc này là role: "ADMIN" hoặc "USER"
        Session.login(email, result);
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            App.setRoot("home");
            stage.setMaximized(true);
        } catch (IOException e) {
            messageLabel.setText("Không mở được trang chủ.");
            e.printStackTrace();
        }
        
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