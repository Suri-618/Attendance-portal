import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;

public class StudentRegister {
    private Point initialClick;

    public StudentRegister() {
        JFrame f = new JFrame("Register New Student");
        f.setSize(400, 560);
        f.setUndecorated(true);
        f.setLocationRelativeTo(null);

        // Gradient Background Panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(26, 31, 36);
                Color color2 = new Color(42, 51, 62);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Add a subtle border
                g2d.setColor(new Color(60, 70, 80));
                g2d.drawRect(0, 0, w - 1, h - 1);
            }
        };
        mainPanel.setLayout(null);

        // Window dragging logic
        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                mainPanel.getComponentAt(initialClick);
            }
        });
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = f.getLocation().x;
                int thisY = f.getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                f.setLocation(X, Y);
            }
        });

        // Close button
        JLabel closeLabel = new JLabel("X", SwingConstants.CENTER);
        closeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeLabel.setForeground(new Color(150, 150, 150));
        closeLabel.setBounds(360, 10, 30, 30);
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new StudentLogin();
                f.dispose();
            }

            public void mouseEntered(MouseEvent e) {
                closeLabel.setForeground(Color.RED);
            }

            public void mouseExited(MouseEvent e) {
                closeLabel.setForeground(new Color(150, 150, 150));
            }
        });
        mainPanel.add(closeLabel);

        // Back button
        JLabel backLabel = new JLabel("< Back", SwingConstants.LEFT);
        backLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backLabel.setForeground(new Color(150, 150, 150));
        backLabel.setBounds(15, 15, 60, 20);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new StudentLogin();
                f.dispose();
            }

            public void mouseEntered(MouseEvent e) {
                backLabel.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                backLabel.setForeground(new Color(150, 150, 150));
            }
        });
        mainPanel.add(backLabel);

        // Title
        JLabel titleLabel = new JLabel("Student Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 50, 400, 40);
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Create a new student account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(150, 160, 170));
        subtitleLabel.setBounds(0, 85, 400, 30);
        mainPanel.add(subtitleLabel);

        // Form Fields
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(200, 210, 220));
        nameLabel.setBounds(50, 130, 300, 20);

        JTextField nameField = new JTextField();
        styleTextField(nameField);
        nameField.setBounds(50, 155, 300, 40);

        JLabel l1 = new JLabel("Roll No:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l1.setForeground(new Color(200, 210, 220));
        l1.setBounds(50, 210, 300, 20);

        JTextField roll = new JTextField();
        styleTextField(roll);
        roll.setBounds(50, 235, 300, 40);

        JLabel l2 = new JLabel("Create Password:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l2.setForeground(new Color(200, 210, 220));
        l2.setBounds(50, 290, 300, 20);

        JPasswordField pass = new JPasswordField();
        styleTextField(pass);
        pass.setBounds(50, 315, 300, 40);

        JLabel l3 = new JLabel("Confirm Password:");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l3.setForeground(new Color(200, 210, 220));
        l3.setBounds(50, 370, 300, 20);

        JPasswordField confirmPass = new JPasswordField();
        styleTextField(confirmPass);
        confirmPass.setBounds(50, 395, 300, 40);

        JButton registerBtn = createPremiumButton("Register Account", new Color(0, 123, 255), new Color(0, 105, 217));
        registerBtn.setBounds(50, 455, 300, 45);

        registerBtn.addActionListener(e -> {
            String nameText = nameField.getText().trim();
            String rollText = roll.getText().trim();
            String pwdText = new String(pass.getPassword());
            String cPwdText = new String(confirmPass.getPassword());

            if (nameText.isEmpty() || rollText.isEmpty() || pwdText.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!pwdText.equals(cPwdText)) {
                JOptionPane.showMessageDialog(f, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection con = DBConnection.getConnection();
                if (con == null) {
                    JOptionPane.showMessageDialog(f, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement ps = con
                        .prepareStatement("INSERT INTO student (rollno, name, password) VALUES (?, ?, ?)");
                ps.setString(1, rollText);
                ps.setString(2, nameText);
                // Hash the password before storing
                ps.setString(3, PasswordUtils.hashPassword(pwdText));

                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(f, "Student Registered Successfully!\nYou can now login.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    new StudentLogin();
                    f.dispose();
                }
            } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(f, "Roll No already exists in the database!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Database Error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(l1);
        mainPanel.add(roll);
        mainPanel.add(l2);
        mainPanel.add(pass);
        mainPanel.add(l3);
        mainPanel.add(confirmPass);
        mainPanel.add(registerBtn);

        // Footer
        JLabel footerLabel = new JLabel("CANDY.IO \u00A9 2026", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 110, 120));
        footerLabel.setBounds(0, 520, 400, 30);
        mainPanel.add(footerLabel);

                mainPanel.setPreferredSize(new Dimension(f.getWidth(), f.getHeight()));
        mainPanel.setMinimumSize(new Dimension(f.getWidth(), f.getHeight()));
        mainPanel.setMaximumSize(new Dimension(f.getWidth(), f.getHeight()));
        
        JPanel wrapperPanel = new JPanel(new java.awt.GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int tw = getWidth(), th = getHeight();
                Color color1 = new Color(15, 20, 25);
                Color color2 = new Color(30, 40, 50);
                GradientPaint gp = new GradientPaint(0, 0, color1, tw, th, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, tw, th);
            }
        };
        wrapperPanel.add(mainPanel, new java.awt.GridBagConstraints());
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.add(wrapperPanel);
        f.setVisible(true);
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(45, 55, 65));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 90)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private JButton createPremiumButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed() || getModel().isPressed()) {
                    g2.setColor(hoverColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
