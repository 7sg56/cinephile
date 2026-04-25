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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        setTitle("Cinephile - Now Showing");
        setSize(1280, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_DARK);

        add(createNavBar(), BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(20, 30, 20, 30));
        contentWrapper.add(createSectionHeader(), BorderLayout.NORTH);

        moviesGridPanel = new JPanel();
        moviesGridPanel.setOpaque(false);
        moviesGridPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        populateMovies();

        JScrollPane scrollPane = new JScrollPane(moviesGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_DARK);
        contentWrapper.add(scrollPane, BorderLayout.CENTER);

        add(contentWrapper, BorderLayout.CENTER);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(Theme.PANEL_DARK);
        navBar.setBorder(new EmptyBorder(12, 30, 12, 30));
        navBar.setPreferredSize(new Dimension(0, 60));

        JLabel logo = new JLabel("CINEPHILE");
        logo.setFont(Theme.FONT_BRAND);
        logo.setForeground(Theme.PRIMARY);

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightNav.setOpaque(false);

        JPanel avatar = createAvatarCircle();
        JLabel userLabel = new JLabel(currentUser.getFullName());
        userLabel.setFont(Theme.FONT_LABEL);
        userLabel.setForeground(Theme.TEXT_PRIMARY);

        ModernButton btnLogout = new ModernButton("Logout", ModernButton.Style.GHOST);
        btnLogout.setPreferredSize(new Dimension(100, 36));
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        rightNav.add(avatar);
        rightNav.add(userLabel);
        rightNav.add(btnLogout);
        navBar.add(logo, BorderLayout.WEST);
        navBar.add(rightNav, BorderLayout.EAST);
        return navBar;
    }

    private JPanel createAvatarCircle() {
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(Theme.createPrimaryGradient(getWidth(), getHeight()));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(Theme.FONT_SUBHEADER);
                FontMetrics fm = g2.getFontMetrics();
                String init = currentUser.getFullName().substring(0, 1).toUpperCase();
                g2.drawString(init, (getWidth() - fm.stringWidth(init)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setOpaque(false);
        return avatar;
    }

    private JPanel createSectionHeader() {
        JPanel sectionBlock = new JPanel();
        sectionBlock.setOpaque(false);
        sectionBlock.setLayout(new BoxLayout(sectionBlock, BoxLayout.Y_AXIS));
        sectionBlock.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel title = new JLabel("Now Showing");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_PRIMARY);
        sectionBlock.add(title);
        JPanel underline = new JPanel();
        underline.setBackground(Theme.PRIMARY);
        underline.setMaximumSize(new Dimension(60, 3));
        underline.setPreferredSize(new Dimension(60, 3));
        sectionBlock.add(Box.createRigidArea(new Dimension(0, 6)));
        sectionBlock.add(underline);
        return sectionBlock;
    }

    private void populateMovies() {
        List<Movie> movies = movieDAO.getAllActiveMovies();
        int rows = (int) Math.ceil((double) movies.size() / 4);
        moviesGridPanel.setPreferredSize(new Dimension(1100, Math.max(700, rows * 460)));
        for (Movie movie : movies) {
            moviesGridPanel.add(createMovieCard(movie));
        }
    }

    private JPanel createMovieCard(Movie movie) {
        RoundedPanel card = new RoundedPanel(16, Theme.CARD_DARK);
        card.setPreferredSize(new Dimension(270, 420));
        card.setLayout(new BorderLayout());
        card.setCursor(Theme.HAND_CURSOR);

        card.add(createPosterPanel(movie), BorderLayout.CENTER);
        card.add(createCardInfo(movie), BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorderColor(Theme.PRIMARY);
                card.setShowBorder(true);
                card.repaint();
            }

            public void mouseExited(MouseEvent e) {
                card.setShowBorder(false);
                card.repaint();
            }
        });
        return card;
    }

    private JPanel createPosterPanel(Movie movie) {
        JPanel posterPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(45, 25, 30), 0, getHeight(), new Color(20, 15, 25)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                try {
                    String p = movie.getPosterPath();
                    if (p != null && !p.isEmpty()) {
                        Image img = new ImageIcon(getClass().getResource(p)).getImage();
                        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                        g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    }
                } catch (Exception e) {
                    g2.setColor(Theme.withAlpha(Color.WHITE, 40));
                    g2.setFont(new Font("SansSerif", Font.BOLD, 48));
                    FontMetrics fm = g2.getFontMetrics();
                    String i = movie.getTitle().substring(0, 1).toUpperCase();
                    g2.drawString(i, (getWidth() - fm.stringWidth(i)) / 2, getHeight() / 2 + fm.getAscent() / 3);
                }
                g2.setPaint(new GradientPaint(0, getHeight() * 0.5f, new Color(0, 0, 0, 0), 0, getHeight(),
                        new Color(0, 0, 0, 180)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        posterPanel.setPreferredSize(new Dimension(270, 300));
        posterPanel.setOpaque(false);
        posterPanel.setLayout(new BorderLayout());

        JLabel ratingBadge = new JLabel(movie.getRating() != null ? movie.getRating() : "NR") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.withAlpha(Color.BLACK, 160));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        ratingBadge.setFont(Theme.FONT_CAPTION);
        ratingBadge.setForeground(Theme.ACCENT_GOLD);
        ratingBadge.setHorizontalAlignment(SwingConstants.CENTER);
        ratingBadge.setPreferredSize(new Dimension(36, 22));
        ratingBadge.setOpaque(false);
        JPanel bw = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bw.setOpaque(false);
        bw.add(ratingBadge);
        posterPanel.add(bw, BorderLayout.NORTH);
        return posterPanel;
    }

    private JPanel createCardInfo(Movie movie) {
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(12, 16, 14, 16));

        JLabel lblTitle = new JLabel(movie.getTitle());
        lblTitle.setFont(Theme.FONT_SUBHEADER);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        String meta = (movie.getGenre() != null ? movie.getGenre() : "") + " | " + movie.getDurationMinutes() + "m";
        JLabel lblMeta = new JLabel(meta);
        lblMeta.setFont(Theme.FONT_CAPTION);
        lblMeta.setForeground(Theme.TEXT_MUTED);
        lblMeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernButton bookBtn = new ModernButton("Book Tickets");
        bookBtn.setMaximumSize(new Dimension(240, 36));
        bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookBtn.addActionListener(e -> openBookingFlow(movie));

        infoPanel.add(lblTitle);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        infoPanel.add(lblMeta);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(bookBtn);
        return infoPanel;
    }

    private void openBookingFlow(Movie movie) {
        com.cinephile.dao.ShowDAO showDAO = new com.cinephile.dao.ShowDAO();
        List<com.cinephile.model.Show> shows = showDAO.getShowsForMovie(movie.getId());
        if (shows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active shows currently available.");
            return;
        }
        com.cinephile.model.Show selected = shows.get(0);
        if (shows.size() > 1) {
            com.cinephile.model.Show sel = (com.cinephile.model.Show) JOptionPane.showInputDialog(
                    this, "Select a show time:", "Shows", JOptionPane.PLAIN_MESSAGE,
                    null, shows.toArray(), shows.get(0));
            if (sel != null)
                selected = sel;
            else
                return;
        }
        com.cinephile.dao.BookingDAO bDao = new com.cinephile.dao.BookingDAO();
        int booked = bDao.getBookedSeatNumbersForShow(selected.getId()).size();
        int confirm = JOptionPane.showConfirmDialog(this,
                selected.getShowTime() + " | " + selected.getHallName() + "\n" +
                        "Base: Rs. " + selected.getBasePrice() + "\nAvailable: " + (60 - booked) + "/60\n\nProceed?",
                "Availability", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION)
            new BookingFlowFrame(currentUser, selected).setVisible(true);
    }
}
