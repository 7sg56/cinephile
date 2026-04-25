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
import com.cinephile.gui.components.StepIndicatorPanel;

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

    private static final String[] STEP_LABELS = { "Seats", "Food & Drinks", "Payment" };

    // Warm palette for food item card backgrounds
    private static final Color[] CARD_ACCENT_COLORS = {
            new Color(60, 40, 30),
            new Color(40, 50, 40),
            new Color(50, 35, 45),
            new Color(35, 45, 55),
            new Color(55, 45, 30),
            new Color(40, 40, 55),
    };

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
        setTitle("Food & Drinks");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        // ═══════════════ TOP: Step Indicator ═══════════════
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Theme.PANEL_DARK);

        StepIndicatorPanel stepIndicator = new StepIndicatorPanel(STEP_LABELS, 1);
        stepIndicator.setPreferredSize(new Dimension(0, 70));
        topSection.add(stepIndicator, BorderLayout.CENTER);

        // Sub-header
        JPanel subHeader = new JPanel(new BorderLayout());
        subHeader.setBackground(Theme.BACKGROUND_DARK);
        subHeader.setBorder(new EmptyBorder(12, 30, 12, 30));
        JLabel title = new JLabel("Grab a Bite");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_PRIMARY);
        JLabel subLabel = new JLabel("Add snacks and drinks to your order (optional)");
        subLabel.setFont(Theme.FONT_CAPTION);
        subLabel.setForeground(Theme.TEXT_MUTED);
        subHeader.add(title, BorderLayout.WEST);
        subHeader.add(subLabel, BorderLayout.EAST);
        topSection.add(subHeader, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);

        // ═══════════════ CENTER: Food Item Grid ═══════════════
        List<Amenity> items = amenityDAO.getAllActiveAmenities();
        int cols = 3;
        int rows = Math.max(1, (int) Math.ceil((double) items.size() / cols));

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 16, 16));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        for (int i = 0; i < items.size(); i++) {
            gridPanel.add(createFoodCard(items.get(i), i));
        }
        // Fill remaining cells if grid isn't full
        int remaining = (rows * cols) - items.size();
        for (int i = 0; i < remaining; i++) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            gridPanel.add(empty);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // ═══════════════ FOOTER: Totals & Checkout ═══════════════
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.PANEL_DARK);
        footer.setBorder(new EmptyBorder(15, 30, 15, 30));

        JPanel stats = new JPanel();
        stats.setOpaque(false);
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));

        JLabel lblTicketSub = new JLabel("Tickets: Rs. " + ticketSubtotal);
        lblTicketSub.setForeground(Theme.TEXT_SECONDARY);
        lblTicketSub.setFont(Theme.FONT_LABEL);

        lblFoodSubtotal = new JLabel("Food & Drinks: Rs. 0.00");
        lblFoodSubtotal.setForeground(Theme.TEXT_SECONDARY);
        lblFoodSubtotal.setFont(Theme.FONT_LABEL);

        lblGrandTotal = new JLabel("Total: Rs. " + ticketSubtotal);
        lblGrandTotal.setFont(Theme.FONT_HEADER);
        lblGrandTotal.setForeground(Theme.PRIMARY);

        stats.add(lblTicketSub);
        stats.add(Box.createRigidArea(new Dimension(0, 4)));
        stats.add(lblFoodSubtotal);
        stats.add(Box.createRigidArea(new Dimension(0, 6)));
        stats.add(lblGrandTotal);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        ModernButton btnSkip = new ModernButton("Skip", ModernButton.Style.GHOST);
        btnSkip.setPreferredSize(new Dimension(140, 44));
        btnSkip.addActionListener(e -> proceedToPayment());

        ModernButton btnCheckout = new ModernButton("Proceed to Payment");
        btnCheckout.setPreferredSize(new Dimension(220, 44));
        btnCheckout.addActionListener(e -> proceedToPayment());

        btnPanel.add(btnSkip);
        btnPanel.add(btnCheckout);

        footer.add(stats, BorderLayout.WEST);
        footer.add(btnPanel, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createFoodCard(Amenity item, int index) {
        RoundedPanel card = new RoundedPanel(16, Theme.CARD_DARK, true);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Top: colored placeholder for food image
        JPanel imageArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color accent = CARD_ACCENT_COLORS[index % CARD_ACCENT_COLORS.length];
                g2.setPaint(new GradientPaint(0, 0, accent, getWidth(), getHeight(), accent.darker()));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Food icon placeholder text
                g2.setColor(Theme.withAlpha(Color.WHITE, 60));
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                String initial = item.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initial, (getWidth() - fm.stringWidth(initial)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        imageArea.setOpaque(false);
        imageArea.setPreferredSize(new Dimension(0, 80));
        card.add(imageArea, BorderLayout.NORTH);

        // Center: name and price
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(Theme.FONT_SUBHEADER);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("Rs. " + item.getPrice());
        priceLabel.setFont(Theme.FONT_LABEL);
        priceLabel.setForeground(Theme.ACCENT_GOLD);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        infoPanel.add(priceLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Bottom: quantity stepper
        JPanel stepperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        stepperPanel.setOpaque(false);
        stepperPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel qtyLabel = new JLabel("0");
        qtyLabel.setForeground(Theme.TEXT_PRIMARY);
        qtyLabel.setFont(Theme.FONT_HEADER);
        qtyLabel.setPreferredSize(new Dimension(40, 36));
        qtyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        ModernButton btnMinus = new ModernButton("-", ModernButton.Style.SECONDARY);
        btnMinus.setPreferredSize(new Dimension(38, 36));
        btnMinus.setFont(Theme.FONT_HEADER);
        btnMinus.addActionListener(e -> {
            int current = cart.getOrDefault(item, 0);
            if (current > 0) {
                cart.put(item, current - 1);
                qtyLabel.setText(String.valueOf(current - 1));
                updateTotals();
            }
        });

        ModernButton btnPlus = new ModernButton("+", ModernButton.Style.SECONDARY);
        btnPlus.setPreferredSize(new Dimension(38, 36));
        btnPlus.setFont(Theme.FONT_HEADER);
        btnPlus.addActionListener(e -> {
            int current = cart.getOrDefault(item, 0);
            cart.put(item, current + 1);
            qtyLabel.setText(String.valueOf(current + 1));
            updateTotals();
        });

        stepperPanel.add(btnMinus);
        stepperPanel.add(qtyLabel);
        stepperPanel.add(btnPlus);

        card.add(stepperPanel, BorderLayout.SOUTH);

        return card;
    }

    private void proceedToPayment() {
        BigDecimal taxRate = new BigDecimal("0.10");
        BigDecimal combined = ticketSubtotal.add(foodSubtotal);
        BigDecimal taxes = combined.multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
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

        new PaymentFrame(user, show, booking, seats).setVisible(true);
        dispose();
    }

    private void updateTotals() {
        BigDecimal newFoodTotal = BigDecimal.ZERO;
        for (Map.Entry<Amenity, Integer> entry : cart.entrySet()) {
            BigDecimal qty = new BigDecimal(entry.getValue());
            newFoodTotal = newFoodTotal.add(entry.getKey().getPrice().multiply(qty));
        }
        this.foodSubtotal = newFoodTotal;
        lblFoodSubtotal.setText("Food & Drinks: Rs. " + foodSubtotal);
        lblGrandTotal.setText("Total: Rs. " + ticketSubtotal.add(foodSubtotal));
    }
}
