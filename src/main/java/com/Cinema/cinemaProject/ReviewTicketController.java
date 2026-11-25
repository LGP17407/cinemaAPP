
package com.Cinema.cinemaProject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import DAO.TicketDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import model.ModelMovies;
import model.ModelSeats;
import model.ModelShowtimes;

import util.UiUtil;
import util.ViewNavigator;

/**
 * POPUP 3: Xem lại vé, quyết định đặt hay không.
 */
public class ReviewTicketController {

    @FXML
    private Label lblMovie, lblShowtime, lblRoom, lblSeats, lblTotal, lblError;

    @FXML
    private Button btnConfirm;

    private final TicketDAO ticketDAO = new TicketDAO();

    private ModelMovies movie;
    private ModelShowtimes showtime;
    private List<ModelSeats> seats;

    public void setData(ModelMovies movie, ModelShowtimes showtime,
                        List<ModelSeats> seats) {

        this.movie = movie;
        this.showtime = showtime;
        this.seats = seats;

        lblMovie.setText(movie.getTitle());

        // ModelShowtimes sử dụng String cho startTime
        lblShowtime.setText(showtime.getStartTime());

        // ModelShowtimes không có roomName, sử dụng roomId
        lblRoom.setText("Phòng " + showtime.getRoomId());

        // Ghép hàng ghế và số ghế
        lblSeats.setText(
            seats.stream()
                 .map(s -> s.getSeatRow() + s.getSeatNumber())
                 .collect(Collectors.joining(", "))
        );

        // basePrice có kiểu double
        BigDecimal total = BigDecimal.valueOf(showtime.getBasePrice())
                .multiply(BigDecimal.valueOf(seats.size()));

        lblTotal.setText(
            UiUtil.money(total) + "  (" + seats.size() + " vé)"
        );
    }

    /**
     * Đặt vé: transaction trong TicketDAO.
     * Thành công -> commit.
     * Có lỗi -> rollback.
     */
    @FXML
    private void onConfirm() {

        if (!Session.isLoggedIn()) {
            lblError.setText("Bạn chưa đăng nhập.");
            return;
        }

        btnConfirm.setDisable(true);

        List<Integer> seatIds = seats.stream()
                .map(ModelSeats::getSeatId)
                .collect(Collectors.toList());

        try {

            ticketDAO.bookTickets(
                Session.getUserId(),
                showtime.getShowtimeId(),
                seatIds
            );

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {

                lblError.setText(
                    "Có ghế vừa được người khác đặt. Vé chưa được tạo - "
                    + "vui lòng bấm \"Quay lại\" để chọn ghế khác."
                );

            } else {

                lblError.setText(
                    "Đặt vé thất bại, dữ liệu đã được hoàn tác: "
                    + e.getMessage()
                );

                btnConfirm.setDisable(false);
            }

            return;
        }

        UiUtil.showInfo("Đặt vé thành công!");

        close();
    }

    /**
     * Quay lại popup 2, giữ nguyên suất chiếu và ghế đã chọn.
     */
    @FXML
    private void onBack() {

        ViewNavigator.replaceScene(
            lblMovie.getScene().getWindow(),
            "BookingDialog.fxml",
            (BookingController c) -> c.setData(movie, showtime, seats)
        );
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        ((Stage) lblMovie.getScene().getWindow()).close();
    }
}
