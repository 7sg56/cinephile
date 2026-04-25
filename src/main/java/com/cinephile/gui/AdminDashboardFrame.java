package com.cinephile.gui;

import com.cinephile.dao.MovieDAO;
import com.cinephile.model.Movie;
import com.cinephile.model.User;
import com.cinephile.util.Theme;
import com.cinephile.gui.components.ModernButton;
import com.cinephile.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {
    private User admin;
    private MovieDAO movieDAO;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public AdminDashboardFrame(User adminUser) {
        this.admin = adminUser;
        this.movieDAO = new MovieDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Cinephile Admin Dashboard - " + admin.getFullName());
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main Content Area with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.BACKGROUND_DARK);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create individual cards
        mainContentPanel.add(createDashboardOverviewPanel(), "OVERVIEW");
        mainContentPanel.add(createMoviesPanel(), "MOVIES");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(Theme.PANEL_DARK);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel logo = new JLabel("CINEPHILE");
        logo.setFont(Theme.FONT_TITLE);
        logo.setForeground(Theme.PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Admin Panel");
        roleLabel.setFont(Theme.FONT_SMALL);
        roleLabel.setForeground(Theme.TEXT_SECONDARY);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(roleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 50)));

        ModernButton btnDashboard = new ModernButton("Dashboard");
        btnDashboard.setMaximumSize(new Dimension(200, 40));
        btnDashboard.addActionListener(e -> cardLayout.show(mainContentPanel, "OVERVIEW"));

        ModernButton btnMovies = new ModernButton("Manage Movies");
        btnMovies.setMaximumSize(new Dimension(200, 40));
        btnMovies.addActionListener(e -> {
            // refresh data whenever opened
            mainContentPanel.add(createMoviesPanel(), "MOVIES");
            cardLayout.show(mainContentPanel, "MOVIES");
        });

        ModernButton btnLogout = new ModernButton("Logout");
        btnLogout.setMaximumSize(new Dimension(200, 40));
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        sidebar.add(btnDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(btnMovies);
        sidebar.add(Box.createVerticalGlue()); // Push logout to bottom
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JPanel createDashboardOverviewPanel() {
        RoundedPanel panel = new RoundedPanel(20, Theme.BACKGROUND_DARK);
        panel.setLayout(new BorderLayout());

        JLabel header = new JLabel("Welcome back, " + admin.getFullName());
        header.setFont(Theme.FONT_HEADER);
        header.setForeground(Theme.TEXT_PRIMARY);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel info = new JLabel("More dashboard widgets (revenue, quick stats) will go here.");
        info.setFont(Theme.FONT_REGULAR);
        info.setForeground(Theme.TEXT_SECONDARY);

        panel.add(header, BorderLayout.NORTH);
        panel.add(info, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMoviesPanel() {
        RoundedPanel panel = new RoundedPanel(20, Theme.PANEL_DARK);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header and Add Button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTarget = new JLabel("Movie Collection");
        lblTarget.setFont(Theme.FONT_HEADER);
        lblTarget.setForeground(Theme.TEXT_PRIMARY);

        ModernButton btnAdd = new ModernButton("+ Add Movie");
        btnAdd.setPreferredSize(new Dimension(150, 40));
        btnAdd.addActionListener(e -> showAddMovieDialog());

        topPanel.add(lblTarget, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(topPanel, BorderLayout.NORTH);

        // Fetch Movies & Build Table
        List<Movie> movies = movieDAO.getAllMovies();
        String[] columns = { "ID", "Title", "Genre", "Duration (m)", "Status" };
        Object[][] data = new Object[movies.size()][5];
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            data[i][0] = m.getId();
            data[i][1] = m.getTitle();
            data[i][2] = m.getGenre();
            data[i][3] = m.getDurationMinutes();
            data[i][4] = m.isActive() ? "Active" : "Archived";
        }

        JTable table = new JTable(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.setBackground(Theme.INPUT_BG);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Theme.INPUT_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showAddMovieDialog() {
        // Simplified form for adding movies
        JDialog dialog = new JDialog(this, "Add New Movie", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.getContentPane().setBackground(Theme.PANEL_DARK);

        JPanel pnl = new JPanel(new GridLayout(6, 2, 10, 10));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField txtTitle = new JTextField();
        JTextField txtGenre = new JTextField();
        JTextField txtDuration = new JTextField();
        JTextField txtRating = new JTextField();
        JTextField txtLanguage = new JTextField();

        pnl.add(createLabel("Title:"));
        pnl.add(txtTitle);
        pnl.add(createLabel("Genre:"));
        pnl.add(txtGenre);
        pnl.add(createLabel("Duration (m):"));
        pnl.add(txtDuration);
        pnl.add(createLabel("Rating:"));
        pnl.add(txtRating);
        pnl.add(createLabel("Language:"));
        pnl.add(txtLanguage);

        ModernButton saveBtn = new ModernButton("Save");
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
                JOptionPane.showMessageDialog(dialog, "Movie added successfully!");
                // refresh via action
                mainContentPanel.add(createMoviesPanel(), "MOVIES");
                cardLayout.show(mainContentPanel, "MOVIES");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add movie.");
            }
        });

        dialog.add(pnl);
        dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    private JLabel createLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(Theme.TEXT_SECONDARY);
        return l;
    }
}
