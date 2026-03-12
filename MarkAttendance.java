import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class MarkAttendance {
    private Point initialClick;

    public MarkAttendance(String facultyId) {

        JFrame f = new JFrame("Mark Attendance");
        f.setSize(750, 500);
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

                // subtle border
                g2d.setColor(new Color(60, 70, 80));
                g2d.drawRect(0, 0, w - 1, h - 1);
            }
        };
        mainPanel.setLayout(null);

        // Window dragging logic
        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = f.getLocation().x;
                int thisY = f.getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                f.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        // Header Section
        JLabel backLabel = new JLabel("< Back");
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

        JLabel closeLabel = new JLabel("X");
        closeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeLabel.setForeground(new Color(150, 150, 150));
        closeLabel.setBounds(720, 10, 30, 30);
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

        JLabel titleLabel = new JLabel("MARK ATTENDANCE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 15, 750, 30);
        mainPanel.add(titleLabel);

        // Inputs Section
        JLabel l1 = new JLabel("Session ID:");
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        l1.setForeground(new Color(200, 210, 220));
        l1.setBounds(40, 70, 100, 30);
        mainPanel.add(l1);

        JTextField tfSession = new JTextField();
        styleTextField(tfSession);
        tfSession.setBounds(130, 70, 150, 35);
        mainPanel.add(tfSession);

        JButton loadBtn = createPremiumButton("Load Students", new Color(0, 123, 255), new Color(0, 105, 217));
        loadBtn.setBounds(300, 70, 150, 35);
        mainPanel.add(loadBtn);

        JButton generateQRBtn = createPremiumButton("Generate QR", new Color(111, 66, 193), new Color(102, 16, 242));
        generateQRBtn.setBounds(470, 70, 150, 35);
        mainPanel.add(generateQRBtn);

        // Table Model Setup
        String[] cols = { "Roll No", "Name", "Present" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 2 ? Boolean.class : String.class;
            }

            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        JTable table = new JTable(model);
        setupPremiumTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(new Color(26, 31, 36));
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 80)));
        sp.setBounds(40, 130, 670, 250);
        mainPanel.add(sp);

        // Save Button
        JButton saveBtn = createPremiumButton("Save Attendance", new Color(40, 167, 69), new Color(33, 136, 56));
        saveBtn.setBounds(275, 410, 200, 45);
        mainPanel.add(saveBtn);

        // Actions
        loadBtn.addActionListener(e -> {
            model.setRowCount(0);

            String sessionId = tfSession.getText();
            if (sessionId.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Enter Session ID", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Connection con = DBConnection.getConnection();
                String sql = "SELECT rollno, name FROM student";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[] {
                            rs.getString("rollno"),
                            rs.getString("name"),
                            false
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(f, "Error loading students", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        generateQRBtn.addActionListener(e -> {
            String sessionId = tfSession.getText();
            if (sessionId.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Enter Session ID", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(sessionId, BarcodeFormat.QR_CODE, 300, 300);
                java.awt.image.BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

                JDialog dialog = new JDialog(f, "QR Code: " + sessionId, true);
                dialog.setSize(350, 400);
                dialog.setLocationRelativeTo(f);
                dialog.setLayout(new BorderLayout());

                JLabel imageLabel = new JLabel(new ImageIcon(qrImage), SwingConstants.CENTER);
                dialog.add(imageLabel, BorderLayout.CENTER);

                JLabel info = new JLabel("Students: Scan this to mark attendance.", SwingConstants.CENTER);
                info.setFont(new Font("Segoe UI", Font.BOLD, 14));
                info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                dialog.add(info, BorderLayout.SOUTH);

                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(f, "Error generating QR Code", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        saveBtn.addActionListener(e -> {
            String sessionId = tfSession.getText();

            if (sessionId.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Enter Session ID", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Connection con = DBConnection.getConnection();
                String sql = "INSERT INTO attendance (session_id, regno, status) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);

                for (int i = 0; i < model.getRowCount(); i++) {
                    String roll = model.getValueAt(i, 0).toString();
                    boolean present = (boolean) model.getValueAt(i, 2);
                    String status = present ? "Present" : "Absent";

                    ps.setString(1, sessionId);
                    ps.setString(2, roll);
                    ps.setString(3, status);

                    ps.addBatch();
                }

                ps.executeBatch();
                JOptionPane.showMessageDialog(f, "Attendance Saved Successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                new FacultyDashboard(facultyId);
                f.dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(f, "Error saving attendance", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupPremiumTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(new Color(36, 41, 46));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(60, 70, 80));
        table.setSelectionBackground(new Color(0, 123, 255));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(20, 25, 30));
        header.setForeground(new Color(200, 210, 220));
        header.setPreferredSize(new Dimension(100, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }
}
