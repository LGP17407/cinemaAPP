
package com.Cinema.cinemaProject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.Tooltip;
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
    private static final DateTimeFormatter TIME_ONLY = DateTimeFormatter.ofPattern("HH:mm");

    private static final String SEAT_FREE =
            "-fx-background-color:#4caf50; -fx-text-fill:white; -fx-font-weight:bold; -fx-cursor:hand;";

    private static final String SEAT_FREE_VIP =
            "-fx-background-color:#00897b; -fx-text-fill:white; -fx-font-weight:bold; "
            + "-fx-border-color:#ffd54f; -fx-border-width:1.5; -fx-border-radius:3; -fx-background-radius:3; -fx-cursor:hand;";

    private static final String SEAT_SELECTED =
            "-fx-background-color:#2196f3; -fx-text-fill:white; -fx-font-weight:bold; -fx-cursor:hand;";

    private static final String SEAT_BOOKED =
            "-fx-background-color:#9e9e9e; -fx-text-fill:white; -fx-opacity:0.85;";

    /** Hàng rồi số: A1, A2, ..., A10. */
    private static final Comparator<ModelSeats> SEAT_ORDER =
            Comparator.comparing(ModelSeats::getSeatRow)
                    .thenComparingInt(ModelSeats::getSeatNumber);

    @FXML
    private Label lblMovie, lblShowtimeInfo, lblSelected, lblTotal, lblError;

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
    private boolean updatingShowtimes = false;

    /**
     * @param preselected suất đã chọn trước, có thể null
     * @param preSeats ghế đã chọn trước đó, có thể rỗng
     */
    public void setData(ModelMovies movie,
                        ModelShowtimes preselected,
                        Collection<ModelSeats> preSeats) {

        this.movie = movie;
        lblMovie.setText(movie.getTitle());
        lblShowtimeInfo.setText("");
        lblError.setText("");
        btnNext.setDisable(false);
        selected.clear();
        builtShowtimeId = -1;
        seatGrid.getChildren().clear();

        updatingShowtimes = true;
        try {
            List<ModelShowtimes> showtimes =
                    showtimeDAO.findUpcomingByMovie(movie.getMovieId());

            cbShowtime.getItems().setAll(showtimes);

            if (showtimes.isEmpty()) {
                cbShowtime.setDisable(true);
                lblShowtimeInfo.setText("Chưa có suất chiếu sắp tới cho phim này.");
                lblError.setText("Phim này hiện chưa có suất chiếu sắp tới. Vui lòng thêm lịch chiếu ở mục Chỉnh sửa (Admin).");
                btnNext.setDisable(true);
                refreshSummary();
                return;
            }

            cbShowtime.setDisable(false);

            ModelShowtimes matched = null;
            if (preselected != null) {
                matched = showtimes.stream()
                        .filter(s -> s.getShowtimeId() == preselected.getShowtimeId())
                        .findFirst()
                        .orElse(null);
            }

            if (matched != null) {
                builtShowtimeId = matched.getShowtimeId();
                if (preSeats != null) {
                    for (ModelSeats s : preSeats) {
                        selected.put(s.getSeatId(), s);
                    }
                }
                cbShowtime.getSelectionModel().select(matched);
            } else {
                cbShowtime.getSelectionModel().selectFirst();
            }

        } catch (SQLException e) {
            cbShowtime.setDisable(true);
            lblError.setText("Không tải được suất chiếu: " + e.getMessage());
            btnNext.setDisable(true);
            refreshSummary();
            return;
        } finally {
            updatingShowtimes = false;
        }

        if (cbShowtime.getValue() != null) {
            buildSeatGrid();
        }
    }

    @FXML
    private void onShowtimeChanged() {
        if (updatingShowtimes) {
            return;
        }
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
            seatGrid.getChildren().clear();
            lblShowtimeInfo.setText("");
            refreshSummary();
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
            booked = ticketDAO.getBookedSeatIds(st.getShowtimeId());
        } catch (SQLException e) {
            lblError.setText("Không tải được ghế: " + e.getMessage());
            return;
        }

        Set<Integer> unavailableIds = new HashSet<>(booked);
        int freeCount = 0;
        for (ModelSeats seat : seats) {
            boolean avail = !booked.contains(seat.getSeatId())
                    && "AVAILABLE".equalsIgnoreCase(seat.getStatus());
            if (avail) {
                freeCount++;
            } else {
                unavailableIds.add(seat.getSeatId());
            }
        }
        selected.keySet().removeAll(unavailableIds);

        updateShowtimeInfo(st, freeCount, seats.size());

        List<String> rows = seats.stream()
                .map(ModelSeats::getSeatRow)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        seatGrid.getChildren().clear();

        for (int r = 0; r < rows.size(); r++) {
            Label rowLabel = new Label(rows.get(r));
            rowLabel.setStyle("-fx-font-weight:bold; -fx-text-fill:#444444; -fx-min-width:22; -fx-alignment:center;");
            seatGrid.add(rowLabel, 0, r);
        }

        for (ModelSeats seat : seats) {
            String seatCode = seat.getSeatRow() + seat.getSeatNumber();
            ToggleButton b = new ToggleButton(seatCode);
            b.setPrefSize(50, 34);

            boolean isBooked = booked.contains(seat.getSeatId());
            boolean isMaintOrUnavail = !"AVAILABLE".equalsIgnoreCase(seat.getStatus());
            boolean unavailable = isBooked || isMaintOrUnavail;

            if (unavailable) {
                b.setDisable(true);
                b.setStyle(SEAT_BOOKED);
                String reason = isBooked
                        ? "Đã được đặt"
                        : ("MAINTENANCE".equalsIgnoreCase(seat.getStatus()) ? "Đang bảo trì" : "Không khả dụng");
                b.setTooltip(new Tooltip(seatCode + " (" + seat.getSeatType() + ") - " + reason));
            } else {
                b.setSelected(selected.containsKey(seat.getSeatId()));
                b.setStyle(seatStyle(seat, b.isSelected()));
                b.setTooltip(new Tooltip(seatCode + " (" + seat.getSeatType() + ") - Còn trống"));

                b.selectedProperty().addListener(
                        (obs, was, isSel) -> onSeatToggled(b, seat, isSel)
                );
            }

            seatGrid.add(
                    b,
                    seat.getSeatNumber(),
                    rows.indexOf(seat.getSeatRow())
            );
        }

        btnNext.setDisable(freeCount == 0);
        if (freeCount == 0) {
            lblError.setText("Suất chiếu này đã hết ghế trống. Vui lòng chọn suất chiếu khác.");
        }

        refreshSummary();
    }

    private void updateShowtimeInfo(ModelShowtimes st, int freeCount, int totalCount) {
        String roomName = (st.getRoomName() != null && !st.getRoomName().isBlank())
                ? st.getRoomName()
                : "Phòng " + st.getRoomId();
        LocalDateTime start = st.startDateTime();
        LocalDateTime end = st.endDateTime();
        String timeRange = start != null
                ? UiUtil.DATE_TIME.format(start) + (end != null ? " - " + TIME_ONLY.format(end) : "")
                : st.getStartTime();
        lblShowtimeInfo.setText(String.format(
                "%s  |  %s  |  Giá vé: %s  |  Ghế trống: %d/%d",
                roomName,
                timeRange,
                UiUtil.money(st.price()),
                freeCount,
                totalCount
        ));
    }

    private static String seatStyle(ModelSeats seat, boolean isSelected) {
        if (isSelected) {
            return SEAT_SELECTED;
        }
        return "VIP".equalsIgnoreCase(seat.getSeatType()) ? SEAT_FREE_VIP : SEAT_FREE;
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

        b.setStyle(seatStyle(seat, isSelected));
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
