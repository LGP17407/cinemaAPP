package com.Cinema.cinemaProject;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.ModelMovies;
import model.ModelMovies.Status;
import model.ModelSeats;
import util.UiUtil;
import util.ViewNavigator;

/** POPUP 1: chi tiết phim. */
public class MovieDetailController {

    @FXML private ImageView imgPoster;
    @FXML private Label lblTitle, lblGenre, lblDuration, lblReleaseDate, lblStatus;
    @FXML private TextArea txtDescription;
    @FXML private Button btnBook;

    private ModelMovies movie;

    public void setMovie(ModelMovies movie) {
        this.movie = movie;
        imgPoster.setImage(UiUtil.loadImage(movie.getPosterUrl(), 400, 600));
        lblTitle.setText(movie.getTitle());
        lblGenre.setText("Thể loại: " + orDash(movie.getGenre()));
        lblDuration.setText("Độ dài: " + movie.getDurationMins() + " phút");
        // Model chỉ có createdAt; nếu thêm releaseDate thì đổi ở đây.
        lblReleaseDate.setText("Ngày thêm: "
                + (movie.getCreatedAt() != null ? UiUtil.DATE.format(movie.getCreatedAt()) : "—"));
        lblStatus.setText("Trạng thái: " + statusText(movie.getStatus()));
        txtDescription.setText(movie.getDescription() != null ? movie.getDescription() : "");
        btnBook.setDisable(movie.getStatus() != Status.SHOWING);
    }

    /** Khớp enum ModelMovies.Status. */
    private static String statusText(Status status) {
        if (status == null) return "Không rõ";
        return switch (status) {
            case SHOWING     -> "Đang chiếu";
            case COMING_SOON -> "Sắp chiếu";
            case ENDED       -> "Đã kết thúc";
        };
    }

    private static String orDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    @FXML
    private void onBook() {
        if (movie.getStatus() != Status.SHOWING) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Phim này hiện không mở bán vé.");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }
        ViewNavigator.replaceScene(lblTitle.getScene().getWindow(), "BookingDialog.fxml",
                (BookingController c) -> c.setData(movie, null, List.<ModelSeats>of()));
    }

    @FXML
    private void onClose() {
        ((Stage) lblTitle.getScene().getWindow()).close();
    }
}