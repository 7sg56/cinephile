package com.cinephile.dao;

import com.cinephile.model.Amenity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmenityDAO {

    public List<Amenity> getAllActiveAmenities() {
        List<Amenity> items = new ArrayList<>();
        String query = "SELECT * FROM amenities WHERE is_active = TRUE ORDER BY category, price DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Amenity amenity = new Amenity();
                amenity.setId(rs.getInt("id"));
                amenity.setName(rs.getString("name"));
                amenity.setCategory(rs.getString("category"));
                amenity.setPrice(rs.getBigDecimal("price"));
                amenity.setActive(rs.getBoolean("is_active"));
                items.add(amenity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
