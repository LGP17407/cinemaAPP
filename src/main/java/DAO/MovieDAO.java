
package DAO;

import db.DB_CONNECTION;
import model.ModelMovies;
import model.ModelMovies.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    /**
     * Phim theo trạng thái (enum Status trong ModelMovies),
     * khớp schema bảng movies (movie_id, title, description, duration_mins, poster_url, status, created_at).
     */
    public List<ModelMovies> findByStatus(Status status) throws SQLException {

        String sql = "SELECT movie_id, title, description, duration_mins, status, poster_url, created_at "
                   + "FROM movies WHERE status = ? ORDER BY created_at DESC, movie_id DESC";

        List<ModelMovies> list = new ArrayList<>();

        Connection c = DB_CONNECTION.getInstance().createConnection();
        if (c == null) {
            throw new SQLException("Không thể kết nối cơ sở dữ liệu.");
        }

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status.name());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp created = rs.getTimestamp("created_at");
                    String rawStatus = rs.getString("status");
                    Status movieStatus = rawStatus != null
                            ? Status.valueOf(rawStatus.trim().toUpperCase())
                            : status;

                    list.add(new ModelMovies(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("duration_mins"),
                        movieStatus,
                        null,
                        rs.getString("poster_url"),
                        false,
                        created != null ? created.toLocalDateTime() : null
                    ));
                }
            }
        }

        return list;
    }
}
