package com.cinephile.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ImagePanel extends JPanel {
    private Image img;
    private int radius;

    public ImagePanel(String resourcePath, int cornerRadius) {
        this.radius = cornerRadius;
        setOpaque(false);
        try {
            this.img = new ImageIcon(getClass().getResource(resourcePath)).getImage();
        } catch (Exception e) {
            System.err.println("Failed to load image: " + resourcePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Clip round corners if radius > 0
            if (radius > 0) {
                Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
                g2.clip(clip);
            }

            g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        }
    }
}
