package dao;

import dto.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDao extends DatabaseService {
    
    public UserDao() {
        super();
    }

    public User authenticateUser(String email, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        String query = "SELECT id, name, email, password, user_type FROM users WHERE email = ? AND password = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setUserType(resultSet.getString("user_type"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }catch (Exception e) {
            System.err.println("Unexpected error during user authentication: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    

    public boolean createUser(User user) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "INSERT INTO users (name, email, password, user_type) VALUES (?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getUserType());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    

    public User getUserById(int userId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        String query = "SELECT id, name, email, password, user_type FROM users WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setUserType(resultSet.getString("user_type"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }  catch (Exception e) {
            System.err.println("Unexpected error ");
        }finally {
            closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    

    public User getUserByEmail(String email) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        String query = "SELECT id, name, email, password, user_type FROM users WHERE email = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setUserType(resultSet.getString("user_type"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    

    public List<User> getAllUsers() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<>();
        
        String query = "SELECT id, name, email, password, user_type FROM users ORDER BY name";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setUserType(resultSet.getString("user_type"));
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return users;
    }
    

    public boolean updateUser(User user) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "UPDATE users SET name = ?, email = ?, password = ?, user_type = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getUserType());
            statement.setInt(5, user.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error ");
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    

    public boolean deleteUser(int userId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "DELETE FROM users WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            
            int rowsAffected = statement.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error ");
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        return executeCountQuery(query, email) > 0;
    }
    

    public List<User> getUsersByType(String userType) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<>();
        
        String query = "SELECT id, name, email, password, user_type FROM users WHERE user_type = ? ORDER BY name";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, userType);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setUserType(resultSet.getString("user_type"));
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting users by type: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return users;
    }
}