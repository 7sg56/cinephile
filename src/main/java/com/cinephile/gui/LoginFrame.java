package com.cinephile.gui;

import com.cinephile.dao.UserDAO;
import com.cinephile.model.User;
import com.cinephile.util.Theme;
import com.cinephile.gui.components.ModernButton;
import com.cinephile.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Cinephile - Login");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(Theme.BACKGROUND_DARK);

        // ═══════════════ LEFT PANEL: Cinematic Branding ═══════════════
        Image heroImg;
        try {
            heroImg = new ImageIcon(getClass().getResource("/ui/cinema_hero.png")).getImage();
        } catch (Exception e) {
            heroImg = null;
        }
        final Image finalHeroImg = heroImg;

        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (finalHeroImg != null) {
                    // Draw hero image scaled to fill
                    g2.drawImage(finalHeroImg, 0, 0, getWidth(), getHeight(), this);
                    // Dark gradient overlay for text readability
                    g2.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 120), 0, getHeight(),
                            new Color(10, 10, 14, 230)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(25, 5, 8), 0, getHeight(), new Color(10, 10, 14)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                // Subtle accent glow
                g2.setPaint(new RadialGradientPaint(
                        new Point(getWidth() / 2, 80), getWidth() / 2f,
                        new float[] { 0f, 1f },
                        new Color[] { Theme.withAlpha(Theme.PRIMARY, 35), new Color(0, 0, 0, 0) }));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setOpaque(false);

        JPanel brandBox = new JPanel();
        brandBox.setOpaque(false);
        brandBox.setLayout(new BoxLayout(brandBox, BoxLayout.Y_AXIS));

        JLabel brandName = new JLabel("CINEPHILE");
        brandName.setFont(new Font("SansSerif", Font.BOLD, 48));
        brandName.setForeground(Theme.PRIMARY);
        brandName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Your Premium Cinema Experience");
        tagline.setFont(Theme.FONT_REGULAR);
        tagline.setForeground(Theme.TEXT_SECONDARY);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline2 = new JLabel("Book tickets, pick your seats, grab snacks.");
        tagline2.setFont(Theme.FONT_CAPTION);
        tagline2.setForeground(Theme.TEXT_MUTED);
        tagline2.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandBox.add(brandName);
        brandBox.add(Box.createRigidArea(new Dimension(0, 12)));
        brandBox.add(tagline);
        brandBox.add(Box.createRigidArea(new Dimension(0, 6)));
        brandBox.add(tagline2);

        leftPanel.add(brandBox);

        // ═══════════════ RIGHT PANEL: Login Form ═══════════════
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Theme.BACKGROUND_DARK);

        RoundedPanel loginBox = new RoundedPanel(20, Theme.PANEL_DARK, true);
        loginBox.setPreferredSize(new Dimension(380, 440));
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(Theme.FONT_HEADER);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(Theme.FONT_LABEL);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = createModernTextField("Email Address");
        passwordField = createModernPasswordField("Password");

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Theme.PRIMARY);
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ModernButton loginBtn = new ModernButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(300, 48));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pwd = new String(passwordField.getPassword());

            if (email.isEmpty() || pwd.isEmpty()) {
                errorLabel.setText("Please enter both email and password.");
                return;
            }

            User user = userDAO.authenticate(email, pwd);
            if (user != null) {
                errorLabel.setText(" ");
                if (user.getRole().equalsIgnoreCase("ADMIN")) {
                    new AdminDashboardFrame(user).setVisible(true);
                } else {
                    new ClientDashboardFrame(user).setVisible(true);
                }
                dispose();
            } else {
                errorLabel.setText("Invalid email or password.");
            }
        });

        // Make all form elements left-aligned for consistent BoxLayout.Y_AXIS
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble form
        loginBox.add(titleLabel);
        loginBox.add(Box.createRigidArea(new Dimension(0, 6)));
        loginBox.add(subtitleLabel);
        loginBox.add(Box.createRigidArea(new Dimension(0, 35)));
        loginBox.add(createFieldLabel("Email"));
        loginBox.add(Box.createRigidArea(new Dimension(0, 6)));
        loginBox.add(emailField);
        loginBox.add(Box.createRigidArea(new Dimension(0, 20)));
        loginBox.add(createFieldLabel("Password"));
        loginBox.add(Box.createRigidArea(new Dimension(0, 6)));
        loginBox.add(passwordField);
        loginBox.add(Box.createRigidArea(new Dimension(0, 12)));
        loginBox.add(errorLabel);
        loginBox.add(Box.createRigidArea(new Dimension(0, 20)));
        loginBox.add(loginBtn);

        rightPanel.add(loginBox);

        root.add(leftPanel);
        root.add(rightPanel);
        add(root);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(300, 20));
        return lbl;
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(300, 44));
        tf.setPreferredSize(new Dimension(300, 44));
        tf.setBackground(Theme.INPUT_BG);
        tf.setForeground(Theme.TEXT_PRIMARY);
        tf.setCaretColor(Theme.TEXT_PRIMARY);
        tf.setFont(Theme.FONT_REGULAR);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        attachFocusGlow(tf);
        return tf;
    }

    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setMaximumSize(new Dimension(300, 44));
        pf.setPreferredSize(new Dimension(300, 44));
        pf.setBackground(Theme.INPUT_BG);
        pf.setForeground(Theme.TEXT_PRIMARY);
        pf.setCaretColor(Theme.TEXT_PRIMARY);
        pf.setFont(Theme.FONT_REGULAR);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        attachFocusGlow(pf);
        return pf;
    }

    private void attachFocusGlow(JComponent field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.PRIMARY, 2, true),
                        BorderFactory.createEmptyBorder(7, 13, 7, 13)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
                        BorderFactory.createEmptyBorder(8, 14, 8, 14)));
            }
        });
    }
}
