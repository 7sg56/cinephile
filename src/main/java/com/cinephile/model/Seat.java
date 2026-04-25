package com.cinephile.model;

public class Seat {
    private int id;
    private int bookingId;
    private int showId;
    private String seatNumber;
    private boolean isLounge;

    public Seat() {
    }

    public Seat(int showId, String seatNumber, boolean isLounge) {
        this.showId = showId;
        this.seatNumber = seatNumber;
        this.isLounge = isLounge;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isLounge() {
        return isLounge;
    }

    public void setLounge(boolean lounge) {
        isLounge = lounge;
    }
}
