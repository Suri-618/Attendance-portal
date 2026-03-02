import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MarkAttendance {

    public MarkAttendance(String facultyId) {

        JFrame f = new JFrame("Mark Attendance");
        f.setSize(700, 450);
        f.setLayout(null);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color bg = new Color(245, 247, 250);
        Color primary = new Color(33, 150, 243);
        f.getContentPane().setBackground(bg);

        JLabel title = new JLabel("MARK ATTENDANCE");
        title.setBounds(250, 15, 300, 30);
        title.setFont(new Font("Times New Roman", Font.BOLD, 20));
        title.setForeground(primary);
        f.add(title);

        JLabel l1 = new JLabel("Session ID:");
        l1.setBounds(40, 70, 100, 30);
        f.add(l1);

        JTextField tfSession = new JTextField();
        tfSession.setBounds(130, 70, 100, 30);
        f.add(tfSession);

        JButton loadBtn = new JButton("Load Students");
        loadBtn.setBounds(260, 70, 150, 30);
        f.add(loadBtn);

        String[] cols = {"Roll No", "Name", "Present"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            // @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 2 ? Boolean.class : String.class;
            }

            // @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(40, 120, 600, 220);
        f.add(sp);

        JButton saveBtn = new JButton("Save Attendance");
        saveBtn.setBounds(260, 360, 180, 40);
        f.add(saveBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(10, 10, 70, 25);
        f.add(backBtn);

        loadBtn.addActionListener(e -> {
            model.setRowCount(0);

            String sessionId = tfSession.getText();
            if (sessionId.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Enter Session ID");
                return;
            }

            try {
                Connection con = DBConnection.getConnection();
                String sql = "SELECT rollno, name FROM student";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("rollno"),
                            rs.getString("name"),
                            false   
                    });
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(f, "Error loading students");
            }
        });

        // SAVE ATTENDANCE
        saveBtn.addActionListener(e -> {
            String sessionId = tfSession.getText();

            if (sessionId.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Enter Session ID");
                return;
            }

            try {
               Connection con = DBConnection.getConnection();
               String sql = """
               INSERT INTO attendance (session_id, regno, status)
               VALUES (?, ?, ?)
               """;


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
                JOptionPane.showMessageDialog(f, "Attendance Saved Successfully");

                new FacultyDashboard(facultyId);
                f.dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(f, "Error saving attendance");
            }
        });

        backBtn.addActionListener(e -> {
            new FacultyDashboard(facultyId);
            f.dispose();
        });

        f.setVisible(true);
    }
}
