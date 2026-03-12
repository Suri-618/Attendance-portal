import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class FacultyLogin {
    private Point initialClick;

    public FacultyLogin() {
        JFrame f = new JFrame("Faculty Login");
        f.setSize(400, 500);
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
                new MainFSA();
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

        // Title
        JLabel titleLabel = new JLabel("Faculty Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 50, 400, 40);
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Enter your credentials below", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(150, 160, 170));
        subtitleLabel.setBounds(0, 90, 400, 30);
        mainPanel.add(subtitleLabel);

        JLabel l1 = new JLabel("Faculty ID");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l1.setForeground(new Color(200, 210, 220));
        l1.setBounds(50, 140, 300, 20);

        JTextField id = new JTextField();
        id.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        id.setBackground(new Color(45, 55, 65));
        id.setForeground(Color.WHITE);
        id.setCaretColor(Color.WHITE);
        id.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 90)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        id.setBounds(50, 165, 300, 40);

        JLabel l2 = new JLabel("Password");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l2.setForeground(new Color(200, 210, 220));
        l2.setBounds(50, 220, 300, 20);

        JPasswordField pass = new JPasswordField();
        pass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pass.setBackground(new Color(45, 55, 65));
        pass.setForeground(Color.WHITE);
        pass.setCaretColor(Color.WHITE);
        pass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 90)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        pass.setBounds(50, 245, 300, 40);

        JButton login = createPremiumButton("Login", new Color(40, 167, 69), new Color(33, 136, 56));
        login.setBounds(50, 310, 300, 45);

        JButton register = createPremiumButton("Register", new Color(0, 123, 255),
                new Color(0, 105, 217));
        register.setBounds(50, 365, 300, 45);

        // Back button
        JLabel backLabel = new JLabel("< Back", SwingConstants.LEFT);
        backLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backLabel.setForeground(new Color(150, 150, 150));
        backLabel.setBounds(15, 15, 60, 20);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new MainFSA();
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

        login.addActionListener(e -> {
            try {
                Connection con = DBConnection.getConnection();
                String sql = "SELECT * FROM faculty WHERE faculty_id=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, id.getText());
                // Hash the input password before comparing
                ps.setString(2, PasswordUtils.hashPassword(new String(pass.getPassword())));
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    new FacultyDashboard(id.getText());
                    f.dispose();
                } else {
                    JOptionPane.showMessageDialog(f, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        register.addActionListener(e -> {
            new FacultyRegister();
            f.dispose();
        });

        mainPanel.add(l1);
        mainPanel.add(l2);
        mainPanel.add(id);
        mainPanel.add(pass);
        mainPanel.add(login);
        mainPanel.add(register);

        // Footer
        JLabel footerLabel = new JLabel("CANDY.IO \u00A9 2026", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 110, 120));
        footerLabel.setBounds(0, 450, 400, 30);
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
