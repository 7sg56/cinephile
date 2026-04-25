package com.cinephile.dao;

import com.cinephile.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public List<Movie> getAllActiveMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies WHERE is_active = TRUE ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public boolean addMovie(Movie movie) {
        String query = "INSERT INTO movies (title, genre, duration_minutes, rating, language, description, poster_path, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getDurationMinutes());
            stmt.setString(4, movie.getRating());
            stmt.setString(5, movie.getLanguage());
            stmt.setString(6, movie.getDescription());
            stmt.setString(7, movie.getPosterPath());
            stmt.setBoolean(8, movie.isActive());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMovie(int id) {
        // We usually just soft-delete rather than actually deleting standard movies
        String query = "UPDATE movies SET is_active = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        movie.setGenre(rs.getString("genre"));
        movie.setDurationMinutes(rs.getInt("duration_minutes"));
        movie.setRating(rs.getString("rating"));
        movie.setLanguage(rs.getString("language"));
        movie.setDescription(rs.getString("description"));
        movie.setPosterPath(rs.getString("poster_path"));
        movie.setActive(rs.getBoolean("is_active"));
        movie.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return movie;
    }
}
