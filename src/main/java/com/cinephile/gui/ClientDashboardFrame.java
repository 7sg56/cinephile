package com.cinephile.gui;

import com.cinephile.dao.MovieDAO;
import com.cinephile.model.Movie;
import com.cinephile.model.User;
import com.cinephile.util.Theme;
import com.cinephile.gui.components.ModernButton;
import com.cinephile.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ClientDashboardFrame extends JFrame {
    private User currentUser;
    private MovieDAO movieDAO;
    private JPanel moviesGridPanel;

    public ClientDashboardFrame(User user) {
        this.currentUser = user;
        this.movieDAO = new MovieDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Cinephile - Movies");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        // Top Navigation Bar
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(Theme.PANEL_DARK);
        navBar.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel logo = new JLabel("CINEPHILE");
        logo.setFont(Theme.FONT_TITLE);
        logo.setForeground(Theme.PRIMARY);

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightNav.setOpaque(false);

        JLabel userLabel = new JLabel("Hi, " + currentUser.getFullName());
        userLabel.setFont(Theme.FONT_REGULAR);
        userLabel.setForeground(Theme.TEXT_PRIMARY);

        ModernButton btnLogout = new ModernButton("Logout");
        btnLogout.setPreferredSize(new Dimension(130, 40));
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        rightNav.add(userLabel);
        rightNav.add(btnLogout);

        navBar.add(logo, BorderLayout.WEST);
        navBar.add(rightNav, BorderLayout.EAST);
        add(navBar, BorderLayout.NORTH);

        // Main Content Area: Scrollable Grid of Movies
        moviesGridPanel = new JPanel();
        moviesGridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        moviesGridPanel.setBackground(Theme.BACKGROUND_DARK);

        populateMovies();

        // Need a wrapper to ensure FlowLayout wraps correctly in JScrollPane
        // Wrap Layout might be better, but we'll simulate wrapping with a smart
        // preferred height
        JScrollPane scrollPane = new JScrollPane(moviesGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateMovies() {
        List<Movie> movies = movieDAO.getAllActiveMovies();

        // Calculate rows needed for preferred size assuming ~300px per card and 4
        // columns
        int rows = (int) Math.ceil((double) movies.size() / 4);
        moviesGridPanel.setPreferredSize(new Dimension(1000, Math.max(700, rows * 450)));

        for (Movie movie : movies) {
            moviesGridPanel.add(createMovieCard(movie));
        }
    }

    private JPanel createMovieCard(Movie movie) {
        RoundedPanel card = new RoundedPanel(20, Theme.PANEL_DARK);
        card.setPreferredSize(new Dimension(250, 400));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Poster Placeholder (Gray Box)
        JPanel posterBox = new JPanel();
        posterBox.setBackground(Theme.INPUT_BG);
        posterBox.setMaximumSize(new Dimension(220, 260));
        posterBox.setPreferredSize(new Dimension(220, 260));
        posterBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblPosterAlt = new JLabel("POSTER");
        lblPosterAlt.setForeground(Theme.BORDER_COLOR);
        posterBox.add(lblPosterAlt);

        // Details
        JLabel lblTitle = new JLabel(movie.getTitle());
        lblTitle.setFont(Theme.FONT_HEADER);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMeta = new JLabel(movie.getGenre() + " • " + movie.getDurationMinutes() + "m • " + movie.getRating());
        lblMeta.setFont(Theme.FONT_SMALL);
        lblMeta.setForeground(Theme.TEXT_SECONDARY);
        lblMeta.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton bookBtn = new ModernButton("Book Tickets");
        bookBtn.setMaximumSize(new Dimension(200, 40));
        bookBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookBtn.addActionListener(e -> {
            com.cinephile.dao.ShowDAO showDAO = new com.cinephile.dao.ShowDAO();
            List<com.cinephile.model.Show> shows = showDAO.getShowsForMovie(movie.getId());

            if (shows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No active shows currently available for this movie.");
                return;
            }

            com.cinephile.model.Show selectedShow = shows.get(0);
            if (shows.size() > 1) {
                com.cinephile.model.Show selection = (com.cinephile.model.Show) JOptionPane.showInputDialog(
                        this, "Select a show time:\n", "Shows Available", JOptionPane.PLAIN_MESSAGE,
                        null, shows.toArray(), shows.get(0));
                if (selection != null) {
                    selectedShow = selection;
                } else {
                    return; // user cancelled
                }
            }

            new BookingFlowFrame(currentUser, selectedShow).setVisible(true);
        });

        card.add(posterBox);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblMeta);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(bookBtn);

        return card;
    }
}
