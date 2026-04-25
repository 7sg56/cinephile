package com.cinephile.gui.components;

import com.cinephile.util.Theme;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {

    public enum Style {
        PRIMARY, GHOST, SUCCESS, SECONDARY
    }

    private boolean isHovered = false;
    private boolean isPressed = false;
    private Style style;

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        init();
    }

    private void init() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(Theme.FONT_SUBHEADER);
        setCursor(Theme.HAND_CURSOR);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 12;

        switch (style) {
            case PRIMARY:
                if (isPressed) {
                    g2.setColor(Theme.PRIMARY_DARK);
                } else if (isHovered) {
                    g2.setPaint(new GradientPaint(0, 0, Theme.PRIMARY_HOVER, 0, h, Theme.PRIMARY));
                } else {
                    g2.setPaint(Theme.createPrimaryGradient(w, h));
                }
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                break;

            case SUCCESS:
                if (isPressed) {
                    g2.setColor(Theme.SUCCESS_DARK);
                } else if (isHovered) {
                    g2.setColor(Theme.SUCCESS_GREEN.brighter());
                } else {
                    g2.setPaint(new GradientPaint(0, 0, Theme.SUCCESS_GREEN, 0, h, Theme.SUCCESS_DARK));
                }
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                break;

            case GHOST:
                if (isHovered) {
                    g2.setColor(Theme.withAlpha(Theme.PRIMARY, 30));
                    g2.fillRoundRect(0, 0, w, h, arc, arc);
                }
                g2.setColor(isHovered ? Theme.PRIMARY_HOVER : Theme.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);
                setForeground(isHovered ? Theme.PRIMARY_HOVER : Theme.PRIMARY);
                break;

            case SECONDARY:
                if (isPressed) {
                    g2.setColor(Theme.BORDER_COLOR);
                } else if (isHovered) {
                    g2.setColor(Theme.INPUT_BG.brighter());
                } else {
                    g2.setColor(Theme.INPUT_BG);
                }
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                g2.setColor(Theme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
                break;
        }

        // Subtle top-edge highlight for PRIMARY and SUCCESS
        if (style == Style.PRIMARY || style == Style.SUCCESS) {
            g2.setColor(new Color(255, 255, 255, 25));
            g2.drawLine(6, 1, w - 7, 1);
        }

        g2.dispose();

        // Slight vertical offset on press for tactile feel
        if (isPressed) {
            Graphics g3 = g.create();
            g3.translate(0, 1);
            super.paintComponent(g3);
            g3.dispose();
        } else {
            super.paintComponent(g);
        }
    }
}
