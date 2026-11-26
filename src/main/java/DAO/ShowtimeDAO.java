
package DAO;

import db.DB_CONNECTION;
import model.ModelShowtimes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeDAO {

    /**
     * Lấy danh sách suất chiếu sắp tới của một bộ phim kèm tên phòng và số ghế trống.
     */
    public List<ModelShowtimes> findUpcomingByMovie(int movieId)
            throws SQLException {

        List<ModelShowtimes> list = new ArrayList<>();

        String sql = "SELECT st.showtime_id, st.movie_id, st.room_id, r.room_name, "
                   + "st.start_time, st.end_time, st.base_price, "
                   + "st.is_deleted, st.created_at, "
                   + "(SELECT COUNT(*) FROM seats s "
                   + " WHERE s.room_id = st.room_id "
                   + "   AND s.status = 'AVAILABLE' "
                   + "   AND NOT EXISTS ("
                   + "       SELECT 1 FROM tickets t "
                   + "       WHERE t.showtime_id = st.showtime_id AND t.seat_id = s.seat_id"
                   + "   )) AS available_seats "
                   + "FROM showtimes st "
                   + "JOIN rooms r ON st.room_id = r.room_id "
                   + "WHERE st.movie_id = ? "
                   + "AND st.is_deleted = 0 "
                   + "AND st.status = 'ACTIVE' "
                   + "AND st.start_time > NOW() "
                   + "ORDER BY st.start_time ASC";

        Connection c = DB_CONNECTION.getInstance().createConnection();
        if (c == null) {
            throw new SQLException("Không thể kết nối cơ sở dữ liệu.");
        }

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Timestamp startTs = rs.getTimestamp("start_time");
                    Timestamp endTs = rs.getTimestamp("end_time");
                    Timestamp createdTs = rs.getTimestamp("created_at");

                    ModelShowtimes showtime = new ModelShowtimes(
                        rs.getInt("showtime_id"),
                        rs.getInt("movie_id"),
                        rs.getInt("room_id"),
                        startTs != null ? startTs.toLocalDateTime().toString() : rs.getString("start_time"),
                        endTs != null ? endTs.toLocalDateTime().toString() : rs.getString("end_time"),
                        rs.getDouble("base_price"),
                        rs.getBoolean("is_deleted"),
                        createdTs != null ? createdTs.toLocalDateTime().toString() : rs.getString("created_at")
                    );
                    showtime.setRoomName(rs.getString("room_name"));
                    showtime.setAvailableSeats(rs.getInt("available_seats"));

                    list.add(showtime);
                }
            }
        }

        return list;
    }
}
