package com.Cinema.cinemaProject;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeController {

    @FXML private Button editButton;
    @FXML private Label userLabel;

    // Các khung nội dung, sau này thêm danh sách phim vào đây
    @FXML private VBox showingBox;
    @FXML private VBox upcomingBox;

    @FXML
    private void initialize() {
        // Nút "Chỉnh sửa" chỉ dành cho ADMIN.
        // setManaged(false) để nút biến mất hẳn, không để lại khoảng trống.
        boolean admin = Session.isAdmin();
        editButton.setVisible(admin);
        editButton.setManaged(admin);

        userLabel.setText(Session.getEmail() + " (" + Session.getRole() + ")");
    }

    @FXML
    private void handleHome() {
        // Đang ở trang chủ rồi nên không cần làm gì.
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
        try {
            App.setRoot("account");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        if (!Session.isAdmin()) {
            return;
        }
        try {
            App.setRoot("admin");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void showComingSoon(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Tính năng \"" + featureName + "\" đang được phát triển.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
