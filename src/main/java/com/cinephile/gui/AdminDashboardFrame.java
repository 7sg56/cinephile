package com.cinephile.gui;

import com.cinephile.dao.MovieDAO;
import com.cinephile.model.Movie;
import com.cinephile.model.User;
import com.cinephile.util.Theme;
import com.cinephile.gui.components.ModernButton;
import com.cinephile.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {
    private User admin;
    private MovieDAO movieDAO;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private ModernButton activeSidebarBtn;

    public AdminDashboardFrame(User adminUser) {
        this.admin = adminUser;
        this.movieDAO = new MovieDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Cinephile Admin - " + admin.getFullName());
        setSize(1280, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.BACKGROUND_DARK);
        mainContentPanel.setBorder(new EmptyBorder(24, 28, 24, 28));
        mainContentPanel.add(createOverviewPanel(), "OVERVIEW");
        mainContentPanel.add(createMoviesPanel(), "MOVIES");
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(Theme.SIDEBAR_DARK);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel logo = new JLabel("CINEPHILE");
        logo.setFont(Theme.FONT_BRAND);
        logo.setForeground(Theme.PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Admin Panel");
        roleLabel.setFont(Theme.FONT_CAPTION);
        roleLabel.setForeground(Theme.TEXT_MUTED);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Divider
        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(220, 1));
        divider.setBackground(Theme.DIVIDER);

        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(roleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 24)));
        sidebar.add(divider);
        sidebar.add(Box.createRigidArea(new Dimension(0, 24)));

        ModernButton btnDash = new ModernButton("Dashboard", ModernButton.Style.SECONDARY);
        btnDash.setMaximumSize(new Dimension(220, 42));
        activeSidebarBtn = btnDash;
        btnDash.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "OVERVIEW");
            setActiveSidebar(btnDash);
        });

        ModernButton btnMovies = new ModernButton("Manage Movies", ModernButton.Style.SECONDARY);
        btnMovies.setMaximumSize(new Dimension(220, 42));
        btnMovies.addActionListener(e -> {
            mainContentPanel.add(createMoviesPanel(), "MOVIES");
            cardLayout.show(mainContentPanel, "MOVIES");
            setActiveSidebar(btnMovies);
        });

        ModernButton btnLogout = new ModernButton("Logout", ModernButton.Style.GHOST);
        btnLogout.setMaximumSize(new Dimension(220, 42));
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        sidebar.add(btnDash);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnMovies);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private void setActiveSidebar(ModernButton btn) {
        activeSidebarBtn = btn;
    }

    // ─── Overview Panel ─────────────────────────────────────────────
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel header = new JLabel("Welcome back, " + admin.getFullName());
        header.setFont(Theme.FONT_HEADER);
        header.setForeground(Theme.TEXT_PRIMARY);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));
        panel.add(header, BorderLayout.NORTH);

        // Stats cards row
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        cardsRow.setPreferredSize(new Dimension(0, 120));

        List<Movie> movies = movieDAO.getAllMovies();
        long active = movies.stream().filter(Movie::isActive).count();

        cardsRow.add(createStatCard("Total Movies", String.valueOf(movies.size()), Theme.PRIMARY));
        cardsRow.add(createStatCard("Active Titles", String.valueOf(active), Theme.SUCCESS_GREEN));
        cardsRow.add(createStatCard("Revenue", "Rs. --", Theme.ACCENT_GOLD));

        panel.add(cardsRow, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String label, String value, Color accent) {
        RoundedPanel card = new RoundedPanel(16, Theme.PANEL_DARK, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Accent top bar
        JPanel bar = new JPanel();
        bar.setMaximumSize(new Dimension(40, 4));
        bar.setPreferredSize(new Dimension(40, 4));
        bar.setBackground(accent);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(Theme.FONT_TITLE);
        lblValue.setForeground(Theme.TEXT_PRIMARY);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(Theme.FONT_LABEL);
        lblLabel.setForeground(Theme.TEXT_SECONDARY);
        lblLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(bar);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(lblValue);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(lblLabel);
        return card;
    }

    // ─── Movies Panel ───────────────────────────────────────────────
    private JPanel createMoviesPanel() {
        RoundedPanel panel = new RoundedPanel(16, Theme.PANEL_DARK, true);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel lbl = new JLabel("Movie Collection");
        lbl.setFont(Theme.FONT_HEADER);
        lbl.setForeground(Theme.TEXT_PRIMARY);
        ModernButton btnAdd = new ModernButton("+ Add Movie");
        btnAdd.setPreferredSize(new Dimension(150, 40));
        btnAdd.addActionListener(e -> showAddMovieDialog());
        topPanel.add(lbl, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        List<Movie> movies = movieDAO.getAllMovies();
        String[] columns = { "ID", "Title", "Genre", "Duration (m)", "Status" };
        Object[][] data = new Object[movies.size()][5];
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            data[i] = new Object[] { m.getId(), m.getTitle(), m.getGenre(), m.getDurationMinutes(),
                    m.isActive() ? "Active" : "Archived" };
        }

        JTable table = new JTable(new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        styleTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.getViewport().setBackground(Theme.INPUT_BG);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setBackground(Theme.INPUT_BG);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setGridColor(Theme.DIVIDER);
        table.setRowHeight(36);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(Theme.withAlpha(Theme.PRIMARY, 50));
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setFont(Theme.FONT_LABEL);
        table.setFillsViewportHeight(true);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(Theme.PANEL_DARK);
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setFont(Theme.FONT_SUBHEADER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.DIVIDER));

        // Alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r,
                    int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                if (!sel) {
                    comp.setBackground(r % 2 == 0 ? Theme.INPUT_BG : Theme.CARD_DARK);
                }
                comp.setForeground(Theme.TEXT_PRIMARY);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return comp;
            }
        });
    }

    private void showAddMovieDialog() {
        JDialog dialog = new JDialog(this, "Add New Movie", true);
        dialog.setSize(420, 480);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.PANEL_DARK);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 12, 12));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(24, 24, 24, 24));

        JTextField txtTitle = new JTextField();
        JTextField txtGenre = new JTextField();
        JTextField txtDuration = new JTextField();
        JTextField txtRating = new JTextField();
        JTextField txtLanguage = new JTextField();
        JTextField[] fields = { txtTitle, txtGenre, txtDuration, txtRating, txtLanguage };
        for (JTextField f : fields) {
            f.setBackground(Theme.INPUT_BG);
            f.setForeground(Theme.TEXT_PRIMARY);
            f.setCaretColor(Theme.TEXT_PRIMARY);
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_COLOR), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        }

        form.add(makeLabel("Title:"));
        form.add(txtTitle);
        form.add(makeLabel("Genre:"));
        form.add(txtGenre);
        form.add(makeLabel("Duration (m):"));
        form.add(txtDuration);
        form.add(makeLabel("Rating:"));
        form.add(txtRating);
        form.add(makeLabel("Language:"));
        form.add(txtLanguage);

        ModernButton saveBtn = new ModernButton("Save Movie", ModernButton.Style.SUCCESS);
        saveBtn.setPreferredSize(new Dimension(0, 44));
        saveBtn.addActionListener(e -> {
            Movie m = new Movie();
            m.setTitle(txtTitle.getText());
            m.setGenre(txtGenre.getText());
            try {
                m.setDurationMinutes(Integer.parseInt(txtDuration.getText()));
            } catch (Exception ex) {
                m.setDurationMinutes(120);
            }
            m.setRating(txtRating.getText());
            m.setLanguage(txtLanguage.getText());
            m.setActive(true);
            if (movieDAO.addMovie(m)) {
                JOptionPane.showMessageDialog(dialog, "Movie added!");
                mainContentPanel.add(createMoviesPanel(), "MOVIES");
                cardLayout.show(mainContentPanel, "MOVIES");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add movie.");
            }
        });

        JPanel btnWrapper = new JPanel(new BorderLayout());
        btnWrapper.setOpaque(false);
        btnWrapper.setBorder(new EmptyBorder(0, 24, 20, 24));
        btnWrapper.add(saveBtn, BorderLayout.CENTER);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnWrapper, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JLabel makeLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(Theme.TEXT_SECONDARY);
        l.setFont(Theme.FONT_LABEL);
        return l;
    }
}
