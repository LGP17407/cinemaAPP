
package DAO;

import db.DB_CONNECTION;
import model.ModelSeats;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    /**
     * Toàn bộ ghế của một phòng (dùng để vẽ sơ đồ ghế).
     */
    public List<ModelSeats> findByRoom(int roomId) throws SQLException {

        String sql = "SELECT seat_id, room_id, seat_row, seat_number, seat_type, status "
                   + "FROM seats WHERE room_id=? ORDER BY seat_row, seat_number";

        List<ModelSeats> list = new ArrayList<>();

        Connection c = DB_CONNECTION.getInstance().createConnection();
        if (c == null) {
            throw new SQLException("Không thể kết nối cơ sở dữ liệu.");
        }

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ModelSeats(
                        rs.getInt("seat_id"),
                        rs.getInt("room_id"),
                        rs.getString("seat_row"),
                        rs.getInt("seat_number"),
                        rs.getString("seat_type"),
                        rs.getString("status")
                    ));
                }
            }
        }

        return list;
    }
}
