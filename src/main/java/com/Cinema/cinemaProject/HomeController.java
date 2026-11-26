package com.Cinema.cinemaProject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import DAO.MovieDAO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.ModelMovies;
import model.ModelMovies.Status;
import model.ModelSeats;
import util.UiUtil;
import util.ViewNavigator;

public class HomeController {

    private static final double CARD_WIDTH = 230.0;
    private static final double POSTER_WIDTH = 206.0;
    private static final double POSTER_HEIGHT = 260.0;

    @FXML private Button editButton;
    @FXML private Label userLabel;

    @FXML private VBox showingBox;
    @FXML private VBox upcomingBox;

    private final MovieDAO movieDAO = new MovieDAO();

    @FXML
    private void initialize() {
        // Nút "Chỉnh sửa" chỉ dành cho ADMIN.
        // setManaged(false) để nút biến mất hẳn, không để lại khoảng trống.
        boolean admin = Session.isAdmin();
        editButton.setVisible(admin);
        editButton.setManaged(admin);

        if (Session.isLoggedIn()) {
            userLabel.setText(Session.getEmail() + " (" + Session.getRole() + ")");
        } else {
            userLabel.setText("");
        }

        loadMovies();
    }

    @FXML
    private void handleRefresh() {
        loadMovies();
    }

    private void loadMovies() {
        renderMovieSection(showingBox, Status.SHOWING, "Hiện chưa có phim đang chiếu.");
        renderMovieSection(upcomingBox, Status.UPCOMING, "Hiện chưa có phim sắp chiếu.");
    }

    private void renderMovieSection(VBox container, Status status, String emptyMessage) {
        container.getChildren().clear();

        try {
            List<ModelMovies> movies = movieDAO.findByStatus(status);
            if (movies.isEmpty()) {
                Label emptyLabel = new Label(emptyMessage);
                emptyLabel.setStyle("-fx-text-fill: gray;");
                container.getChildren().add(emptyLabel);
                return;
            }

            FlowPane grid = new FlowPane(16.0, 16.0);
            for (ModelMovies movie : movies) {
                grid.getChildren().add(createMovieCard(movie));
            }
            container.getChildren().add(grid);
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Không thể tải danh sách phim: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #d32f2f;");
            errorLabel.setWrapText(true);
            container.getChildren().add(errorLabel);
        }
    }

    private VBox createMovieCard(ModelMovies movie) {
        VBox card = new VBox(8.0);
        card.setPrefWidth(CARD_WIDTH);
        card.setMinWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);
        card.setPadding(new Insets(12.0));
        card.setStyle("-fx-background-color: #fafafa; -fx-background-radius: 8; "
                + "-fx-border-color: #e0e0e0; -fx-border-radius: 8;");

        StackPane posterBox = createPosterPane(movie);
        posterBox.setCursor(Cursor.HAND);
        posterBox.setOnMouseClicked(e -> openMovieDetails(movie));

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #222222;");
        titleLabel.setMinHeight(38.0);

        Label durationLabel = new Label("Thời lượng: " + movie.getDurationMins() + " phút");
        durationLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

        boolean isShowing = movie.getStatus() == Status.SHOWING;
        Label statusBadge = new Label(isShowing ? "Đang chiếu" : "Sắp chiếu");
        statusBadge.setStyle(isShowing
                ? "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-font-size: 11px; "
                  + "-fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 4;"
                : "-fx-background-color: #e3f2fd; -fx-text-fill: #1565c0; -fx-font-size: 11px; "
                  + "-fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 4;");

        String desc = movie.getDescription() != null ? movie.getDescription().trim() : "";
        Label descLabel = new Label(desc.isEmpty() ? "Chưa có mô tả." : desc);
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(40.0);
        descLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button detailBtn = new Button("Chi tiết");
        detailBtn.setMaxWidth(Double.MAX_VALUE);
        detailBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #222222; -fx-cursor: hand;");
        detailBtn.setOnAction(e -> openMovieDetails(movie));

        HBox actions = new HBox(8.0);
        HBox.setHgrow(detailBtn, Priority.ALWAYS);
        actions.getChildren().add(detailBtn);

        if (isShowing) {
            Button bookBtn = new Button("Đặt vé");
            bookBtn.setMaxWidth(Double.MAX_VALUE);
            bookBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; "
                    + "-fx-font-weight: bold; -fx-cursor: hand;");
            bookBtn.setOnAction(e -> openBookingDialog(movie));
            HBox.setHgrow(bookBtn, Priority.ALWAYS);
            actions.getChildren().add(bookBtn);
        }

        card.getChildren().addAll(posterBox, titleLabel, statusBadge, durationLabel, descLabel, spacer, actions);
        return card;
    }

    private StackPane createPosterPane(ModelMovies movie) {
        StackPane posterBox = new StackPane();
        posterBox.setPrefSize(POSTER_WIDTH, POSTER_HEIGHT);
        posterBox.setMinSize(POSTER_WIDTH, POSTER_HEIGHT);
        posterBox.setMaxSize(POSTER_WIDTH, POSTER_HEIGHT);
        posterBox.setStyle("-fx-background-color: #e8ecf2; -fx-background-radius: 6;");
        posterBox.setAlignment(Pos.CENTER);

        Label placeholder = new Label(movie.getTitle());
        placeholder.setWrapText(true);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setPadding(new Insets(12.0));
        placeholder.setStyle("-fx-text-fill: #5f6b7a; -fx-font-weight: bold; -fx-text-alignment: center;");

        ImageView posterView = new ImageView();
        posterView.setFitWidth(POSTER_WIDTH);
        posterView.setFitHeight(POSTER_HEIGHT);
        posterView.setPreserveRatio(true);

        Image img = UiUtil.loadImage(movie.getPosterUrl(), POSTER_WIDTH, POSTER_HEIGHT);
        if (img != null) {
            posterView.setImage(img);
            if (img.isError()) {
                placeholder.setVisible(true);
            } else if (img.getProgress() >= 1.0) {
                placeholder.setVisible(false);
            }
            img.errorProperty().addListener((obs, wasErr, isErr) -> {
                if (isErr) {
                    placeholder.setVisible(true);
                }
            });
            img.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                if (newProgress.doubleValue() >= 1.0 && !img.isError()) {
                    placeholder.setVisible(false);
                }
            });
        }

        posterBox.getChildren().addAll(placeholder, posterView);
        return posterBox;
    }

    private void openMovieDetails(ModelMovies movie) {
        Window owner = showingBox.getScene().getWindow();
        ViewNavigator.showDialog(
                "MovieDetails.fxml",
                owner,
                "Chi tiết phim - " + movie.getTitle(),
                (MovieDetailController c) -> c.setMovie(movie)
        );
    }

    private void openBookingDialog(ModelMovies movie) {
        Window owner = showingBox.getScene().getWindow();
        ViewNavigator.showDialog(
                "BookingTicket.fxml",
                owner,
                "Đặt vé - " + movie.getTitle(),
                (BookingController c) -> c.setData(movie, null, List.<ModelSeats>of())
        );
    }

    @FXML
    private void handleHome() {
        loadMovies();
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
}
