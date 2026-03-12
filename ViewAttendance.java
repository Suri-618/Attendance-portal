import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;

public class ViewAttendance {
    private Point initialClick;
    private DefaultTableModel model;
    private String studentRollNo;
    private JFrame f;
    private JLabel lblTotal, lblPresent, lblPercentage;

    public ViewAttendance(String rollNo) {
        this.studentRollNo = rollNo;

        f = new JFrame("View Attendance");
        f.setSize(650, 600);
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

                g2d.setColor(new Color(60, 70, 80));
                g2d.drawRect(0, 0, w - 1, h - 1);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Header Panel (Top Custom Frame)
        JPanel headerPanel = new JPanel(null);
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(650, 50));

        // Window dragging logic
        headerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = f.getLocation().x;
                int thisY = f.getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                f.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        JLabel backLabel = new JLabel("< Back");
        backLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backLabel.setForeground(new Color(150, 150, 150));
        backLabel.setBounds(15, 15, 60, 20);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new StudentDashboard(rollNo);
                f.dispose();
            }

            public void mouseEntered(MouseEvent e) {
                backLabel.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                backLabel.setForeground(new Color(150, 150, 150));
            }
        });
        headerPanel.add(backLabel);

        JLabel titleLabel = new JLabel("My Attendance", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 10, 650, 30);
        headerPanel.add(titleLabel);

        JLabel closeLabel = new JLabel("X");
        closeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeLabel.setForeground(new Color(150, 150, 150));
        closeLabel.setBounds(620, 10, 30, 30);
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
        headerPanel.add(closeLabel);

        // Filter Panel
        JPanel filterPanel = new JPanel();
        filterPanel.setOpaque(false);
        filterPanel.setLayout(new GridBagLayout());
        filterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JRadioButton rbMonthly = new JRadioButton("Monthly");
        JRadioButton rbDay = new JRadioButton("By Date");
        JRadioButton rbTillNow = new JRadioButton("Till now", true);

        Font rbFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color textColor = new Color(200, 210, 220);
        setupPremiumRadioButton(rbMonthly, rbFont, textColor);
        setupPremiumRadioButton(rbDay, rbFont, textColor);
        setupPremiumRadioButton(rbTillNow, rbFont, textColor);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbMonthly);
        bg.add(rbDay);
        bg.add(rbTillNow);

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(rbMonthly, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        filterPanel.add(rbDay, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        filterPanel.add(rbTillNow, gbc);

        // Inputs for filters
        String[] months = { "-Month-", "01 - Jan", "02 - Feb", "03 - Mar", "04 - Apr", "05 - May", "06 - Jun",
                "07 - Jul", "08 - Aug", "09 - Sep", "10 - Oct", "11 - Nov", "12 - Dec" };
        JComboBox<String> monthCombo = createPremiumComboBox(months);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = { "-Year-", String.valueOf(currentYear), String.valueOf(currentYear - 1),
                String.valueOf(currentYear - 2) };
        JComboBox<String> yearCombo = createPremiumComboBox(years);

        JTextField dateField = new JTextField("YYYY-MM-DD", 10);
        styleTextField(dateField);
        dateField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (dateField.getText().equals("YYYY-MM-DD")) {
                    dateField.setText("");
                    dateField.setForeground(Color.WHITE);
                }
            }

            public void focusLost(FocusEvent e) {
                if (dateField.getText().isEmpty()) {
                    dateField.setForeground(new Color(150, 150, 150));
                    dateField.setText("YYYY-MM-DD");
                }
            }
        });

        JButton showBtn = createPremiumButton("Show..", new Color(40, 167, 69), new Color(33, 136, 56));
        showBtn.setPreferredSize(new Dimension(80, 30));

        JPanel inputsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputsPanel.setOpaque(false);
        inputsPanel.add(new JLabel(" ") {
            {
                setForeground(textColor);
            }
        }); // spacer
        inputsPanel.add(monthCombo);
        inputsPanel.add(yearCombo);
        inputsPanel.add(dateField);
        inputsPanel.add(showBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        filterPanel.add(inputsPanel, gbc);

        // Initial State
        monthCombo.setEnabled(false);
        yearCombo.setEnabled(false);
        dateField.setEnabled(false);

        rbMonthly.addActionListener(e -> {
            monthCombo.setEnabled(true);
            yearCombo.setEnabled(true);
            dateField.setEnabled(false);
        });
        rbDay.addActionListener(e -> {
            monthCombo.setEnabled(false);
            yearCombo.setEnabled(false);
            dateField.setEnabled(true);
        });
        rbTillNow.addActionListener(e -> {
            monthCombo.setEnabled(false);
            yearCombo.setEnabled(false);
            dateField.setEnabled(false);
        });

        // Table Setup
        model = new DefaultTableModel(new String[] { "Class Name", "Date", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        setupPremiumTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(26, 31, 36));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        scrollPane.setOpaque(false);

        // Assemble Top
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(filterPanel, BorderLayout.SOUTH);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        lblTotal = new JLabel("Total Classes: 0", SwingConstants.CENTER);
        lblPresent = new JLabel("Present: 0", SwingConstants.CENTER);
        lblPercentage = new JLabel("Attendance: 0%", SwingConstants.CENTER);

        Font statsFont = new Font("Segoe UI", Font.BOLD, 18);
        lblTotal.setFont(statsFont);
        lblTotal.setForeground(Color.WHITE);
        lblPresent.setFont(statsFont);
        lblPresent.setForeground(new Color(40, 167, 69)); // Green
        lblPercentage.setFont(statsFont);
        lblPercentage.setForeground(Color.WHITE);

        statsPanel.add(lblTotal);
        statsPanel.add(lblPresent);
        statsPanel.add(lblPercentage);

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);

        // Action for Show
        showBtn.addActionListener(e -> {
            String filterType = "";
            Integer selectedMonth = null;
            Integer selectedYear = null;
            String selectedDate = null;

            if (rbMonthly.isSelected()) {
                filterType = "Monthly";
                if (monthCombo.getSelectedIndex() > 0 && yearCombo.getSelectedIndex() > 0) {
                    selectedMonth = monthCombo.getSelectedIndex();
                    selectedYear = Integer.parseInt((String) yearCombo.getSelectedItem());
                } else {
                    JOptionPane.showMessageDialog(f, "Please select both Month and Year.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else if (rbDay.isSelected()) {
                filterType = "By Date";
                selectedDate = dateField.getText();
                if (selectedDate.equals("YYYY-MM-DD") || selectedDate.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(f, "Please enter a valid date.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                filterType = "Till now";
            }

            loadData(filterType, selectedMonth, selectedYear, selectedDate);
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

        // Load initially
        loadData("Till now", null, null, null);
    }

    private void loadData(String filterType, Integer month, Integer year, String specificDate) {
        model.setRowCount(0); // clear
        int totalClasses = 0;
        int presentClasses = 0;
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(f, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder query = new StringBuilder(
                    "SELECT cs.session_id, cs.session_date, a.status " +
                            "FROM attendance a JOIN class_session cs ON a.session_id = cs.session_id " +
                            "WHERE a.regno=?");

            if (filterType.equals("Monthly")) {
                query.append(" AND MONTH(cs.session_date)=? AND YEAR(cs.session_date)=?");
            } else if (filterType.equals("Particular Day")) {
                query.append(" AND cs.session_date=?");
            }

            query.append(" ORDER BY cs.session_date DESC");

            PreparedStatement ps = con.prepareStatement(query.toString());
            ps.setString(1, studentRollNo);

            if (filterType.equals("Monthly")) {
                ps.setInt(2, month);
                ps.setInt(3, year);
            } else if (filterType.equals("By Date")) {
                ps.setDate(2, java.sql.Date.valueOf(specificDate));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString(3);
                model.addRow(new Object[] {
                        rs.getString(1),
                        rs.getDate(2),
                        status
                });
                totalClasses++;
                if ("Present".equalsIgnoreCase(status)) {
                    presentClasses++;
                }
            }

            lblTotal.setText("Total Classes: " + totalClasses);
            lblPresent.setText("Present: " + presentClasses);
            if (totalClasses > 0) {
                int percentage = (int) Math.round((presentClasses * 100.0) / totalClasses);
                lblPercentage.setText("Attendance: " + percentage + "%");
                if (percentage >= 75) {
                    lblPercentage.setForeground(new Color(40, 167, 69)); // Green
                } else {
                    lblPercentage.setForeground(new Color(220, 53, 69)); // Red
                }
            } else {
                lblPercentage.setText("Attendance: 0%");
                lblPercentage.setForeground(Color.WHITE);
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(f, "Invalid Date Format. Please use YYYY-MM-DD.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(f, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setupPremiumRadioButton(JRadioButton rb, Font font, Color color) {
        rb.setFont(font);
        rb.setForeground(color);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JComboBox<String> createPremiumComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(45, 55, 65));
        combo.setForeground(Color.WHITE);
        // Customizing UI of combo box can be complex without extensive L&F
        // modifications,
        // standard custom background helps.
        combo.setPreferredSize(new Dimension(100, 30));
        return combo;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(45, 55, 65));
        tf.setForeground(new Color(150, 150, 150));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 90)),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        tf.setPreferredSize(new Dimension(100, 30));
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(20, 25, 30));
        header.setForeground(new Color(200, 210, 220));
        header.setPreferredSize(new Dimension(100, 35));

        // Center text alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Specifically for Header Text Alignment
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    public static void showOverallPercentage(String studentRollNo, Component parent) {
        int totalClasses = 0;
        int presentClasses = 0;
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(parent, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "SELECT status FROM attendance a JOIN class_session cs ON a.session_id = cs.session_id WHERE a.regno=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, studentRollNo);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString(1);
                totalClasses++;
                if ("Present".equalsIgnoreCase(status)) {
                    presentClasses++;
                }
            }

            int percentage = 0;
            if (totalClasses > 0) {
                percentage = (int) Math.round((presentClasses * 100.0) / totalClasses);
            }

            JDialog d = new JDialog(parent != null ? (Frame) SwingUtilities.getWindowAncestor(parent) : null,
                    "Overall Attendance", true);
            d.setSize(300, 260);
            d.setUndecorated(true);
            if (parent != null)
                d.setLocationRelativeTo(parent);
            else
                d.setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    GradientPaint gp = new GradientPaint(0, 0, new Color(26, 31, 36), getWidth(), getHeight(),
                            new Color(42, 51, 62));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(60, 70, 80));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                }
            };
            mainPanel.setLayout(null);

            JLabel titleLabel = new JLabel("Attendance Summary", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setBounds(0, 20, 300, 30);
            mainPanel.add(titleLabel);

            JLabel totalLabel = new JLabel("Total Classes: " + totalClasses, SwingConstants.CENTER);
            totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            totalLabel.setForeground(new Color(200, 210, 220));
            totalLabel.setBounds(0, 70, 300, 25);
            mainPanel.add(totalLabel);

            JLabel presentLabel = new JLabel("Present: " + presentClasses, SwingConstants.CENTER);
            presentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            presentLabel.setForeground(new Color(40, 167, 69));
            presentLabel.setBounds(0, 100, 300, 25);
            mainPanel.add(presentLabel);

            JLabel percLabel = new JLabel("Attendance = " + percentage + "%", SwingConstants.CENTER);
            percLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            percLabel.setForeground(percentage >= 75 ? new Color(40, 167, 69) : new Color(220, 53, 69));
            percLabel.setBounds(0, 140, 300, 30);
            mainPanel.add(percLabel);

            JButton closeBtn = new JButton("Close") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isArmed() || getModel().isPressed()) {
                        g2.setColor(new Color(200, 35, 51));
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(220, 53, 69));
                    } else {
                        g2.setColor(new Color(220, 53, 69));
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setFocusPainted(false);
            closeBtn.setBorderPainted(false);
            closeBtn.setContentAreaFilled(false);
            closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeBtn.setBounds(100, 200, 100, 35);
            closeBtn.addActionListener(e -> d.dispose());
            mainPanel.add(closeBtn);

            d.add(mainPanel);
            d.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Database Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
