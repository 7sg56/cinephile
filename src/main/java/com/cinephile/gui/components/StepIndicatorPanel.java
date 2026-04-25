package com.cinephile.gui.components;

import com.cinephile.util.Theme;
import javax.swing.*;
import java.awt.*;

/**
 * A horizontal step-progress indicator (e.g. Seats > Food & Drinks > Payment).
 * Renders numbered circles connected by lines, with completed/active/upcoming
 * states.
 */
public class StepIndicatorPanel extends JPanel {
    private final String[] steps;
    private int activeStep; // 0-indexed

    public StepIndicatorPanel(String[] steps, int activeStep) {
        this.steps = steps;
        this.activeStep = activeStep;
        setOpaque(false);
        setPreferredSize(new Dimension(0, 70));
    }

    public void setActiveStep(int step) {
        this.activeStep = step;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();
        int circleSize = 32;
        int totalSteps = steps.length;

        // Compute spacing
        int usableWidth = Math.min(w - 80, 600);
        int startX = (w - usableWidth) / 2;
        int stepGap = usableWidth / (totalSteps - 1);
        int cy = h / 2 - 5;

        // Draw connecting lines first
        for (int i = 0; i < totalSteps - 1; i++) {
            int x1 = startX + i * stepGap + circleSize / 2;
            int x2 = startX + (i + 1) * stepGap - circleSize / 2;
            if (i < activeStep) {
                g2.setColor(Theme.PRIMARY);
                g2.setStroke(new BasicStroke(3f));
            } else {
                g2.setColor(Theme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(2f));
            }
            g2.drawLine(x1 + circleSize / 2, cy + circleSize / 2, x2, cy + circleSize / 2);
        }

        // Draw circles and labels
        for (int i = 0; i < totalSteps; i++) {
            int cx = startX + i * stepGap;

            if (i < activeStep) {
                // Completed
                g2.setColor(Theme.PRIMARY);
                g2.fillOval(cx, cy, circleSize, circleSize);
                g2.setColor(Color.WHITE);
                g2.setFont(Theme.FONT_SUBHEADER);
                // Draw checkmark
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int mx = cx + circleSize / 2;
                int my = cy + circleSize / 2;
                g2.drawLine(mx - 6, my, mx - 2, my + 5);
                g2.drawLine(mx - 2, my + 5, mx + 7, my - 5);
            } else if (i == activeStep) {
                // Active - gradient fill
                g2.setPaint(Theme.createPrimaryGradient(circleSize, circleSize));
                g2.fillOval(cx, cy, circleSize, circleSize);
                // White glow ring
                g2.setColor(Theme.withAlpha(Theme.PRIMARY, 80));
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(cx - 3, cy - 3, circleSize + 6, circleSize + 6);
                // Number
                g2.setColor(Color.WHITE);
                g2.setFont(Theme.FONT_SUBHEADER);
                FontMetrics fm = g2.getFontMetrics();
                String num = String.valueOf(i + 1);
                g2.drawString(num, cx + (circleSize - fm.stringWidth(num)) / 2,
                        cy + (circleSize + fm.getAscent() - fm.getDescent()) / 2);
            } else {
                // Upcoming
                g2.setColor(Theme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx, cy, circleSize, circleSize);
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_SMALL);
                FontMetrics fm = g2.getFontMetrics();
                String num = String.valueOf(i + 1);
                g2.drawString(num, cx + (circleSize - fm.stringWidth(num)) / 2,
                        cy + (circleSize + fm.getAscent() - fm.getDescent()) / 2);
            }

            // Label below
            Color labelColor = (i <= activeStep) ? Theme.TEXT_PRIMARY : Theme.TEXT_MUTED;
            g2.setColor(labelColor);
            g2.setFont((i == activeStep) ? Theme.FONT_LABEL : Theme.FONT_CAPTION);
            FontMetrics fm = g2.getFontMetrics();
            int labelX = cx + (circleSize - fm.stringWidth(steps[i])) / 2;
            g2.drawString(steps[i], labelX, cy + circleSize + 18);
        }

        g2.dispose();
    }
}
