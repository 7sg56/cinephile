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
import com.cinephile.gui.components.StepIndicatorPanel;

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
    private JPanel centerPanel;

    private static final String[] STEP_LABELS = { "Seats", "Food & Drinks", "Payment" };

    public PaymentFrame(User user, Show show, Booking booking, List<Seat> seats) {
        this.user = user;
        this.show = show;
        this.booking = booking;
        this.seats = seats;
        this.bookingDAO = new BookingDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Payment - Cinephile");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        // ═══════════════ TOP: Step Indicator ═══════════════
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Theme.PANEL_DARK);

        StepIndicatorPanel stepIndicator = new StepIndicatorPanel(STEP_LABELS, 2);
        stepIndicator.setPreferredSize(new Dimension(0, 70));
        topSection.add(stepIndicator, BorderLayout.CENTER);
        add(topSection, BorderLayout.NORTH);

        // ═══════════════ CENTER: Receipt + Payment ═══════════════
        centerPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        centerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        centerPanel.setOpaque(false);

        centerPanel.add(createReceiptPanel());
        centerPanel.add(createPaymentPanel());

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createReceiptPanel() {
        RoundedPanel panel = new RoundedPanel(18, Theme.PANEL_DARK, true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblReceipt = new JLabel("Order Summary");
        lblReceipt.setFont(Theme.FONT_HEADER);
        lblReceipt.setForeground(Theme.TEXT_PRIMARY);
        lblReceipt.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblReceipt);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Movie info
        panel.add(createReceiptRow("Movie", show.getMovieTitle()));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createReceiptRow("Show", show.getShowTime() + " | " + show.getHallName()));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createReceiptRow("Tickets", String.valueOf(booking.getTotalTickets())));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Divider
        panel.add(createDivider());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Pricing
        panel.add(createReceiptRow("Tickets Subtotal", "Rs. " + booking.getTicketsSubtotal()));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createReceiptRow("Food & Amenities", "Rs. " + booking.getFoodSubtotal()));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(createReceiptRow("Taxes (10%)", "Rs. " + booking.getTaxes()));
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(createDivider());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Grand total
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel totalLabel = new JLabel("Grand Total");
        totalLabel.setFont(Theme.FONT_HEADER);
        totalLabel.setForeground(Theme.TEXT_PRIMARY);
        JLabel totalValue = new JLabel("Rs. " + booking.getGrandTotal());
        totalValue.setFont(Theme.FONT_TITLE);
        totalValue.setForeground(Theme.PRIMARY);
        totalRow.add(totalLabel, BorderLayout.WEST);
        totalRow.add(totalValue, BorderLayout.EAST);
        panel.add(totalRow);

        return panel;
    }

    private JPanel createPaymentPanel() {
        RoundedPanel panel = new RoundedPanel(18, Theme.PANEL_DARK, true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblPay = new JLabel("Select Payment Method");
        lblPay.setFont(Theme.FONT_HEADER);
        lblPay.setForeground(Theme.TEXT_PRIMARY);
        lblPay.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblPay);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Payment method cards
        panel.add(createPaymentMethodCard("Credit / Debit Card", "Visa, Mastercard, RuPay", "CARD"));
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(createPaymentMethodCard("UPI", "Google Pay, PhonePe, Paytm", "UPI"));
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(createPaymentMethodCard("Net Banking", "All major banks supported", "NETBANKING"));

        return panel;
    }

    private JPanel createPaymentMethodCard(String title, String subtitle, String method) {
        RoundedPanel card = new RoundedPanel(14, Theme.CARD_DARK, true);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Theme.HAND_CURSOR);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.FONT_SUBHEADER);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(Theme.FONT_CAPTION);
        lblSub.setForeground(Theme.TEXT_MUTED);

        textPanel.add(lblTitle);
        textPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        textPanel.add(lblSub);

        ModernButton payBtn = new ModernButton("Pay Now");
        payBtn.setPreferredSize(new Dimension(110, 38));
        payBtn.addActionListener(e -> processPayment(method));

        card.add(textPanel, BorderLayout.CENTER);
        card.add(payBtn, BorderLayout.EAST);

        return card;
    }

    private JPanel createReceiptRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setForeground(Theme.TEXT_SECONDARY);
        l.setFont(Theme.FONT_LABEL);
        JLabel v = new JLabel(value);
        v.setForeground(Theme.TEXT_PRIMARY);
        v.setFont(Theme.FONT_LABEL);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private JPanel createDivider() {
        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBackground(Theme.DIVIDER);
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        return divider;
    }

    private void processPayment(String method) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm payment of Rs. " + booking.getGrandTotal() + " via " + method + "?",
                "Confirm Payment", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingDAO.createBookingWithSeats(booking, seats)) {
                // Replace center panel with success view
                showSuccessPanel(method);
            } else {
                JOptionPane.showMessageDialog(this,
                        "A database error occurred while saving your booking. Please try again.");
            }
        }
    }

    private void showSuccessPanel(String method) {
        centerPanel.removeAll();

        RoundedPanel successPanel = new RoundedPanel(20, Theme.PANEL_DARK, true);
        successPanel.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Success circle
        JPanel checkCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = 72;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Glow
                g2.setColor(Theme.withAlpha(Theme.SUCCESS_GREEN, 30));
                g2.fillOval(x - 8, y - 8, size + 16, size + 16);

                // Circle
                g2.setPaint(new GradientPaint(x, y, Theme.SUCCESS_GREEN, x, y + size, Theme.SUCCESS_DARK));
                g2.fillOval(x, y, size, size);

                // Checkmark
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.drawLine(cx - 14, cy, cx - 4, cy + 12);
                g2.drawLine(cx - 4, cy + 12, cx + 16, cy - 10);

                g2.dispose();
            }
        };
        checkCircle.setOpaque(false);
        checkCircle.setPreferredSize(new Dimension(100, 100));
        checkCircle.setMaximumSize(new Dimension(100, 100));
        checkCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSuccess = new JLabel("Booking Confirmed!");
        lblSuccess.setFont(Theme.FONT_TITLE);
        lblSuccess.setForeground(Theme.TEXT_PRIMARY);
        lblSuccess.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRef = new JLabel("Ref: " + booking.getBookingReference());
        lblRef.setFont(Theme.FONT_LABEL);
        lblRef.setForeground(Theme.TEXT_SECONDARY);
        lblRef.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAmount = new JLabel("Rs. " + booking.getGrandTotal() + " paid via " + method);
        lblAmount.setFont(Theme.FONT_REGULAR);
        lblAmount.setForeground(Theme.TEXT_MUTED);
        lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton btnDownload = new ModernButton("Download Ticket (PDF)", ModernButton.Style.SUCCESS);
        btnDownload.setMaximumSize(new Dimension(260, 48));
        btnDownload.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDownload.addActionListener(e -> {
            try {
                String pdfPath = PDFGenerator.generateTicketReceipt(user, show, booking, seats, method);
                JOptionPane.showMessageDialog(this, "Ticket saved at:\n" + pdfPath);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to generate PDF receipt.");
            }
        });

        ModernButton btnHome = new ModernButton("Back to Home", ModernButton.Style.GHOST);
        btnHome.setMaximumSize(new Dimension(200, 44));
        btnHome.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnHome.addActionListener(e -> {
            new ClientDashboardFrame(user).setVisible(true);
            dispose();
        });

        content.add(checkCircle);
        content.add(Box.createRigidArea(new Dimension(0, 24)));
        content.add(lblSuccess);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(lblRef);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(lblAmount);
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        content.add(btnDownload);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        content.add(btnHome);

        successPanel.add(content);
        centerPanel.add(successPanel);
        centerPanel.add(new JPanel() {
            {
                setOpaque(false);
            }
        }); // keep 2-column grid balanced

        centerPanel.revalidate();
        centerPanel.repaint();
    }
}
