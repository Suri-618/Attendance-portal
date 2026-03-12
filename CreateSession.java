import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import javax.swing.*;

public class CreateSession {
    private Point initialClick;

    public CreateSession(String facultyId) {

        JFrame f = new JFrame("Create Session");
        f.setSize(450, 480);
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
        closeLabel.setBounds(410, 10, 30, 30);
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new FacultyDashboard(facultyId);
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
                new FacultyDashboard(facultyId);
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
        JLabel titleLabel = new JLabel("Create Session", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 50, 450, 40);
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Enter session details below", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(150, 160, 170));
        subtitleLabel.setBounds(0, 90, 450, 30);
        mainPanel.add(subtitleLabel);

        // Input Fields
        JLabel l1 = new JLabel("Class Name (Session ID)");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l1.setForeground(new Color(200, 210, 220));
        l1.setBounds(75, 140, 300, 20);

        JTextField t1 = new JTextField();
        t1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t1.setBackground(new Color(45, 55, 65));
        t1.setForeground(Color.WHITE);
        t1.setCaretColor(Color.WHITE);
        t1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 90)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        t1.setBounds(75, 165, 300, 40);

        JLabel l2 = new JLabel("Session Date (YYYY-MM-DD)");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l2.setForeground(new Color(200, 210, 220));
        l2.setBounds(75, 220, 300, 20);

        JTextField t2 = new JTextField("YYYY-MM-DD");
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t2.setBackground(new Color(45, 55, 65));
        t2.setForeground(new Color(150, 150, 150)); // Placeholder color
        t2.setCaretColor(Color.WHITE);
        t2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 90)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        t2.setBounds(75, 245, 300, 40);

        // Add focus listener for placeholder behavior on date field
        t2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (t2.getText().equals("YYYY-MM-DD")) {
                    t2.setText("");
                    t2.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (t2.getText().isEmpty()) {
                    t2.setForeground(new Color(150, 150, 150));
                    t2.setText("YYYY-MM-DD");
                }
            }
        });

        // Create Button
        JButton btn = createPremiumButton("Create", new Color(0, 123, 255), new Color(0, 105, 217));
        btn.setBounds(75, 330, 300, 45);

        btn.addActionListener(e -> {
            try {
                Connection con = DBConnection.getConnection();
                String sql = "INSERT INTO class_session (session_id, session_date, created_by) VALUES (?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, t1.getText());

                String dateStr = t2.getText();
                if (dateStr.equals("YYYY-MM-DD")) {
                    throw new Exception("Please enter a valid date.");
                }

                ps.setDate(2, Date.valueOf(dateStr));
                ps.setString(3, facultyId);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(f, "Session Created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                new FacultyDashboard(facultyId);
                f.dispose();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(f, "Invalid Date Format. Please use YYYY-MM-DD.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(l1);
        mainPanel.add(l2);
        mainPanel.add(t1);
        mainPanel.add(t2);
        mainPanel.add(btn);

        // Footer
        JLabel footerLabel = new JLabel("CANDY.IO \u00A9 2026", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 110, 120));
        footerLabel.setBounds(0, 430, 450, 30);
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
