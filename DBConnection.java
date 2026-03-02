import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static Connection con;

    public static Connection getConnection() throws java.sql.SQLException {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/studentdb",
                        "root",
                        "root");
            }
        } catch (ClassNotFoundException e) {
            throw new java.sql.SQLException("MySQL JDBC Driver not found in classpath.", e);
        }
        return con;
    }

    public static void main(String[] args) {
        try {
            System.out.println("Testing Database Connection...");
            Connection testCon = getConnection();
            if (testCon != null) {
                System.out.println("Connection Successful!");
            }
        } catch (Exception e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        }
    }
}
