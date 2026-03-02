import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FacultyDashboard {
    private Point initialClick;

    public FacultyDashboard(String facultyId) {

        JFrame f = new JFrame("Faculty Dashboard");
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
                System.exit(0);
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
        JLabel titleLabel = new JLabel("Faculty Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 50, 450, 40);
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Welcome, ID: " + facultyId, SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(150, 160, 170));
        subtitleLabel.setBounds(0, 90, 450, 30);
        mainPanel.add(subtitleLabel);

        // Buttons
        JButton cs = createPremiumButton("Create Session", new Color(0, 123, 255), new Color(0, 105, 217));
        JButton ma = createPremiumButton("Mark Attendance", new Color(40, 167, 69), new Color(33, 136, 56));
        JButton lo = createPremiumButton("Logout", new Color(220, 53, 69), new Color(200, 35, 51));

        cs.setBounds(100, 160, 250, 50);
        ma.setBounds(100, 230, 250, 50);
        lo.setBounds(100, 300, 250, 50);

        cs.addActionListener(e -> {
            new CreateSession(facultyId);
            f.dispose();
        });

        ma.addActionListener(e -> {
            new MarkAttendance(facultyId);
            f.dispose();
        });

        lo.addActionListener(e -> {
            new MainFSA();
            f.dispose();
        });

        mainPanel.add(cs);
        mainPanel.add(ma);
        mainPanel.add(lo);

        // Footer
        JLabel footerLabel = new JLabel("JUNO University Portal \u00A9 2026", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 110, 120));
        footerLabel.setBounds(0, 430, 450, 30);
        mainPanel.add(footerLabel);

        f.add(mainPanel);
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
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
