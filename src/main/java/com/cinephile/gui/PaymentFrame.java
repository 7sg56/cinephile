package com.cinephile.gui;

import com.cinephile.dao.BookingDAO;
import com.cinephile.model.Booking;
import com.cinephile.model.Seat;
import com.cinephile.model.Show;
import com.cinephile.model.User;
import com.cinephile.util.PDFGenerator;
import com.cinephile.util.Theme;
import com.cinephile.gui.components.ModernButton;
import com.cinephile.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PaymentFrame extends JFrame {
    private User user;
    private Show show;
    private Booking booking;
    private List<Seat> seats;
    private BookingDAO bookingDAO;

    public PaymentFrame(User user, Show show, Booking booking, List<Seat> seats) {
        this.user = user;
        this.show = show;
        this.booking = booking;
        this.seats = seats;
        this.bookingDAO = new BookingDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Payment Summary");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Theme.PANEL_DARK);

        JLabel title = new JLabel("Checkout Summary");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Split panel: Receipt Details on Left, Payment methods on Right
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        centerPanel.setOpaque(false);

        // -- Left: Receipt Box --
        RoundedPanel receiptPanel = new RoundedPanel(20, Theme.PANEL_DARK);
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
        receiptPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblReceipt = new JLabel("Your Order Details:");
        lblReceipt.setFont(Theme.FONT_HEADER);
        lblReceipt.setForeground(Theme.PRIMARY);

        receiptPanel.add(lblReceipt);
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        receiptPanel.add(createRow("Tickets Subtotal:", "$" + booking.getTicketsSubtotal()));
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        receiptPanel.add(createRow("Food/Amenities:", "$" + booking.getFoodSubtotal()));
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        receiptPanel.add(createRow("Taxes (10%):", "$" + booking.getTaxes()));
        receiptPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblGrandTotal = new JLabel("Grand Total: $" + booking.getGrandTotal());
        lblGrandTotal.setFont(Theme.FONT_TITLE);
        lblGrandTotal.setForeground(Theme.TEXT_PRIMARY);
        receiptPanel.add(lblGrandTotal);

        // -- Right: Payment methods --
        RoundedPanel payPanel = new RoundedPanel(20, Theme.PANEL_DARK);
        payPanel.setLayout(new BoxLayout(payPanel, BoxLayout.Y_AXIS));
        payPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblPay = new JLabel("Select Payment Method");
        lblPay.setFont(Theme.FONT_HEADER);
        lblPay.setForeground(Theme.TEXT_PRIMARY);
        lblPay.setAlignmentX(Component.CENTER_ALIGNMENT);
        payPanel.add(lblPay);
        payPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        ModernButton btnCard = new ModernButton("Credit / Debit Card");
        btnCard.setMaximumSize(new Dimension(300, 50));
        btnCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton btnUpi = new ModernButton("UPI / Wallet");
        btnUpi.setMaximumSize(new Dimension(300, 50));
        btnUpi.setAlignmentX(Component.CENTER_ALIGNMENT);

        payPanel.add(btnCard);
        payPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        payPanel.add(btnUpi);

        centerPanel.add(receiptPanel);
        centerPanel.add(payPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Add action listeners to simulate success
        btnCard.addActionListener(e -> processPayment("CARD"));
        btnUpi.addActionListener(e -> processPayment("UPI"));
    }

    private JPanel createRow(String label, String value) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        r.setMaximumSize(new Dimension(400, 30));
        JLabel l = new JLabel(label);
        l.setForeground(Theme.TEXT_SECONDARY);
        l.setFont(Theme.FONT_REGULAR);
        JLabel v = new JLabel(value);
        v.setForeground(Theme.TEXT_PRIMARY);
        v.setFont(Theme.FONT_REGULAR);
        r.add(l, BorderLayout.WEST);
        r.add(v, BorderLayout.EAST);
        return r;
    }

    private void processPayment(String method) {
        // Here we simulate external gateway logic
        int confirm = JOptionPane.showConfirmDialog(this,
                "Simulating " + method + " gateway...\nConfirm payment of $" + booking.getGrandTotal() + "?",
                "Payment Simulator", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Save Booking and Seats Atomically to Database using DAO
            if (bookingDAO.createBookingWithSeats(booking, seats)) {
                // Generate PDF File
                try {
                    String pdfPath = PDFGenerator.generateTicketReceipt(user, show, booking, seats, method);
                    JOptionPane.showMessageDialog(this,
                            "Booking Confirmed!\nBooking ID: " + booking.getBookingReference() +
                                    "\nTickets generated and saved at: " + pdfPath);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Booking confirmed but failed to generate PDF receipt.");
                }

                // Return to client dashboard
                new ClientDashboardFrame(user).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "A database error occurred while saving your booking. Please try again.");
            }
        }
    }
}
