import java.awt.Component;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExporter {

    public static void exportAttendanceToExcel(String facultyId, Component parent) {
        String excelFilePath = "attendance_report.xlsx";

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Attendance Report");

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = { "Session ID", "Date", "Student Roll No", "Student Name", "Status" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Fetch Data from database
            Connection con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(parent, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "SELECT a.session_id, cs.session_date, a.regno, s.name, a.status " +
                    "FROM attendance a " +
                    "JOIN class_session cs ON a.session_id = cs.session_id " +
                    "JOIN student s ON a.regno = s.rollno " +
                    "WHERE cs.faculty_id = ? " +
                    "ORDER BY cs.session_date DESC, a.session_id, s.name";

            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, facultyId);

            ResultSet resultSet = statement.executeQuery();

            int rowCount = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowCount++);

                row.createCell(0).setCellValue(resultSet.getString("session_id"));

                java.sql.Date sqlDate = resultSet.getDate("session_date");
                if (sqlDate != null) {
                    row.createCell(1).setCellValue(sqlDate.toString());
                } else {
                    row.createCell(1).setCellValue("N/A");
                }

                row.createCell(2).setCellValue(resultSet.getString("regno"));
                row.createCell(3).setCellValue(resultSet.getString("name"));
                row.createCell(4).setCellValue(resultSet.getString("status"));
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
            }

            JOptionPane.showMessageDialog(parent, "Attendance report exported successfully to: " + excelFilePath,
                    "Export Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Failed to export data: " + e.getMessage(), "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
