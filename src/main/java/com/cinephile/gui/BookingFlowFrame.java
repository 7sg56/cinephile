package com.cinephile.gui;

import com.cinephile.dao.BookingDAO;
import com.cinephile.model.Show;
import com.cinephile.model.User;
import com.cinephile.util.Theme;
import com.cinephile.gui.components.ModernButton;
import com.cinephile.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookingFlowFrame extends JFrame {
    private User user;
    private Show show;
    private BookingDAO bookingDAO;
    private List<String> bookedSeats;
    private List<String> selectedSeats;

    private JLabel lblTotalCount;
    private JLabel lblPriceEstimate;

    public BookingFlowFrame(User user, Show show) {
        this.user = user;
        this.show = show;
        this.bookingDAO = new BookingDAO();
        this.selectedSeats = new ArrayList<>();
        this.bookedSeats = bookingDAO.getBookedSeatNumbersForShow(show.getId());
        initUI();
    }

    private void initUI() {
        setTitle("Select Seats - " + show.getMovieTitle());
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Theme.PANEL_DARK);

        JLabel title = new JLabel(
                "Screening: " + show.getMovieTitle() + " | " + show.getShowTime() + " | " + show.getHallName());
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Seat Grid Panel
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Screen visual
        JLabel screenLabel = new JLabel("S C R E E N", SwingConstants.CENTER);
        screenLabel.setFont(Theme.FONT_SMALL);
        screenLabel.setForeground(Theme.BORDER_COLOR);
        screenLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Theme.BORDER_COLOR));
        gridWrapper.add(screenLabel, BorderLayout.NORTH);

        JPanel seatsGrid = new JPanel(new GridLayout(6, 10, 10, 10)); // 6 rows, 10 cols
        seatsGrid.setOpaque(false);
        seatsGrid.setBorder(new EmptyBorder(30, 0, 0, 0));

        char[] rows = { 'A', 'B', 'C', 'D', 'E', 'F' };
        for (char r : rows) {
            for (int c = 1; c <= 10; c++) {
                String seatNo = "" + r + c;
                seatsGrid.add(createSeatButton(seatNo));
            }
        }
        gridWrapper.add(seatsGrid, BorderLayout.CENTER);
        add(gridWrapper, BorderLayout.CENTER);

        // Checkout Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.PANEL_DARK);
        footer.setBorder(new EmptyBorder(15, 30, 15, 30));

        JPanel stats = new JPanel(new GridLayout(2, 1));
        stats.setOpaque(false);
        lblTotalCount = new JLabel("Selected Seats: 0");
        lblTotalCount.setForeground(Theme.TEXT_SECONDARY);
        lblPriceEstimate = new JLabel("Subtotal: $0.00");
        lblPriceEstimate.setFont(Theme.FONT_HEADER);
        lblPriceEstimate.setForeground(Theme.PRIMARY);
        stats.add(lblTotalCount);
        stats.add(lblPriceEstimate);

        ModernButton btnProceed = new ModernButton("Proceed to Food & Checkout");
        btnProceed.setPreferredSize(new Dimension(250, 45));
        btnProceed.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one seat.");
                return;
            }
            int count = selectedSeats.size();
            BigDecimal subtotal = show.getBasePrice().multiply(new BigDecimal(count));
            new FoodCheckoutFrame(user, show, selectedSeats, subtotal).setVisible(true);
            dispose();
        });

        footer.add(stats, BorderLayout.WEST);
        footer.add(btnProceed, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private JButton createSeatButton(String seatNumber) {
        JButton btn = new JButton(seatNumber);
        btn.setFont(Theme.FONT_SMALL);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR));

        boolean isBooked = bookedSeats.contains(seatNumber);
        if (isBooked) {
            btn.setBackground(Theme.BORDER_COLOR);
            btn.setForeground(Color.DARK_GRAY); // Disabled look
            btn.setEnabled(false);
        } else {
            btn.setBackground(Theme.INPUT_BG);
            btn.setForeground(Theme.TEXT_PRIMARY);
            btn.addActionListener(e -> toggleSeat(btn, seatNumber));
        }
        return btn;
    }

    private void toggleSeat(JButton btn, String seatNumber) {
        if (selectedSeats.contains(seatNumber)) {
            selectedSeats.remove(seatNumber);
            btn.setBackground(Theme.INPUT_BG);
            btn.setForeground(Theme.TEXT_PRIMARY);
        } else {
            selectedSeats.add(seatNumber);
            btn.setBackground(Theme.PRIMARY); // highlighted in Netflix red
            btn.setForeground(Color.WHITE);
        }
        updateTotals();
    }

    private void updateTotals() {
        int count = selectedSeats.size();
        BigDecimal subtotal = show.getBasePrice().multiply(new BigDecimal(count));
        lblTotalCount.setText("Selected Seats: " + count + " (" + String.join(", ", selectedSeats) + ")");
        lblPriceEstimate.setText("Subtotal: $" + subtotal.toString());
    }
}
