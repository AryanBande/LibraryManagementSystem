package dao;

import utils.DatabaseConnection;
import java.sql.*;


public class DatabaseService {
    protected DatabaseConnection dbConnection;
    
    public DatabaseService() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    

    protected Connection getConnection() throws SQLException {
        return dbConnection.getConnection();
    }
    

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
    

    protected void closeResources(Connection connection, Statement statement) {
        closeResources(connection, statement, null);
    }
    

    protected void commitTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.commit();
            } catch (SQLException e) {
                System.err.println("Error committing transaction: " + e.getMessage());
            }
        }
    }
    

    protected int executeCountQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
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
}