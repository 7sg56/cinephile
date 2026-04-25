package com.cinephile.gui.components;

import com.cinephile.util.Theme;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;

public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private boolean showBorder;
    private Color borderColor;

    public RoundedPanel(int radius, Color bgColor) {
        this(radius, bgColor, false);
    }

    public RoundedPanel(int radius, Color bgColor, boolean showBorder) {
        super();
        this.cornerRadius = radius;
        this.showBorder = showBorder;
        this.borderColor = Theme.BORDER_COLOR;
        this.setBackground(bgColor);
        this.setOpaque(false);
    }

    public void setShowBorder(boolean show) {
        this.showBorder = show;
        repaint();
    }

    public void setBorderColor(Color c) {
        this.borderColor = c;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        if (showBorder) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
