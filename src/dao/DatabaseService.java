package dao;

import utils.DatabaseConnection;
import java.sql.*;

/**
 * Base Database Service class
 * Provides common database operations and utilities
 */
public class DatabaseService {
    protected DatabaseConnection dbConnection;
    
    public DatabaseService() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    protected Connection getConnection() throws SQLException {
        return dbConnection.getConnection();
    }
    
    /**
     * Close database resources safely
     * @param connection Connection to close
     * @param statement Statement to close
     * @param resultSet ResultSet to close
     */
    protected void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
        
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Close database resources safely (without ResultSet)
     * @param connection Connection to close
     * @param statement Statement to close
     */
    protected void closeResources(Connection connection, Statement statement) {
        closeResources(connection, statement, null);
    }
    
    /**
     * Commit transaction
     * @param connection Connection to commit
     */
    protected void commitTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.commit();
            } catch (SQLException e) {
                System.err.println("Error committing transaction: " + e.getMessage());
            }
        }
    }
    
    /**
     * Rollback transaction
     * @param connection Connection to rollback
     */
    protected void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                System.err.println("Error rolling back transaction: " + e.getMessage());
            }
        }
    }
    
    /**
     * Execute a query that returns a single integer result (like count queries)
     * @param query SQL query
     * @param parameters Query parameters
     * @return Integer result or -1 if error
     */
    protected int executeCountQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("Error executing count query: " + e.getMessage());
            return -1;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Check if database connection is working
     * @return true if connection is successful, false otherwise
     */
//    public boolean testConnection() {
//        return dbConnection.testConnection();
//    }
    
    /**
     * Initialize database tables (if needed)
     * This method can be used to ensure tables exist
     * @return true if successful, false otherwise
     */
    public boolean initializeDatabase() {
        Connection connection = null;
        Statement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.createStatement();
            
            // Test if tables exist by running a simple query
            String testQuery = "SELECT COUNT(*) FROM users WHERE ROWNUM = 1";
            statement.executeQuery(testQuery);
            
            commitTransaction(connection);
            System.out.println("Database connection verified successfully.");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database initialization check failed: " + e.getMessage());
            rollbackTransaction(connection);
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
}