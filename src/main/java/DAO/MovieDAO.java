
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
     * bỏ qua phim đã xoá mềm.
     */
    public List<ModelMovies> findByStatus(Status status) throws SQLException {

        String sql = "SELECT movie_id, title, description, duration_mins, status, genre, poster_url, is_deleted, created_at "
                   + "FROM movies WHERE status=? AND is_deleted=0 ORDER BY created_at DESC";

        List<ModelMovies> list = new ArrayList<>();

        try (Connection c = DB_CONNECTION.getInstance().createConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status.name());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp created = rs.getTimestamp("created_at");

                    list.add(new ModelMovies(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("duration_mins"),
                        Status.valueOf(rs.getString("status")),
                        rs.getString("genre"),
                        rs.getString("poster_url"),
                        rs.getBoolean("is_deleted"),
                        created != null ? created.toLocalDateTime() : null
                    ));
                }
            }
        }

        return list;
    }
}
