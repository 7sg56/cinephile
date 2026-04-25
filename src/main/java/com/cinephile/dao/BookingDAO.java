package com.cinephile.dao;

import com.cinephile.model.Booking;
import com.cinephile.model.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public List<String> getBookedSeatNumbersForShow(int showId) {
        List<String> bookedSeats = new ArrayList<>();
        String query = "SELECT seat_number FROM seats WHERE show_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, showId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookedSeats.add(rs.getString("seat_number"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedSeats;
    }

    public boolean createBookingWithSeats(Booking booking, List<Seat> seats) {
        String insertBooking = "INSERT INTO bookings (user_id, show_id, booking_reference, status, total_tickets, tickets_subtotal, food_subtotal, taxes, grand_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertSeat = "INSERT INTO seats (booking_id, show_id, seat_number, is_lounge) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction bounds!

            try (PreparedStatement bStmt = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS)) {
                bStmt.setInt(1, booking.getUserId());
                bStmt.setInt(2, booking.getShowId());
                bStmt.setString(3, booking.getBookingReference());
                bStmt.setString(4, booking.getStatus());
                bStmt.setInt(5, booking.getTotalTickets());
                bStmt.setBigDecimal(6, booking.getTicketsSubtotal());
                bStmt.setBigDecimal(7, booking.getFoodSubtotal());
                bStmt.setBigDecimal(8, booking.getTaxes());
                bStmt.setBigDecimal(9, booking.getGrandTotal());
                bStmt.executeUpdate();

                try (ResultSet rs = bStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedBookingId = rs.getInt(1);
                        booking.setId(generatedBookingId);

                        // Insert Seats
                        try (PreparedStatement sStmt = conn.prepareStatement(insertSeat)) {
                            for (Seat seat : seats) {
                                sStmt.setInt(1, generatedBookingId);
                                sStmt.setInt(2, seat.getShowId());
                                sStmt.setString(3, seat.getSeatNumber());
                                sStmt.setBoolean(4, seat.isLounge());
                                sStmt.addBatch();
                            }
                            sStmt.executeBatch();
                        }
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
