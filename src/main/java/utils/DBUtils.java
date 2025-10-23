package utils;

import java.sql.*;

/**
 * DBUtils - đọc thông tin kết nối từ System properties hoặc environment variables.
 * Fallback: dùng mặc định localhost, CarDealerDBI, sa, 12345.
 */
public class DBUtils {

    private static final String DEFAULT_DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=CarDealerDBI;encrypt=true;trustServerCertificate=true";
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASS = "12345";

    // ======================================================
    // GET CONNECTION
    // ======================================================
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String url = System.getProperty("db.url");
            String user = System.getProperty("db.username");
            String pass = System.getProperty("db.password");

            if (url == null || url.trim().isEmpty()) url = System.getenv("SPRING_DATASOURCE_URL");
            if (user == null || user.trim().isEmpty()) user = System.getenv("SPRING_DATASOURCE_USERNAME");
            if (pass == null || pass.trim().isEmpty()) pass = System.getenv("SPRING_DATASOURCE_PASSWORD");

            if (url == null || url.trim().isEmpty()) url = DEFAULT_DB_URL;
            if (user == null || user.trim().isEmpty()) user = DEFAULT_USER;
            if (pass == null || pass.trim().isEmpty()) pass = DEFAULT_PASS;

            conn = DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to DB: " + e.getMessage());
        }
        return conn;
    }

    // ======================================================
    // CREATE PREPARED STATEMENT
    // ======================================================
    public static PreparedStatement createPreparedStatement(String sql) throws SQLException {
        Connection conn = getConnection();
        return conn.prepareStatement(sql);
    }

    // ======================================================
    // CLOSE UTILITIES
    // ======================================================
    public static void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
    }

    public static void closeQuietly(Statement st) {
        if (st != null) {
            try { st.close(); } catch (SQLException ignored) {}
        }
    }

    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
