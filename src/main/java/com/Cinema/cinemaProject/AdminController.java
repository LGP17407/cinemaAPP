package com.Cinema.cinemaProject;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Record;
import service.AdminServices;

/**
 * Controller cho màn hình Chỉnh sửa của Admin (admin.fxml):
 * quản lý phim, quản lý lịch chiếu, quản lý phòng và ghế.
 */
public class AdminController {

    private static final DateTimeFormatter DT_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter TIME_INPUT = DateTimeFormatter.ofPattern("H:mm");
    private static final NumberFormat CURRENCY_FORMAT =
            NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    // Thanh điều hướng
    @FXML private Button editButton;
    @FXML private Label userLabel;

    // Tab 1: Quản lý phim
    @FXML private TableView<Record.movies> moviesTable;
    @FXML private TableColumn<Record.movies, Number> movieIdCol;
    @FXML private TableColumn<Record.movies, String> movieTitleCol;
    @FXML private TableColumn<Record.movies, Number> movieDurationCol;
    @FXML private TableColumn<Record.movies, String> movieStatusCol;
    @FXML private TableColumn<Record.movies, String> moviePosterCol;

    @FXML private Label selectedMovieLabel;
    @FXML private TextField movieTitleField;
    @FXML private TextField movieDurationField;
    @FXML private TextField moviePosterField;
    @FXML private ComboBox<String> movieStatusCombo;
    @FXML private TextArea movieDescArea;
    @FXML private Label movieFeedbackLabel;
    @FXML private Button updateMovieBtn;
    @FXML private Button stopMovieBtn;

    // Tab 2: Quản lý lịch chiếu
    @FXML private TableView<Record.showTimes> showtimesTable;
    @FXML private TableColumn<Record.showTimes, Number> stIdCol;
    @FXML private TableColumn<Record.showTimes, String> stMovieCol;
    @FXML private TableColumn<Record.showTimes, String> stRoomCol;
    @FXML private TableColumn<Record.showTimes, String> stStartCol;
    @FXML private TableColumn<Record.showTimes, String> stEndCol;
    @FXML private TableColumn<Record.showTimes, String> stPriceCol;
    @FXML private TableColumn<Record.showTimes, String> stStatusCol;
    @FXML private TableColumn<Record.showTimes, Number> stBookedCol;

    @FXML private Label selectedShowtimeLabel;
    @FXML private ComboBox<Record.movies> stMovieCombo;
    @FXML private ComboBox<Record.rooms> stRoomCombo;
    @FXML private DatePicker stDatePicker;
    @FXML private TextField stTimeField;
    @FXML private Label stEndTimePreviewLabel;
    @FXML private TextField stPriceField;
    @FXML private Label stWarningLabel;
    @FXML private Label stFeedbackLabel;
    @FXML private Button updateShowtimeBtn;
    @FXML private Button cancelShowtimeBtn;

    // Tab 3: Quản lý phòng & ghế theo suất chiếu
    @FXML private TableView<Record.showTimes> roomShowtimesTable;
    @FXML private TableColumn<Record.showTimes, Number> rsIdCol;
    @FXML private TableColumn<Record.showTimes, String> rsMovieCol;
    @FXML private TableColumn<Record.showTimes, String> rsRoomCol;
    @FXML private TableColumn<Record.showTimes, String> rsStartCol;
    @FXML private TableColumn<Record.showTimes, String> rsStatusCol;
    @FXML private TableColumn<Record.showTimes, Number> rsTotalSeatsCol;
    @FXML private TableColumn<Record.showTimes, Number> rsBookedSeatsCol;
    @FXML private TableColumn<Record.showTimes, Number> rsEmptySeatsCol;

    @FXML private Label roomSeatsTitleLabel;
    @FXML private Label roomSeatsSummaryLabel;
    @FXML private GridPane seatsGrid;
    @FXML private Label selectedSeatLabel;
    @FXML private ComboBox<String> seatStatusCombo;
    @FXML private Button updateSeatStatusBtn;
    @FXML private Label seatFeedbackLabel;

    private final Map<Integer, String> movieTitlesById = new HashMap<>();
    private final Map<Integer, String> roomNamesById = new HashMap<>();
    private Map<Integer, Integer> bookedCountsByShowtime = new HashMap<>();
    private Map<Integer, int[]> seatStatsByShowtime = new HashMap<>();
    private Set<Integer> bookedSeatIdsInSelectedShowtime = new HashSet<>();

    private Record.movies selectedMovie;
    private Record.showTimes selectedShowtime;
    private Record.showTimes selectedShowtimeForSeats;
    private Record.seats selectedSeat;

    @FXML
    private void initialize() {
        boolean admin = Session.isAdmin();
        editButton.setVisible(admin);
        editButton.setManaged(admin);
        userLabel.setText(Session.isLoggedIn()
                ? Session.getEmail() + " (" + Session.getRole() + ")"
                : "");

        if (!admin) {
            handleHome();
            return;
        }

        setupMoviesTab();
        setupShowtimesTab();
        setupRoomsTab();

        loadMovies();
        loadRooms();
        loadShowtimes();
    }

    // =========================================================================
    // TAB 1: QUẢN LÝ PHIM
    // =========================================================================

    private void setupMoviesTab() {
        movieIdCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().movieId()));
        movieTitleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().title()));
        movieDurationCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().durationMins()));
        movieStatusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status()));
        moviePosterCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().poster_url() != null ? d.getValue().poster_url() : ""));

        movieStatusCombo.setItems(FXCollections.observableArrayList("SHOWING", "UPCOMING", "STOPPED"));
        movieStatusCombo.setValue("SHOWING");

        moviesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateMovieForm(newVal);
            }
        });
        updateMovieActionButtons();
    }

    private void populateMovieForm(Record.movies movie) {
        selectedMovie = movie;
        selectedMovieLabel.setText("Đang chọn phim ID #" + movie.movieId());
        movieTitleField.setText(movie.title());
        movieDurationField.setText(String.valueOf(movie.durationMins()));
        moviePosterField.setText(movie.poster_url() != null ? movie.poster_url() : "");
        movieStatusCombo.setValue(movie.status());
        movieDescArea.setText(movie.description() != null ? movie.description() : "");
        hideFeedback(movieFeedbackLabel);
        updateMovieActionButtons();
    }

    private void updateMovieActionButtons() {
        boolean hasSelection = selectedMovie != null;
        updateMovieBtn.setDisable(!hasSelection);
        stopMovieBtn.setDisable(!hasSelection || "STOPPED".equals(selectedMovie.status()));
    }

    private void loadMovies() {
        try {
            List<Record.movies> list = AdminServices.getAllMovies();
            movieTitlesById.clear();
            for (Record.movies m : list) {
                movieTitlesById.put(m.movieId(), m.title());
            }
            moviesTable.setItems(FXCollections.observableArrayList(list));
            stMovieCombo.setItems(FXCollections.observableArrayList(list));
            updateEndTimePreview();
        } catch (SQLException e) {
            showFeedback(movieFeedbackLabel, "Lỗi tải danh sách phim: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleRefreshMovies() {
        loadMovies();
        hideFeedback(movieFeedbackLabel);
    }

    @FXML
    private void handleAddMovie() {
        try {
            int duration = parseDuration(movieDurationField.getText());
            AdminServices.addMovie(
                    movieTitleField.getText(),
                    movieDescArea.getText(),
                    duration,
                    moviePosterField.getText(),
                    movieStatusCombo.getValue());
            loadMovies();
            handleClearMovieForm();
            showFeedback(movieFeedbackLabel, "Đã thêm phim mới thành công.", false);
        } catch (Exception e) {
            showFeedback(movieFeedbackLabel, e.getMessage(), true);
        }
    }

    @FXML
    private void handleUpdateMovie() {
        if (selectedMovie == null) {
            showFeedback(movieFeedbackLabel, "Vui lòng chọn phim cần sửa.", true);
            return;
        }
        try {
            int duration = parseDuration(movieDurationField.getText());
            AdminServices.updateMovie(
                    selectedMovie.movieId(),
                    movieTitleField.getText(),
                    movieDescArea.getText(),
                    duration,
                    moviePosterField.getText(),
                    movieStatusCombo.getValue());
            int editedId = selectedMovie.movieId();
            loadMovies();
            loadShowtimes();
            reselectMovie(editedId);
            showFeedback(movieFeedbackLabel, "Đã cập nhật phim #" + editedId + " thành công.", false);
        } catch (Exception e) {
            showFeedback(movieFeedbackLabel, e.getMessage(), true);
        }
    }

    @FXML
    private void handleStopMovie() {
        if (selectedMovie == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Đặt trạng thái phim \"" + selectedMovie.title() + "\" thành STOPPED?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.OK) {
            return;
        }
        try {
            int stoppedId = selectedMovie.movieId();
            AdminServices.stopMovie(stoppedId);
            loadMovies();
            reselectMovie(stoppedId);
            showFeedback(movieFeedbackLabel, "Đã chuyển phim #" + stoppedId + " sang STOPPED.", false);
        } catch (SQLException e) {
            showFeedback(movieFeedbackLabel, "Lỗi: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleClearMovieForm() {
        selectedMovie = null;
        moviesTable.getSelectionModel().clearSelection();
        selectedMovieLabel.setText("Đang ở chế độ thêm phim mới");
        movieTitleField.clear();
        movieDurationField.clear();
        moviePosterField.clear();
        movieStatusCombo.setValue("SHOWING");
        movieDescArea.clear();
        hideFeedback(movieFeedbackLabel);
        updateMovieActionButtons();
    }

    private void reselectMovie(int movieId) {
        for (Record.movies item : moviesTable.getItems()) {
            if (item.movieId() == movieId) {
                moviesTable.getSelectionModel().select(item);
                return;
            }
        }
    }

    private int parseDuration(String raw) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Thời lượng phim phải là số nguyên dương.");
        }
    }

    // =========================================================================
    // TAB 2: QUẢN LÝ LỊCH CHIẾU
    // =========================================================================

    private void setupShowtimesTab() {
        stIdCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().showtimeId()));
        stMovieCol.setCellValueFactory(d -> new SimpleStringProperty(
                movieTitlesById.getOrDefault(d.getValue().movieId(), "Movie #" + d.getValue().movieId())));
        stRoomCol.setCellValueFactory(d -> new SimpleStringProperty(
                roomNamesById.getOrDefault(d.getValue().roomId(), "Room #" + d.getValue().roomId())));
        stStartCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().startTIme().format(DT_DISPLAY)));
        stEndCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().endTime().format(DT_DISPLAY)));
        stPriceCol.setCellValueFactory(d -> new SimpleStringProperty(
                CURRENCY_FORMAT.format(d.getValue().basePrice()) + " đ"));
        stStatusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status()));
        stBookedCol.setCellValueFactory(d -> new SimpleIntegerProperty(
                bookedCountsByShowtime.getOrDefault(d.getValue().showtimeId(), 0)));

        stDatePicker.setValue(LocalDate.now());
        stTimeField.setText("19:00");

        stMovieCombo.valueProperty().addListener((obs, o, n) -> updateEndTimePreview());
        stDatePicker.valueProperty().addListener((obs, o, n) -> updateEndTimePreview());
        stTimeField.textProperty().addListener((obs, o, n) -> updateEndTimePreview());

        showtimesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateShowtimeForm(newVal);
            }
        });
        updateEndTimePreview();
        updateShowtimeActionButtons();
    }

    private void populateShowtimeForm(Record.showTimes st) {
        selectedShowtime = st;
        selectedShowtimeLabel.setText("Đang chọn suất chiếu ID #" + st.showtimeId());

        stMovieCombo.getItems().stream()
                .filter(m -> m.movieId() == st.movieId())
                .findFirst()
                .ifPresent(stMovieCombo::setValue);
        stRoomCombo.getItems().stream()
                .filter(r -> r.roomID() == st.roomId())
                .findFirst()
                .ifPresent(stRoomCombo::setValue);

        stDatePicker.setValue(st.startTIme().toLocalDate());
        stTimeField.setText(st.startTIme().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        stPriceField.setText(String.valueOf((long) st.basePrice()));

        int booked = bookedCountsByShowtime.getOrDefault(st.showtimeId(), 0);
        if ("CANCELLED".equals(st.status())) {
            stWarningLabel.setText("Suất chiếu này đã bị hủy (CANCELLED), không thể chỉnh sửa.");
            stWarningLabel.setVisible(true);
            stWarningLabel.setManaged(true);
        } else if (booked > 0) {
            stWarningLabel.setText("Cảnh báo: Suất chiếu đã có " + booked
                    + " vé được đặt. Đã khóa sửa đổi để tránh làm lệch vé của khách.");
            stWarningLabel.setVisible(true);
            stWarningLabel.setManaged(true);
        } else {
            stWarningLabel.setVisible(false);
            stWarningLabel.setManaged(false);
        }

        hideFeedback(stFeedbackLabel);
        updateEndTimePreview();
        updateShowtimeActionButtons();
    }

    private void updateShowtimeActionButtons() {
        boolean hasSelection = selectedShowtime != null;
        boolean cancelled = hasSelection && "CANCELLED".equals(selectedShowtime.status());
        boolean hasBookedTickets = hasSelection
                && bookedCountsByShowtime.getOrDefault(selectedShowtime.showtimeId(), 0) > 0;

        updateShowtimeBtn.setDisable(!hasSelection || cancelled || hasBookedTickets);
        cancelShowtimeBtn.setDisable(!hasSelection || cancelled);
    }

    private void updateEndTimePreview() {
        Record.movies movie = stMovieCombo.getValue();
        LocalDate date = stDatePicker.getValue();
        String timeText = stTimeField.getText();
        if (movie == null || date == null || timeText == null || timeText.isBlank()) {
            stEndTimePreviewLabel.setText("—");
            return;
        }
        try {
            LocalTime startTime = LocalTime.parse(timeText.trim(), TIME_INPUT);
            LocalDateTime end = LocalDateTime.of(date, startTime).plusMinutes(movie.durationMins());
            stEndTimePreviewLabel.setText(end.format(DT_DISPLAY)
                    + " (tự tính +" + movie.durationMins() + " phút)");
        } catch (DateTimeParseException e) {
            stEndTimePreviewLabel.setText("Giờ bắt đầu chưa đúng định dạng HH:mm (vd: 09:30, 19:00)");
        }
    }

    private void loadShowtimes() {
        try {
            bookedCountsByShowtime = AdminServices.getBookedTicketCountsByShowtime();
            seatStatsByShowtime = AdminServices.getSeatStatsByShowtime();
            List<Record.showTimes> list = AdminServices.getAllShowtimes();
            showtimesTable.setItems(FXCollections.observableArrayList(list));
            syncRoomShowtimesTable(list);
        } catch (SQLException e) {
            showFeedback(stFeedbackLabel, "Lỗi tải lịch chiếu: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleRefreshShowtimes() {
        loadMovies();
        loadRooms();
        loadShowtimes();
        hideFeedback(stFeedbackLabel);
    }

    @FXML
    private void handleAddShowtime() {
        try {
            Record.movies movie = stMovieCombo.getValue();
            Record.rooms room = stRoomCombo.getValue();
            if (movie == null || room == null) {
                throw new IllegalArgumentException("Vui lòng chọn phim và phòng chiếu.");
            }
            LocalDateTime startTime = parseStartDateTime();
            double basePrice = parsePrice(stPriceField.getText());
            AdminServices.addShowtime(movie.movieId(), room.roomID(), startTime, basePrice);
            loadShowtimes();
            handleClearShowtimeForm();
            showFeedback(stFeedbackLabel, "Đã thêm suất chiếu mới (tự tính giờ kết thúc).", false);
        } catch (Exception e) {
            showFeedback(stFeedbackLabel, e.getMessage(), true);
        }
    }

    @FXML
    private void handleUpdateShowtime() {
        if (selectedShowtime == null) {
            return;
        }
        try {
            Record.movies movie = stMovieCombo.getValue();
            Record.rooms room = stRoomCombo.getValue();
            if (movie == null || room == null) {
                throw new IllegalArgumentException("Vui lòng chọn phim và phòng chiếu.");
            }
            LocalDateTime startTime = parseStartDateTime();
            double basePrice = parsePrice(stPriceField.getText());
            int id = selectedShowtime.showtimeId();
            AdminServices.updateShowtime(id, movie.movieId(), room.roomID(), startTime, basePrice);
            loadShowtimes();
            reselectShowtime(id);
            showFeedback(stFeedbackLabel, "Đã cập nhật suất chiếu #" + id + ".", false);
        } catch (Exception e) {
            showFeedback(stFeedbackLabel, e.getMessage(), true);
        }
    }

    @FXML
    private void handleCancelShowtime() {
        if (selectedShowtime == null) {
            return;
        }
        int booked = bookedCountsByShowtime.getOrDefault(selectedShowtime.showtimeId(), 0);
        String msg = booked > 0
                ? "Suất chiếu #" + selectedShowtime.showtimeId() + " hiện đã có "
                  + booked
                  + " vé được đặt!\nHủy suất sẽ đặt trạng thái thành CANCELLED (giữ lại dữ liệu vé). Tiếp tục?"
                : "Hủy suất chiếu #" + selectedShowtime.showtimeId() + " (đặt status = CANCELLED)?";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.OK) {
            return;
        }
        try {
            int id = selectedShowtime.showtimeId();
            AdminServices.cancelShowtime(id);
            loadShowtimes();
            reselectShowtime(id);
            showFeedback(stFeedbackLabel, "Đã hủy suất chiếu #" + id + " (CANCELLED).", false);
        } catch (SQLException e) {
            showFeedback(stFeedbackLabel, "Lỗi: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleClearShowtimeForm() {
        selectedShowtime = null;
        showtimesTable.getSelectionModel().clearSelection();
        selectedShowtimeLabel.setText("Đang ở chế độ thêm suất chiếu mới");
        stDatePicker.setValue(LocalDate.now());
        stTimeField.setText("19:00");
        stPriceField.clear();
        stWarningLabel.setVisible(false);
        stWarningLabel.setManaged(false);
        hideFeedback(stFeedbackLabel);
        updateEndTimePreview();
        updateShowtimeActionButtons();
    }

    private void reselectShowtime(int showtimeId) {
        for (Record.showTimes item : showtimesTable.getItems()) {
            if (item.showtimeId() == showtimeId) {
                showtimesTable.getSelectionModel().select(item);
                return;
            }
        }
    }

    private LocalDateTime parseStartDateTime() {
        LocalDate date = stDatePicker.getValue();
        if (date == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày chiếu.");
        }
        String rawTime = stTimeField.getText();
        if (rawTime == null || rawTime.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập giờ bắt đầu (HH:mm).");
        }
        try {
            LocalTime time = LocalTime.parse(rawTime.trim(), TIME_INPUT);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Giờ bắt đầu phải theo định dạng HH:mm (ví dụ: 09:30 hoặc 19:00).");
        }
    }

    private double parsePrice(String raw) {
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá vé phải là số hợp lệ.");
        }
    }

    // =========================================================================
    // TAB 3: QUẢN LÝ PHÒNG & GHẾ THEO SUẤT CHIẾU
    // =========================================================================

    private void setupRoomsTab() {
        rsIdCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().showtimeId()));
        rsMovieCol.setCellValueFactory(d -> new SimpleStringProperty(
                movieTitlesById.getOrDefault(d.getValue().movieId(), "Movie #" + d.getValue().movieId())));
        rsRoomCol.setCellValueFactory(d -> new SimpleStringProperty(
                roomNamesById.getOrDefault(d.getValue().roomId(), "Room #" + d.getValue().roomId())));
        rsStartCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().startTIme().format(DT_DISPLAY)));
        rsStatusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status()));
        rsTotalSeatsCol.setCellValueFactory(d -> new SimpleIntegerProperty(
                seatStatsByShowtime.getOrDefault(d.getValue().showtimeId(), new int[3])[0]));
        rsBookedSeatsCol.setCellValueFactory(d -> new SimpleIntegerProperty(
                seatStatsByShowtime.getOrDefault(d.getValue().showtimeId(), new int[3])[1]));
        rsEmptySeatsCol.setCellValueFactory(d -> new SimpleIntegerProperty(
                seatStatsByShowtime.getOrDefault(d.getValue().showtimeId(), new int[3])[2]));

        seatStatusCombo.setItems(FXCollections.observableArrayList("AVAILABLE", "MAINTENANCE", "UNAVAILABLE"));
        seatStatusCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(String status) {
                return formatSeatStatus(status);
            }

            @Override
            public String fromString(String label) {
                return label;
            }
        });
        seatStatusCombo.setValue("MAINTENANCE");
        updateSeatStatusBtn.setDisable(true);

        roomShowtimesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedShowtimeForSeats = newVal;
                loadSeatsForShowtime(newVal);
            }
        });
    }

    private void loadRooms() {
        try {
            List<Record.rooms> rooms = AdminServices.getAllRooms();
            roomNamesById.clear();
            for (Record.rooms r : rooms) {
                roomNamesById.put(r.roomID(), r.roomName());
            }
            stRoomCombo.setItems(FXCollections.observableArrayList(rooms));
        } catch (SQLException e) {
            showFeedback(seatFeedbackLabel, "Lỗi tải danh sách phòng: " + e.getMessage(), true);
        }
    }

    private void syncRoomShowtimesTable(List<Record.showTimes> showtimes) {
        Integer keepId = selectedShowtimeForSeats != null ? selectedShowtimeForSeats.showtimeId() : null;
        roomShowtimesTable.setItems(FXCollections.observableArrayList(showtimes));
        if (keepId != null) {
            for (Record.showTimes st : showtimes) {
                if (st.showtimeId() == keepId) {
                    roomShowtimesTable.getSelectionModel().select(st);
                    return;
                }
            }
        }
        if (!showtimes.isEmpty()) {
            roomShowtimesTable.getSelectionModel().selectFirst();
        } else {
            selectedShowtimeForSeats = null;
            seatsGrid.getChildren().clear();
            roomSeatsTitleLabel.setText("Sơ đồ ghế theo suất chiếu (chưa có suất chiếu)");
            roomSeatsSummaryLabel.setVisible(false);
            roomSeatsSummaryLabel.setManaged(false);
        }
    }

    private void loadSeatsForShowtime(Record.showTimes showtime) {
        seatsGrid.getChildren().clear();
        selectedSeat = null;
        selectedSeatLabel.setText("Chưa chọn ghế nào.");
        updateSeatStatusBtn.setDisable(true);

        String movieTitle = movieTitlesById.getOrDefault(showtime.movieId(), "Movie #" + showtime.movieId());
        String roomName = roomNamesById.getOrDefault(showtime.roomId(), "Room #" + showtime.roomId());
        roomSeatsTitleLabel.setText(String.format("Suất #%d — %s | %s (%s)",
                showtime.showtimeId(), movieTitle, roomName, showtime.startTIme().format(DT_DISPLAY)));

        try {
            List<Record.seats> seats = AdminServices.getSeatsByRoom(showtime.roomId());
            bookedSeatIdsInSelectedShowtime = AdminServices.getBookedSeatIdsByShowtime(showtime.showtimeId());

            int totalSeats = seats.size();
            int availSeats = 0;
            int unavailSeats = 0;
            int maintSeats = 0;

            Map<String, Integer> rowIndexMap = new LinkedHashMap<>();
            int nextRow = 0;

            for (Record.seats seat : seats) {
                boolean isBooked = bookedSeatIdsInSelectedShowtime.contains(seat.seatId());
                if (isBooked || "UNAVAILABLE".equals(seat.status())) {
                    unavailSeats++;
                } else if ("MAINTENANCE".equals(seat.status())) {
                    maintSeats++;
                } else {
                    availSeats++;
                }

                Integer rowIdx = rowIndexMap.get(seat.seatRow());
                if (rowIdx == null) {
                    rowIdx = nextRow++;
                    rowIndexMap.put(seat.seatRow(), rowIdx);
                    Label rowLabel = new Label(seat.seatRow());
                    rowLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 22;");
                    seatsGrid.add(rowLabel, 0, rowIdx);
                }

                String code = seat.seatRow() + seat.seatNumber();
                Button seatBtn = new Button(code);
                seatBtn.setMinWidth(52.0);
                seatBtn.setMinHeight(36.0);
                seatBtn.setStyle(getSeatButtonStyle(seat, isBooked));
                String tooltipStatus = isBooked
                        ? "UNAVAILABLE (Đã đặt vé ở suất này)"
                        : formatSeatStatus(seat.status());
                seatBtn.setTooltip(new Tooltip(String.format("%s (%s) — %s",
                        code,
                        seat.seatType(),
                        tooltipStatus)));
                seatBtn.setOnAction(e -> selectSeat(seat, isBooked));
                seatsGrid.add(seatBtn, seat.seatNumber(), rowIdx);
            }

            roomSeatsSummaryLabel.setText(String.format(
                    "AVAILABLE (Chưa đặt): %d/%d  |  UNAVAILABLE (Đã đặt): %d  |  MAINTENANCE (Bảo trì): %d",
                    availSeats, totalSeats, unavailSeats, maintSeats));
            roomSeatsSummaryLabel.setVisible(true);
            roomSeatsSummaryLabel.setManaged(true);
        } catch (SQLException e) {
            showFeedback(seatFeedbackLabel, "Lỗi tải sơ đồ ghế theo suất chiếu: " + e.getMessage(), true);
        }
    }

    private void selectSeat(Record.seats seat, boolean isBooked) {
        selectedSeat = seat;
        String code = seat.seatRow() + seat.seatNumber();
        String statusText = isBooked
                ? "UNAVAILABLE (Đã đặt vé ở suất này) | Mặc định phòng: " + formatSeatStatus(seat.status())
                : formatSeatStatus(seat.status());
        selectedSeatLabel.setText(String.format("Ghế %s (%s) — %s",
                code,
                seat.seatType(),
                statusText));
        seatStatusCombo.setValue(seat.status());
        updateSeatStatusBtn.setDisable(false);
        hideFeedback(seatFeedbackLabel);
    }

    private static String formatSeatStatus(String status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case "AVAILABLE" -> "AVAILABLE (Chưa đặt)";
            case "UNAVAILABLE" -> "UNAVAILABLE (Đã đặt)";
            case "MAINTENANCE" -> "MAINTENANCE (Bảo trì)";
            default -> status;
        };
    }

    private String getSeatButtonStyle(Record.seats seat, boolean isBooked) {
        if (isBooked || "UNAVAILABLE".equals(seat.status())) {
            return "-fx-background-color: #ffcdd2; -fx-border-color: #c62828; "
                    + "-fx-text-fill: #b71c1c; -fx-font-weight: bold; -fx-background-radius: 4; -fx-border-radius: 4;";
        }
        if ("MAINTENANCE".equals(seat.status())) {
            return "-fx-background-color: #ffe0b2; -fx-border-color: #ef6c00; "
                    + "-fx-text-fill: #e65100; -fx-font-weight: bold; -fx-background-radius: 4; -fx-border-radius: 4;";
        }
        return "VIP".equals(seat.seatType())
                ? "-fx-background-color: #e3f2fd; -fx-border-color: #1565c0; "
                  + "-fx-text-fill: #0d47a1; -fx-font-weight: bold; -fx-background-radius: 4; -fx-border-radius: 4;"
                : "-fx-background-color: #e8f5e9; -fx-border-color: #2e7d32; "
                  + "-fx-text-fill: #1b5e20; -fx-font-weight: bold; -fx-background-radius: 4; -fx-border-radius: 4;";
    }

    @FXML
    private void handleRefreshRooms() {
        loadMovies();
        loadRooms();
        loadShowtimes();
        hideFeedback(seatFeedbackLabel);
    }

    @FXML
    private void handleUpdateSeatStatus() {
        if (selectedSeat == null || selectedShowtimeForSeats == null) {
            return;
        }
        String newStatus = seatStatusCombo.getValue();
        String code = selectedSeat.seatRow() + selectedSeat.seatNumber();
        boolean isBooked = bookedSeatIdsInSelectedShowtime.contains(selectedSeat.seatId());

        if (isBooked && !"AVAILABLE".equals(newStatus)) {
            String roomName = roomNamesById.getOrDefault(
                    selectedShowtimeForSeats.roomId(), "Room #" + selectedShowtimeForSeats.roomId());
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Ghế " + code + " đã có vé được đặt ở suất chiếu #"
                            + selectedShowtimeForSeats.showtimeId() + "!\n"
                            + "Đổi trạng thái ghế thành " + formatSeatStatus(newStatus) + " sẽ áp dụng cho " + roomName
                            + ". Bạn có chắc chắn muốn tiếp tục?",
                    ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText(null);
            Optional<ButtonType> choice = confirm.showAndWait();
            if (choice.isEmpty() || choice.get() != ButtonType.OK) {
                return;
            }
        }

        try {
            AdminServices.updateSeatStatus(selectedSeat.seatId(), newStatus);
            loadShowtimes();
            showFeedback(seatFeedbackLabel,
                    "Đã cập nhật ghế " + code + " thành " + formatSeatStatus(newStatus) + ".", false);
        } catch (Exception e) {
            showFeedback(seatFeedbackLabel, e.getMessage(), true);
        }
    }

    // =========================================================================
    // ĐIỀU HƯỚNG & TIỆN ÍCH CHUNG
    // =========================================================================

    private void showFeedback(Label label, String message, boolean error) {
        label.setText(message);
        label.setStyle(error
                ? "-fx-text-fill: #c62828; -fx-font-weight: bold;"
                : "-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        label.setVisible(true);
        label.setManaged(true);
    }

    private void hideFeedback(Label label) {
        label.setVisible(false);
        label.setManaged(false);
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
        try {
            App.setRoot("account");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        handleRefreshShowtimes();
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
