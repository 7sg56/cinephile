package com.cinephile.gui;

import com.cinephile.dao.BookingDAO;
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
import java.util.List;

public class BookingFlowFrame extends JFrame {
    private User user;
    private Show show;
    private BookingDAO bookingDAO;
    private List<String> bookedSeats;
    private List<String> selectedSeats;

    private JLabel lblSeatCount;
    private JLabel lblSeatList;
    private JLabel lblPriceEstimate;

    private static final String[] STEP_LABELS = { "Seats", "Food & Drinks", "Payment" };

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
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        // ═══════════════ TOP: Step Indicator + Movie Info ═══════════════
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Theme.PANEL_DARK);

        StepIndicatorPanel stepIndicator = new StepIndicatorPanel(STEP_LABELS, 0);
        stepIndicator.setPreferredSize(new Dimension(0, 70));
        topSection.add(stepIndicator, BorderLayout.CENTER);

        // Movie info bar
        JPanel movieBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        movieBar.setBackground(Theme.BACKGROUND_DARK);
        JLabel lblMovie = new JLabel(show.getMovieTitle());
        lblMovie.setFont(Theme.FONT_SUBHEADER);
        lblMovie.setForeground(Theme.TEXT_PRIMARY);
        JLabel lblShowInfo = new JLabel(show.getShowTime() + "  |  " + show.getHallName());
        lblShowInfo.setFont(Theme.FONT_LABEL);
        lblShowInfo.setForeground(Theme.TEXT_SECONDARY);
        movieBar.add(lblMovie);
        movieBar.add(lblShowInfo);
        topSection.add(movieBar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);

        // ═══════════════ CENTER: Screen + Seat Grid ═══════════════
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(15, 40, 15, 0));

        // Cinema screen arc
        JPanel screenPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                // Glow
                g2.setPaint(new RadialGradientPaint(
                        new Point(w / 2, h + 30), w / 2f,
                        new float[] { 0f, 0.6f, 1f },
                        new Color[] { Theme.withAlpha(Theme.TEXT_SECONDARY, 20),
                                Theme.withAlpha(Theme.TEXT_SECONDARY, 8), new Color(0, 0, 0, 0) }));
                g2.fillRect(0, 0, w, h);

                // Arc
                g2.setColor(Theme.withAlpha(Theme.TEXT_SECONDARY, 80));
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(40, h - 50, w - 80, 80, 0, 180);

                // Label
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                FontMetrics fm = g2.getFontMetrics();
                String label = "SCREEN";
                g2.drawString(label, (w - fm.stringWidth(label)) / 2, h - 12);
                g2.dispose();
            }
        };
        screenPanel.setOpaque(false);
        screenPanel.setPreferredSize(new Dimension(0, 60));
        centerWrapper.add(screenPanel, BorderLayout.NORTH);

        // Seat grid with tier labels
        JPanel seatArea = new JPanel();
        seatArea.setLayout(new BoxLayout(seatArea, BoxLayout.Y_AXIS));
        seatArea.setOpaque(false);
        seatArea.setBorder(new EmptyBorder(10, 30, 10, 30));

        String[][] tiers = {
                { "PREMIUM", "A", "B" },
                { "EXECUTIVE", "C", "D" },
                { "STANDARD", "E", "F" }
        };
        String[] tierPriceLabels = {
                "Rs. " + show.getBasePrice().multiply(new BigDecimal("1.30")).setScale(0, BigDecimal.ROUND_HALF_UP),
                "Rs. " + show.getBasePrice().multiply(new BigDecimal("1.15")).setScale(0, BigDecimal.ROUND_HALF_UP),
                "Rs. " + show.getBasePrice().setScale(0, BigDecimal.ROUND_HALF_UP)
        };

        for (int t = 0; t < tiers.length; t++) {
            // Tier label
            JPanel tierHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            tierHeader.setOpaque(false);
            tierHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            JLabel tierLabel = new JLabel(tiers[t][0] + " - " + tierPriceLabels[t]);
            tierLabel.setFont(Theme.FONT_CAPTION);
            tierLabel.setForeground(t == 0 ? Theme.ACCENT_GOLD : Theme.TEXT_MUTED);
            tierHeader.add(tierLabel);
            seatArea.add(tierHeader);
            seatArea.add(Box.createRigidArea(new Dimension(0, 6)));

            // Rows for this tier
            for (int ri = 1; ri < tiers[t].length; ri++) {
                char row = tiers[t][ri].charAt(0);
                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
                rowPanel.setOpaque(false);
                rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

                // Row label
                JLabel rowLabel = new JLabel(String.valueOf(row));
                rowLabel.setPreferredSize(new Dimension(20, 40));
                rowLabel.setFont(Theme.FONT_SMALL);
                rowLabel.setForeground(Theme.TEXT_MUTED);
                rowPanel.add(rowLabel);

                for (int c = 1; c <= 10; c++) {
                    String seatNo = "" + row + c;
                    rowPanel.add(createSeatButton(seatNo));
                }

                // Row label right
                JLabel rowLabelR = new JLabel(String.valueOf(row));
                rowLabelR.setPreferredSize(new Dimension(20, 40));
                rowLabelR.setFont(Theme.FONT_SMALL);
                rowLabelR.setForeground(Theme.TEXT_MUTED);
                rowPanel.add(rowLabelR);

                seatArea.add(rowPanel);
                seatArea.add(Box.createRigidArea(new Dimension(0, 4)));
            }
            seatArea.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        // Seat Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legend.setOpaque(false);
        legend.add(createLegendItem(Theme.SEAT_AVAILABLE, "Available"));
        legend.add(createLegendItem(Theme.SEAT_SELECTED, "Selected"));
        legend.add(createLegendItem(Theme.SEAT_BOOKED, "Booked"));
        seatArea.add(legend);

        centerWrapper.add(seatArea, BorderLayout.CENTER);

        // ═══════════════ RIGHT: Live Summary Panel ═══════════════
        RoundedPanel summaryPanel = new RoundedPanel(16, Theme.PANEL_DARK, true);
        summaryPanel.setPreferredSize(new Dimension(250, 0));
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel summaryTitle = new JLabel("Booking Summary");
        summaryTitle.setFont(Theme.FONT_SUBHEADER);
        summaryTitle.setForeground(Theme.TEXT_PRIMARY);
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.DIVIDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        lblSeatCount = new JLabel("Seats: 0 selected");
        lblSeatCount.setFont(Theme.FONT_LABEL);
        lblSeatCount.setForeground(Theme.TEXT_SECONDARY);
        lblSeatCount.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSeatList = new JLabel(" ");
        lblSeatList.setFont(Theme.FONT_SMALL);
        lblSeatList.setForeground(Theme.TEXT_MUTED);
        lblSeatList.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblPriceEstimate = new JLabel("Rs. 0");
        lblPriceEstimate.setFont(Theme.FONT_TITLE);
        lblPriceEstimate.setForeground(Theme.PRIMARY);
        lblPriceEstimate.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernButton btnProceed = new ModernButton("Proceed to Food & Drinks");
        btnProceed.setMaximumSize(new Dimension(210, 44));
        btnProceed.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProceed.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one seat.");
                return;
            }
            BigDecimal subtotal = calculateSubtotal();
            new FoodCheckoutFrame(user, show, selectedSeats, subtotal).setVisible(true);
            dispose();
        });

        summaryPanel.add(summaryTitle);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        summaryPanel.add(sep);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        summaryPanel.add(lblSeatCount);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        summaryPanel.add(lblSeatList);
        summaryPanel.add(Box.createVerticalGlue());
        summaryPanel.add(lblPriceEstimate);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        summaryPanel.add(btnProceed);

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.setBorder(new EmptyBorder(15, 10, 15, 20));
        rightWrapper.add(summaryPanel, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);
        add(rightWrapper, BorderLayout.EAST);
    }

    private JButton createSeatButton(String seatNumber) {
        String displayText = seatNumber.substring(1);
        JButton btn = new JButton(displayText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t, (getWidth() - fm.stringWidth(t)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(44, 38));
        btn.setFont(Theme.FONT_SMALL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Theme.HAND_CURSOR);

        boolean isBooked = bookedSeats.contains(seatNumber);
        if (isBooked) {
            btn.setBackground(Theme.SEAT_BOOKED);
            btn.setForeground(Theme.TEXT_MUTED);
            btn.setEnabled(false);
            btn.setCursor(Theme.DEFAULT_CURSOR);
        } else {
            btn.setBackground(Theme.SEAT_AVAILABLE);
            btn.setForeground(Theme.TEXT_PRIMARY);
            btn.addActionListener(e -> toggleSeat(btn, seatNumber));
        }
        return btn;
    }

    private void toggleSeat(JButton btn, String seatNumber) {
        if (selectedSeats.contains(seatNumber)) {
            selectedSeats.remove(seatNumber);
            btn.setBackground(Theme.SEAT_AVAILABLE);
            btn.setForeground(Theme.TEXT_PRIMARY);
        } else {
            selectedSeats.add(seatNumber);
            btn.setBackground(Theme.SEAT_SELECTED);
            btn.setForeground(Color.WHITE);
        }
        btn.repaint();
        updateSummary();
    }

    private BigDecimal getMultiplierForSeat(String seatNo) {
        char row = seatNo.charAt(0);
        if (row == 'A' || row == 'B')
            return new BigDecimal("1.30");
        if (row == 'C' || row == 'D')
            return new BigDecimal("1.15");
        return BigDecimal.ONE;
    }

    private BigDecimal calculateSubtotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (String s : selectedSeats) {
            total = total.add(show.getBasePrice().multiply(getMultiplierForSeat(s)));
        }
        return total.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private void updateSummary() {
        int count = selectedSeats.size();
        lblSeatCount.setText("Seats: " + count + " selected");
        lblSeatList.setText(count > 0 ? String.join(", ", selectedSeats) : " ");
        BigDecimal subtotal = calculateSubtotal();
        lblPriceEstimate.setText("Rs. " + subtotal);
    }

    private JPanel createLegendItem(Color color, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(16, 16));
        swatch.setBackground(color);
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_CAPTION);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        p.add(swatch);
        p.add(lbl);
        return p;
    }
}
