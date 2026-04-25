package com.cinephile.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.GradientPaint;

public class Theme {
    // ─── Core Palette ───────────────────────────────────────────────────
    public static final Color BACKGROUND_DARK = new Color(14, 14, 18);
    public static final Color PANEL_DARK = new Color(24, 24, 30);
    public static final Color CARD_DARK = new Color(30, 30, 38);
    public static final Color SIDEBAR_DARK = new Color(18, 18, 24);
    public static final Color INPUT_BG = new Color(36, 36, 44);
    public static final Color BORDER_COLOR = new Color(55, 55, 65);
    public static final Color DIVIDER = new Color(45, 45, 55);

    // ─── Brand ──────────────────────────────────────────────────────────
    public static final Color PRIMARY = new Color(229, 9, 20);
    public static final Color PRIMARY_HOVER = new Color(255, 40, 50);
    public static final Color PRIMARY_DARK = new Color(180, 5, 15);
    public static final Color ACCENT_GOLD = new Color(255, 193, 7);

    // ─── Semantic ────────────────────────────────────────────────────────
    public static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    public static final Color SUCCESS_DARK = new Color(22, 163, 74);
    public static final Color WARNING_AMBER = new Color(245, 158, 11);

    // ─── Seat Colors ─────────────────────────────────────────────────────
    public static final Color SEAT_AVAILABLE = new Color(38, 70, 83);
    public static final Color SEAT_BOOKED = new Color(55, 55, 60);
    public static final Color SEAT_SELECTED = new Color(229, 9, 20);
    public static final Color SEAT_PREMIUM = new Color(255, 193, 7);

    // ─── Text ────────────────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY = new Color(240, 240, 245);
    public static final Color TEXT_SECONDARY = new Color(155, 155, 170);
    public static final Color TEXT_MUTED = new Color(100, 100, 115);

    // ─── Fonts ───────────────────────────────────────────────────────────
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 30);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_SUBHEADER = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_CAPTION = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_BRAND = new Font("SansSerif", Font.BOLD, 36);

    // ─── Cursors ────────────────────────────────────────────────────────
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    // ─── Helpers ────────────────────────────────────────────────────────
    public static GradientPaint createPrimaryGradient(int width, int height) {
        return new GradientPaint(0, 0, PRIMARY, 0, height, PRIMARY_DARK);
    }

    public static GradientPaint createDarkGradient(int width, int height) {
        return new GradientPaint(0, 0, new Color(20, 20, 28), 0, height, new Color(10, 10, 14));
    }

    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}
