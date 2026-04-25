package com.cinephile.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Cursor;

public class Theme {
    // Colors
    public static final Color BACKGROUND_DARK = new Color(18, 18, 20);
    public static final Color PANEL_DARK = new Color(28, 28, 30);
    public static final Color PRIMARY = new Color(229, 9, 20); // Netflix-like red
    public static final Color PRIMARY_HOVER = new Color(255, 30, 40);
    public static final Color TEXT_PRIMARY = new Color(245, 245, 245);
    public static final Color TEXT_SECONDARY = new Color(170, 170, 170);
    public static final Color INPUT_BG = new Color(40, 40, 45);
    public static final Color BORDER_COLOR = new Color(60, 60, 65);

    // Fonts
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);

    // Cursors
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
}
