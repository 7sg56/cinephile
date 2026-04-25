package com.cinephile.gui;

import com.cinephile.dao.AmenityDAO;
import com.cinephile.model.Amenity;
import com.cinephile.model.Booking;
import com.cinephile.model.Seat;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodCheckoutFrame extends JFrame {
    private User user;
    private Show show;
    private List<String> selectedSeats;
    private AmenityDAO amenityDAO;
    private Map<Amenity, Integer> cart;
    private BigDecimal ticketSubtotal;
    private BigDecimal foodSubtotal;

    private JLabel lblFoodSubtotal;
    private JLabel lblGrandTotal;

    public FoodCheckoutFrame(User user, Show show, List<String> selectedSeats, BigDecimal ticketSubtotal) {
        this.user = user;
        this.show = show;
        this.selectedSeats = selectedSeats;
        this.ticketSubtotal = ticketSubtotal;
        this.amenityDAO = new AmenityDAO();
        this.cart = new HashMap<>();
        this.foodSubtotal = BigDecimal.ZERO;
        initUI();
    }

    private void initUI() {
        setTitle("Add Food & Amenities");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Theme.PANEL_DARK);

        JLabel title = new JLabel("Enhance Your Experience (Optional)");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Main List of Amenities
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        centerPanel.setOpaque(false);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        List<Amenity> items = amenityDAO.getAllActiveAmenities();
        for (Amenity item : items) {
            itemsPanel.add(createAmenityRow(item));
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        com.cinephile.gui.components.ImagePanel foodArt = new com.cinephile.gui.components.ImagePanel("/ui/popcorn.png",
                20);
        foodArt.setPreferredSize(new Dimension(300, 400));
        centerPanel.add(foodArt, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // Footer Checkout Stats
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.PANEL_DARK);
        footer.setBorder(new EmptyBorder(15, 30, 15, 30));

        JPanel stats = new JPanel(new GridLayout(3, 1));
        stats.setOpaque(false);
        JLabel lblTicketSub = new JLabel("Tickets Subtotal: $" + ticketSubtotal);
        lblTicketSub.setForeground(Theme.TEXT_SECONDARY);
        lblFoodSubtotal = new JLabel("Food & Upgrades: $0.00");
        lblFoodSubtotal.setForeground(Theme.TEXT_SECONDARY);
        lblGrandTotal = new JLabel("Total (excl. Tax): $" + ticketSubtotal);
        lblGrandTotal.setFont(Theme.FONT_HEADER);
        lblGrandTotal.setForeground(Theme.PRIMARY);

        stats.add(lblTicketSub);
        stats.add(lblFoodSubtotal);
        stats.add(lblGrandTotal);

        ModernButton btnCheckout = new ModernButton("Proceed to Payment");
        btnCheckout.setPreferredSize(new Dimension(250, 45));
        btnCheckout.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Passing to Payment system... (Phase 6)");
            // Calculate final taxes and build Booking object
            BigDecimal taxRate = new BigDecimal("0.10"); // 10% tax
            BigDecimal combined = ticketSubtotal.add(foodSubtotal);
            BigDecimal taxes = combined.multiply(taxRate);
            BigDecimal grandTotal = combined.add(taxes);

            Booking booking = new Booking();
            booking.setUserId(user.getId());
            booking.setShowId(show.getId());
            booking.setBookingReference("CINE-" + System.currentTimeMillis());
            booking.setStatus("CONFIRMED");
            booking.setTotalTickets(selectedSeats.size());
            booking.setTicketsSubtotal(ticketSubtotal);
            booking.setFoodSubtotal(foodSubtotal);
            booking.setTaxes(taxes);
            booking.setGrandTotal(grandTotal);

            List<Seat> seats = new ArrayList<>();
            for (String s : selectedSeats) {
                seats.add(new Seat(show.getId(), s, false));
            }

            // Route dynamically to Mock Payment System
            new PaymentFrame(user, show, booking, seats).setVisible(true);
            dispose();
        });

        footer.add(stats, BorderLayout.WEST);
        footer.add(btnCheckout, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createAmenityRow(Amenity item) {
        RoundedPanel panel = new RoundedPanel(15, Theme.PANEL_DARK);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setMaximumSize(new Dimension(800, 70));

        JLabel nameLabel = new JLabel(item.getName() + " ($" + item.getPrice() + ")");
        nameLabel.setFont(Theme.FONT_REGULAR);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JLabel qtyLabel = new JLabel("0");
        qtyLabel.setForeground(Theme.TEXT_PRIMARY);
        qtyLabel.setFont(Theme.FONT_HEADER);
        qtyLabel.setBorder(new EmptyBorder(0, 15, 0, 15));

        ModernButton btnMinus = new ModernButton("-");
        btnMinus.setPreferredSize(new Dimension(40, 40));
        btnMinus.addActionListener(e -> {
            int current = cart.getOrDefault(item, 0);
            if (current > 0) {
                cart.put(item, current - 1);
                qtyLabel.setText(String.valueOf(current - 1));
                updateTotals();
            }
        });

        ModernButton btnPlus = new ModernButton("+");
        btnPlus.setPreferredSize(new Dimension(40, 40));
        btnPlus.addActionListener(e -> {
            int current = cart.getOrDefault(item, 0);
            cart.put(item, current + 1);
            qtyLabel.setText(String.valueOf(current + 1));
            updateTotals();
        });

        actionPanel.add(btnMinus);
        actionPanel.add(qtyLabel);
        actionPanel.add(btnPlus);

        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(actionPanel, BorderLayout.EAST);

        return panel;
    }

    private void updateTotals() {
        BigDecimal newFoodTotal = BigDecimal.ZERO;
        for (Map.Entry<Amenity, Integer> entry : cart.entrySet()) {
            BigDecimal qty = new BigDecimal(entry.getValue());
            newFoodTotal = newFoodTotal.add(entry.getKey().getPrice().multiply(qty));
        }
        this.foodSubtotal = newFoodTotal;
        lblFoodSubtotal.setText("Food & Upgrades: $" + foodSubtotal.toString());
        lblGrandTotal.setText("Total (excl. Tax): $" + ticketSubtotal.add(foodSubtotal).toString());
    }
}
