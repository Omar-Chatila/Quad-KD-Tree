package benchmarks;

import java.sql.*;

public class JDBCAccessTest {
    static final String DB_URL = "jdbc:mysql://localhost/world";
    static final String USER = "root";
    static final String PASS = "Sgtb3942!";
    static final String QUERY = "SELECT id, name, population FROM city";

    public static void main(String[] args) {
        // Open a connection

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(QUERY)) {
            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                System.out.print("ID: " + rs.getInt("id"));
                System.out.print(", Name: " + rs.getString("name"));
                System.out.print(", population: " + rs.getInt("population"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}