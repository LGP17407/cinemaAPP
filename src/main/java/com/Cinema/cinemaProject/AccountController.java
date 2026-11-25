package com.Cinema.cinemaProject;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.ModelUser;
import service.UserServices;

/**
 * Controller cho màn hình xem thông tin tài khoản (account.fxml).
 */
public class AccountController {

    @FXML private Button editButton;
    @FXML private Label userLabel;

    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label createdAtLabel;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        boolean admin = Session.isAdmin();
        editButton.setVisible(admin);
        editButton.setManaged(admin);

        if (Session.isLoggedIn()) {
            userLabel.setText(Session.getEmail() + " (" + Session.getRole() + ")");
        } else {
            userLabel.setText("");
        }

        loadAccountInfo();
    }

    private void loadAccountInfo() {
        String email = Session.getEmail();
        ModelUser user = UserServices.getUserByEmail(email);

        if (user == null) {
            fullNameLabel.setText("—");
            emailLabel.setText(email != null ? email : "—");
            createdAtLabel.setText("—");
            statusLabel.setText("Không thể tải thông tin tài khoản từ cơ sở dữ liệu.");
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);
            return;
        }

        fullNameLabel.setText(user.getFullName() != null ? user.getFullName() : "—");
        emailLabel.setText(user.getEmail() != null ? user.getEmail() : "—");
        createdAtLabel.setText(user.getCreatedAt() != null ? user.getCreatedAt() : "—");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }

    @FXML
    private void handleHome() {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTickets() {
        try {
            App.setRoot("tickets");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccount() {
        loadAccountInfo();
    }

    @FXML
    private void handleEdit() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Tính năng \"Chỉnh sửa\" đang được phát triển.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        Session.logout();
        try {
            Stage stage = (Stage) userLabel.getScene().getWindow();
            stage.setMaximized(false);
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
