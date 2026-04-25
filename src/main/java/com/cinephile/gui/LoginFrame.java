package com.cinephile.gui;

import com.cinephile.dao.UserDAO;
import com.cinephile.model.User;
import com.cinephile.util.Theme;
import com.cinephile.gui.components.ModernButton;
import com.cinephile.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Background Layer
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(Theme.BACKGROUND_DARK);
        backgroundPanel.setLayout(new GridBagLayout());

        // Container for Login box
        RoundedPanel loginBox = new RoundedPanel(25, Theme.PANEL_DARK);
        loginBox.setPreferredSize(new Dimension(400, 480));
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Logo / Title
        JLabel titleLabel = new JLabel("CINEPHILE");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign In");
        subtitleLabel.setFont(Theme.FONT_HEADER);
        subtitleLabel.setForeground(Theme.TEXT_PRIMARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input Setup Helpers
        emailField = createModernTextField("Email Address");
        passwordField = createModernPasswordField("Password");

        // Error message label
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Theme.PRIMARY); // red color error
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login Button
        ModernButton loginBtn = new ModernButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(320, 45));
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
                dispose(); // close login window
            } else {
                errorLabel.setText("Invalid email or password.");
            }
        });

        // Assemble
        loginBox.add(titleLabel);
        loginBox.add(Box.createRigidArea(new Dimension(0, 30)));
        loginBox.add(subtitleLabel);
        loginBox.add(Box.createRigidArea(new Dimension(0, 30)));
        loginBox.add(createLabel("Email"));
        loginBox.add(Box.createRigidArea(new Dimension(0, 5)));
        loginBox.add(emailField);
        loginBox.add(Box.createRigidArea(new Dimension(0, 20)));
        loginBox.add(createLabel("Password"));
        loginBox.add(Box.createRigidArea(new Dimension(0, 5)));
        loginBox.add(passwordField);
        loginBox.add(Box.createRigidArea(new Dimension(0, 10)));
        loginBox.add(errorLabel);
        loginBox.add(Box.createRigidArea(new Dimension(0, 20)));
        loginBox.add(loginBtn);

        backgroundPanel.add(loginBox);
        add(backgroundPanel);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_REGULAR);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(320, 40));
        tf.setBackground(Theme.INPUT_BG);
        tf.setForeground(Theme.TEXT_PRIMARY);
        tf.setCaretColor(Color.WHITE);
        tf.setFont(Theme.FONT_REGULAR);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return tf;
    }

    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setMaximumSize(new Dimension(320, 40));
        pf.setBackground(Theme.INPUT_BG);
        pf.setForeground(Theme.TEXT_PRIMARY);
        pf.setCaretColor(Color.WHITE);
        pf.setFont(Theme.FONT_REGULAR);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return pf;
    }
}
