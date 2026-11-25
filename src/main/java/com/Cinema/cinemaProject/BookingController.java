
package com.Cinema.cinemaProject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import DAO.SeatDAO;
import DAO.ShowtimeDAO;
import DAO.TicketDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import model.ModelMovies;
import model.ModelSeats;
import model.ModelShowtimes;

import util.UiUtil;
import util.ViewNavigator;

/** POPUP 2: chọn suất chiếu + chọn ghế. */
public class BookingController {

    private static final int MAX_SEATS = 8;

    private static final String SEAT_FREE =
            "-fx-background-color:#4caf50; -fx-text-fill:white;";

    private static final String SEAT_SELECTED =
            "-fx-background-color:#2196f3; -fx-text-fill:white;";

    private static final String SEAT_BOOKED =
            "-fx-background-color:#9e9e9e; -fx-text-fill:white;";

    /** Hàng rồi số: A1, A2, ..., A10. */
    private static final Comparator<ModelSeats> SEAT_ORDER =
            Comparator.comparing(ModelSeats::getSeatRow)
                    .thenComparingInt(ModelSeats::getSeatNumber);

    @FXML
    private Label lblMovie, lblSelected, lblTotal, lblError;

    @FXML
    private ComboBox<ModelShowtimes> cbShowtime;

    @FXML
    private GridPane seatGrid;

    @FXML
    private Button btnNext;

    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    private ModelMovies movie;

    private final Map<Integer, ModelSeats> selected = new HashMap<>();

    private int builtShowtimeId = -1;

    /**
     * @param preselected suất đã chọn trước, có thể null
     * @param preSeats ghế đã chọn trước đó, có thể rỗng
     */
    public void setData(ModelMovies movie,
                        ModelShowtimes preselected,
                        Collection<ModelSeats> preSeats) {

        this.movie = movie;
        lblMovie.setText(movie.getTitle());

        try {

            List<ModelShowtimes> showtimes =
                    showtimeDAO.findUpcomingByMovie(movie.getMovieId());

            cbShowtime.getItems().setAll(showtimes);

            if (showtimes.isEmpty()) {
                lblError.setText("Phim này hiện chưa có suất chiếu.");
                btnNext.setDisable(true);
                return;
            }

            if (preselected != null) {

                builtShowtimeId = preselected.getShowtimeId();

                for (ModelSeats s : preSeats) {
                    selected.put(s.getSeatId(), s);
                }

                showtimes.stream()
                        .filter(s -> s.getShowtimeId()
                                == preselected.getShowtimeId())
                        .findFirst()
                        .ifPresent(cbShowtime.getSelectionModel()::select);
            }

        } catch (SQLException e) {

            lblError.setText(
                    "Không tải được suất chiếu: " + e.getMessage()
            );

            btnNext.setDisable(true);
            return;
        }

        if (cbShowtime.getValue() != null) {
            buildSeatGrid();
        }
    }

    @FXML
    private void onShowtimeChanged() {

        lblError.setText("");
        buildSeatGrid();
    }

    /**
     * Vẽ sơ đồ ghế từ bảng seats của phòng.
     * Ghế đã đặt / không khả dụng thì xám và khóa.
     */
    private void buildSeatGrid() {

        ModelShowtimes st = cbShowtime.getValue();

        if (st == null) {
            return;
        }

        if (st.getShowtimeId() != builtShowtimeId) {
            selected.clear();
            builtShowtimeId = st.getShowtimeId();
        }

        List<ModelSeats> seats;
        Set<Integer> booked;

        try {

            seats = seatDAO.findByRoom(st.getRoomId());

            booked = ticketDAO.getBookedSeatIds(
                    st.getShowtimeId()
            );

        } catch (SQLException e) {

            lblError.setText(
                    "Không tải được ghế: " + e.getMessage()
            );

            return;
        }

        selected.keySet().removeAll(booked);

        List<String> rows = seats.stream()
                .map(ModelSeats::getSeatRow)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        seatGrid.getChildren().clear();

        for (ModelSeats seat : seats) {

            // Ghép hàng ghế và số ghế
            String seatCode =
                    seat.getSeatRow() + seat.getSeatNumber();

            ToggleButton b = new ToggleButton(seatCode);

            b.setPrefSize(48, 34);

            boolean unavailable =
                    booked.contains(seat.getSeatId())
                    || !"AVAILABLE".equals(seat.getStatus());

            if (unavailable) {

                b.setDisable(true);
                b.setStyle(SEAT_BOOKED);

            } else {

                b.setSelected(
                        selected.containsKey(seat.getSeatId())
                );

                b.setStyle(
                        b.isSelected() ? SEAT_SELECTED : SEAT_FREE
                );

                b.selectedProperty().addListener(
                        (obs, was, isSel) ->
                                onSeatToggled(b, seat, isSel)
                );
            }

            seatGrid.add(
                    b,
                    seat.getSeatNumber() - 1,
                    rows.indexOf(seat.getSeatRow())
            );
        }

        refreshSummary();
    }

    private void onSeatToggled(ToggleButton b,
                               ModelSeats seat,
                               boolean isSelected) {

        if (isSelected) {

            if (selected.size() >= MAX_SEATS) {

                b.setSelected(false);

                lblError.setText(
                        "Chỉ được chọn tối đa "
                        + MAX_SEATS + " ghế mỗi lần."
                );

                return;
            }

            selected.put(seat.getSeatId(), seat);
            lblError.setText("");

        } else {

            selected.remove(seat.getSeatId());
        }

        b.setStyle(
                isSelected ? SEAT_SELECTED : SEAT_FREE
        );

        refreshSummary();
    }

    private List<ModelSeats> sortedSeats() {

        List<ModelSeats> list =
                new ArrayList<>(selected.values());

        list.sort(SEAT_ORDER);

        return list;
    }

    private void refreshSummary() {

        lblSelected.setText(
                "Ghế đã chọn: "
                + (selected.isEmpty()
                        ? "-"
                        : sortedSeats().stream()
                                .map(s -> s.getSeatRow()
                                        + s.getSeatNumber())
                                .collect(Collectors.joining(", "))
                )
        );

        ModelShowtimes st = cbShowtime.getValue();

        BigDecimal total = (st == null)
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(st.getBasePrice())
                        .multiply(
                                BigDecimal.valueOf(selected.size())
                        );

        lblTotal.setText(UiUtil.money(total));
    }

    @FXML
    private void onNext() {

        ModelShowtimes st = cbShowtime.getValue();

        if (st == null) {
            lblError.setText("Vui lòng chọn suất chiếu.");
            return;
        }

        if (selected.isEmpty()) {
            lblError.setText("Vui lòng chọn ít nhất 1 ghế.");
            return;
        }

        List<ModelSeats> seats = sortedSeats();

        ViewNavigator.replaceScene(
                lblMovie.getScene().getWindow(),
                "ReviewTicketDialog.fxml",
                (ReviewTicketController c) ->
                        c.setData(movie, st, seats)
        );
    }

    @FXML
    private void onCancel() {

        ((Stage) lblMovie.getScene().getWindow()).close();
    }
}
