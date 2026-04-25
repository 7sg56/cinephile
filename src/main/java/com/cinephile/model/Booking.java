package com.cinephile.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int userId;
    private int showId;
    private String bookingReference;
    private String status; // CONFIRMED, CANCELLED
    private int totalTickets;
    private BigDecimal ticketsSubtotal;
    private BigDecimal foodSubtotal;
    private BigDecimal taxes;
    private BigDecimal grandTotal;
    private LocalDateTime createdAt;

    public Booking() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public BigDecimal getTicketsSubtotal() {
        return ticketsSubtotal;
    }

    public void setTicketsSubtotal(BigDecimal ticketsSubtotal) {
        this.ticketsSubtotal = ticketsSubtotal;
    }

    public BigDecimal getFoodSubtotal() {
        return foodSubtotal;
    }

    public void setFoodSubtotal(BigDecimal foodSubtotal) {
        this.foodSubtotal = foodSubtotal;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
