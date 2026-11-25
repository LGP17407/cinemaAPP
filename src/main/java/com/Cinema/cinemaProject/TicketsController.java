package com.Cinema.cinemaProject;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.BookingService;
import service.BookingService.BookedTicketView;

/**
 * Controller cho màn hình xem danh sách vé đã đặt (tickets.fxml).
 */
public class TicketsController {

    private static final NumberFormat CURRENCY_FORMAT =
            NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    @FXML private Button editButton;
    @FXML private Label userLabel;
    @FXML private Label statusLabel;
    @FXML private VBox ticketsListBox;

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

        loadBookedTickets();
    }

    private void loadBookedTickets() {
        ticketsListBox.getChildren().clear();
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        if (!Session.isLoggedIn()) {
            statusLabel.setText("Vui lòng đăng nhập để xem vé.");
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);
            return;
        }

        try {
            List<BookedTicketView> tickets =
                    BookingService.getBookedTicketsByEmail(Session.getEmail());

            if (tickets.isEmpty()) {
                Label emptyLabel = new Label("Bạn chưa đặt vé nào.");
                emptyLabel.setStyle("-fx-text-fill: gray;");
                ticketsListBox.getChildren().add(emptyLabel);
                return;
            }

            for (BookedTicketView ticket : tickets) {
                ticketsListBox.getChildren().add(createTicketCard(ticket));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Không thể tải danh sách vé từ cơ sở dữ liệu.");
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);
        }
    }

    private VBox createTicketCard(BookedTicketView ticket) {
        VBox card = new VBox(4.0);
        card.setPadding(new Insets(12.0));
        card.setStyle("-fx-background-color: #fafafa; -fx-background-radius: 6; "
                + "-fx-border-color: #e0e0e0; -fx-border-radius: 6;");

        HBox header = new HBox(12.0);
        Label titleLabel = new Label(ticket.movieTitle());
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label priceLabel = new Label(CURRENCY_FORMAT.format(ticket.price()) + " đ");
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100;");

        header.getChildren().addAll(titleLabel, spacer, priceLabel);

        Label detailLabel = new Label(String.format(
                "Mã vé #%d  |  Phòng: %s  |  Ghế: %s (%s)  |  Suất chiếu: %s",
                ticket.ticketId(),
                ticket.roomName(),
                ticket.seatCode(),
                ticket.seatType(),
                ticket.showtimeStart()
        ));
        detailLabel.setStyle("-fx-text-fill: #444444;");

        Label bookedAtLabel = new Label("Ngày đặt: " + ticket.bookedAt());
        bookedAtLabel.setStyle("-fx-text-fill: #777777; -fx-font-size: 12px;");

        card.getChildren().addAll(header, detailLabel, bookedAtLabel);
        return card;
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
        loadBookedTickets();
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
}