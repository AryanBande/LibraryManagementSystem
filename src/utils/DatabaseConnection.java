package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for managing Oracle database connections
 * Provides centralized database connection management
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String DB_USERNAME = "system"; // Change as per your setup
    private static final String DB_PASSWORD = "happy"; // Change as per your setup
    
    private DatabaseConnection() {
        // Private constructor to implement singleton pattern
        try {
            // Register Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Oracle JDBC Driver not found", e);
        }catch (Exception e) {
            System.err.println("Unexpected error");
        }
    }
    
    /**
     * Get singleton instance of DatabaseConnection
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
//            connection.setAutoCommit(false); // Enable transaction management
            return connection;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;
        }
        catch (Exception e) {
            System.err.println("Unexpected error");
            throw e;
        }
    }
    
    /**
     * Close database connection safely
     * @param connection Connection to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
//    /**
//     * Test database connection
//     * @return true if connection successful, false otherwise
//     */
//    public boolean testConnection() {
//        try (Connection connection = getConnection()) {
//            return connection != null && !connection.isClosed();
//        } catch (SQLException e) {
//            System.err.println("Connection test failed: " + e.getMessage());
//            return false;
//        }
//    }
}