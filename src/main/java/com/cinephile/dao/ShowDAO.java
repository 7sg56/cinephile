package com.cinephile.dao;

import com.cinephile.model.Show;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShowDAO {

    public List<Show> getShowsForMovie(int movieId) {
        List<Show> shows = new ArrayList<>();
        String query = "SELECT s.*, m.title as movie_title FROM shows s " +
                "JOIN movies m ON s.movie_id = m.id " +
                "WHERE s.movie_id = ? AND s.show_date >= CURRENT_DATE " +
                "ORDER BY s.show_date ASC, s.show_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    shows.add(mapResultSetToShow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shows;
    }

    private Show mapResultSetToShow(ResultSet rs) throws SQLException {
        Show show = new Show();
        show.setId(rs.getInt("id"));
        show.setMovieId(rs.getInt("movie_id"));
        show.setShowDate(rs.getDate("show_date").toLocalDate());
        show.setShowTime(rs.getTime("show_time").toLocalTime());
        show.setBasePrice(rs.getBigDecimal("base_price"));
        show.setHallName(rs.getString("hall_name"));
        show.setMovieTitle(rs.getString("movie_title"));
        return show;
    }
}
