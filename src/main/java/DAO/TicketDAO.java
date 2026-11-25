
package DAO;

import db.DB_CONNECTION;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TicketDAO {

    /**
     * ID các ghế đã đặt của một suất chiếu
     * (để tô xám trên sơ đồ ghế).
     */
    public Set<Integer> getBookedSeatIds(int showtimeId) throws SQLException {

        Set<Integer> ids = new HashSet<>();

        String sql = "SELECT seat_id FROM tickets WHERE showtime_id=?";

        try (Connection c = DB_CONNECTION.getInstance().createConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, showtimeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        }

        return ids;
    }

    /**
     * Đặt vé trong 1 transaction:
     * Thành công -> commit.
     * Có lỗi -> rollback.
     *
     * Giá lấy từ DB (showtimes.base_price).
     */
    public void bookTickets(int userId, int showtimeId,
                            List<Integer> seatIds) throws SQLException {

        String sql = "INSERT INTO tickets(user_id, showtime_id, seat_id, price) "
                   + "SELECT ?, st.showtime_id, s.seat_id, st.base_price "
                   + "FROM showtimes st "
                   + "JOIN seats s ON s.room_id = st.room_id "
                   + "WHERE st.showtime_id=? "
                   + "AND st.is_deleted=0 "
                   + "AND st.start_time > NOW() "
                   + "AND s.seat_id=? "
                   + "AND s.status='AVAILABLE'";

        try (Connection c = DB_CONNECTION.getInstance().createConnection()) {

            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(sql)) {

                for (int seatId : seatIds) {

                    ps.setInt(1, userId);
                    ps.setInt(2, showtimeId);
                    ps.setInt(3, seatId);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                            "Suất chiếu hoặc ghế không còn hợp lệ.",
                            "45000"
                        );
                    }
                }

                c.commit();

            } catch (SQLException e) {

                try {
                    c.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }
}
