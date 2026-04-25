package com.cinephile.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

public class Show {
    private int id;
    private int movieId;
    private LocalDate showDate;
    private LocalTime showTime;
    private BigDecimal basePrice;
    private String hallName;

    // Additional lookup fields for UI convenience
    private String movieTitle;

    public Show() {
    }

    public Show(int id, int movieId, LocalDate showDate, LocalTime showTime, BigDecimal basePrice, String hallName) {
        this.id = id;
        this.movieId = movieId;
        this.showDate = showDate;
        this.showTime = showTime;
        this.basePrice = basePrice;
        this.hallName = hallName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public LocalTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalTime showTime) {
        this.showTime = showTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    @Override
    public String toString() {
        return showTime.toString() + " - " + hallName;
    }
}
