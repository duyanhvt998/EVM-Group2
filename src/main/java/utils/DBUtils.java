package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DBUtils - reads connection info from System properties (which Spring Boot sets from application.properties)
 * Fallback: uses embedded defaults (localhost, CarDealerDBI, sa, 12345) for convenience.
 *
 * To override: set -Ddb.url=... -Ddb.username=... -Ddb.password=...
 * Spring Boot's application.properties already sets spring.datasource.* so when running via Spring Boot, set JVM props in
 * src/main/resources/application.properties by adding:
 *   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=CarDealerDBI;trustServerCertificate=true
 *   spring.datasource.username=sa
 *   spring.datasource.password=12345
 *
 * This class will try to use System.getProperty("db.url"), if not present will fall back to spring.datasource.url env var,
 * then to defaults.
 */
public class DBUtils {

    private static final String DEFAULT_DB_URL = "jdbc:sqlserver://172.18.195.60:1433;databaseName=CarDealerDBI;encrypt=true;trustServerCertificate=true";
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASS = "StrongPwd@123";
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = System.getProperty("db.url");
            String user = System.getProperty("db.username");
            String pass = System.getProperty("db.password");
            if (url == null || url.trim().isEmpty()) {
                url = System.getenv("SPRING_DATASOURCE_URL");
            }
            if (user == null || user.trim().isEmpty()) {
                user = System.getenv("SPRING_DATASOURCE_USERNAME");
            }
            if (pass == null || pass.trim().isEmpty()) {
                pass = System.getenv("SPRING_DATASOURCE_PASSWORD");
            }

            // 3) Last fallback to defaults
            if (url == null || url.trim().isEmpty()) url = DEFAULT_DB_URL;
            if (user == null || user.trim().isEmpty()) user = DEFAULT_USER;
            if (pass == null || pass.trim().isEmpty()) pass = DEFAULT_PASS;
            conn = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Fail connecting to DB: " + e.getMessage());
        }
        return conn;
    }
    public static PreparedStatement createPreparedStatement(String sql) throws SQLException {
        Connection conn = getConnection();
        return conn.prepareStatement(sql);
    }
}
