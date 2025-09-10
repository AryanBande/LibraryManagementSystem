package dao;

import dto.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TransactionDao extends DatabaseService {
    
    public TransactionDao() {
        super();
    }
    

    public boolean createTransaction(Transaction transaction) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "INSERT INTO transactions (u_id, b_id, status, issue_date) VALUES (?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, transaction.getUserId());
            statement.setInt(2, transaction.getBookId());
            statement.setString(3, transaction.getStatus());
            statement.setDate(4, transaction.getIssueDate());
            
            int rowsAffected = statement.executeUpdate();

            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating transaction: " + e.getMessage());

            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error ");
            return false;
        }  finally {
            closeResources(connection, statement);
        }
    }
    
    /**
     * Get transaction by ID
     * @param transactionId Transaction ID
     * @return Transaction object if found, null otherwise
     */
    public Transaction getTransactionById(int transactionId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        String query = "SELECT t.id, t.u_id, t.b_id, t.status, t.issue_date, t.return_date, " +
                      "u.name as user_name, b.title as book_title, b.author as book_author " +
                      "FROM transactions t " +
                      "JOIN users u ON t.u_id = u.id " +
                      "JOIN books b ON t.b_id = b.id " +
                      "WHERE t.id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, transactionId);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToTransaction(resultSet);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting transaction by ID: " + e.getMessage());
        }  catch (Exception e) {
            System.err.println("Unexpected error ");
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Get all transactions with user and book details
     * @return List of all transactions
     */
    public List<Transaction> getAllTransactions() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Transaction> transactions = new ArrayList<>();
        
        String query = "SELECT t.id, t.u_id, t.b_id, t.status, t.issue_date, t.return_date, " +
                      "u.name as user_name, b.title as book_title, b.author as book_author " +
                      "FROM transactions t " +
                      "JOIN users u ON t.u_id = u.id " +
                      "JOIN books b ON t.b_id = b.id " +
                      "ORDER BY t.issue_date DESC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        }  finally {
            closeResources(connection, statement, resultSet);
        }
        
        return transactions;
    }
    
    /**
     * Get transactions by user ID
     * @param userId User ID
     * @return List of transactions for the user
     */
    public List<Transaction> getTransactionsByUserId(int userId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Transaction> transactions = new ArrayList<>();
        
        String query = "SELECT t.id, t.u_id, t.b_id, t.status, t.issue_date, t.return_date, " +
                      "u.name as user_name, b.title as book_title, b.author as book_author " +
                      "FROM transactions t " +
                      "JOIN users u ON t.u_id = u.id " +
                      "JOIN books b ON t.b_id = b.id " +
                      "WHERE t.u_id = ? " +
                      "ORDER BY t.issue_date DESC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting transactions by user ID: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        }  finally {
            closeResources(connection, statement, resultSet);
        }
        
        return transactions;
    }
    
    /**
     * Get pending transactions (requests waiting for approval)
     * @return List of pending transactions
     */
    public List<Transaction> getPendingTransactions() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Transaction> transactions = new ArrayList<>();
        
        String query = "SELECT t.id, t.u_id, t.b_id, t.status, t.issue_date, t.return_date, " +
                      "u.name as user_name, b.title as book_title, b.author as book_author " +
                      "FROM transactions t " +
                      "JOIN users u ON t.u_id = u.id " +
                      "JOIN books b ON t.b_id = b.id " +
                      "WHERE t.status = 'PENDING' " +
                      "ORDER BY t.issue_date ASC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting pending transactions: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        }  finally {
            closeResources(connection, statement, resultSet);
        }
        
        return transactions;
    }
    

    public List<Transaction> getApprovedTransactions() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Transaction> transactions = new ArrayList<>();
        
        String query = "SELECT t.id, t.u_id, t.b_id, t.status, t.issue_date, t.return_date, " +
                      "u.name as user_name, b.title as book_title, b.author as book_author " +
                      "FROM transactions t " +
                      "JOIN users u ON t.u_id = u.id " +
                      "JOIN books b ON t.b_id = b.id " +
                      "WHERE t.status = 'APPROVED' AND t.return_date IS NULL " +
                      "ORDER BY t.issue_date ASC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting approved transactions: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        }  finally {
            closeResources(connection, statement, resultSet);
        }
        
        return transactions;
    }
    

    public boolean updateTransactionStatus(int transactionId, String newStatus) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "UPDATE transactions SET status = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
            statement.setString(1, newStatus);
            statement.setInt(2, transactionId);
            
            int rowsAffected = statement.executeUpdate();
            commitTransaction(connection);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating transaction status: " + e.getMessage());
            rollbackTransaction(connection);
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error ");
            return false;
        }  finally {
            closeResources(connection, statement);
        }
    }
    
    /**
     * Mark book as returned
     * @param transactionId Transaction ID
     * @param returnDate Return date
     * @return true if successful, false otherwise
     */
    public boolean returnBook(int transactionId, Date returnDate) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "UPDATE transactions SET return_date = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
            statement.setDate(1, returnDate);
            statement.setInt(2, transactionId);
            
            int rowsAffected = statement.executeUpdate();
            commitTransaction(connection);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            rollbackTransaction(connection);
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error ");
            return false;
        }  finally {
            closeResources(connection, statement);
        }
    }
    
    /**
     * Check if user has already requested a specific book (pending or approved)
     * @param userId User ID
     * @param bookId Book ID
     * @return true if request exists, false otherwise
     */
    public boolean hasActiveRequest(int userId, int bookId) {
        String query = "SELECT COUNT(*) FROM transactions WHERE u_id = ? AND b_id = ? AND status IN ('PENDING', 'APPROVED') AND return_date IS NULL";
        return executeCountQuery(query, userId, bookId) > 0;
    }
    
    /**
     * Get active transactions for a user (approved books not yet returned)
     * @param userId User ID
     * @return List of active transactions
     */
    public List<Transaction> getActiveTransactionsByUserId(int userId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Transaction> transactions = new ArrayList<>();
        
        String query = "SELECT t.id, t.u_id, t.b_id, t.status, t.issue_date, t.return_date, " +
                      "u.name as user_name, b.title as book_title, b.author as book_author " +
                      "FROM transactions t " +
                      "JOIN users u ON t.u_id = u.id " +
                      "JOIN books b ON t.b_id = b.id " +
                      "WHERE t.u_id = ? AND t.status = 'APPROVED' AND t.return_date IS NULL " +
                      "ORDER BY t.issue_date DESC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting active transactions by user ID: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        }  finally {
            closeResources(connection, statement, resultSet);
        }
        
        return transactions;
    }
    
    /**
     * Get transactions by status
     * @param status Transaction status
     * @return List of transactions with the specified status
     */
    public List<Transaction> getTransactionsByStatus(String status) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Transaction> transactions = new ArrayList<>();
        
        String query = "SELECT t.id, t.u_id, t.b_id, t.status, t.issue_date, t.return_date, " +
                      "u.name as user_name, b.title as book_title, b.author as book_author " +
                      "FROM transactions t " +
                      "JOIN users u ON t.u_id = u.id " +
                      "JOIN books b ON t.b_id = b.id " +
                      "WHERE t.status = ? " +
                      "ORDER BY t.issue_date DESC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, status);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting transactions by status: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        }  finally {
            closeResources(connection, statement, resultSet);
        }
        
        return transactions;
    }
    
    /**
     * Delete transaction by ID
     * @param transactionId Transaction ID
     * @return true if successful, false otherwise
     */
    public boolean deleteTransaction(int transactionId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "DELETE FROM transactions WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, transactionId);
            
            int rowsAffected = statement.executeUpdate();

            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());

            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error ");
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    

    private Transaction mapResultSetToTransaction(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setUserId(resultSet.getInt("u_id"));
        transaction.setBookId(resultSet.getInt("b_id"));
        transaction.setStatus(resultSet.getString("status"));
        transaction.setIssueDate(resultSet.getDate("issue_date"));
        transaction.setReturnDate(resultSet.getDate("return_date"));
        
        // Set additional display fields if available
        try {
            transaction.setUserName(resultSet.getString("user_name"));
            transaction.setBookTitle(resultSet.getString("book_title"));
            transaction.setBookAuthor(resultSet.getString("book_author"));
        } catch (Exception e) {
            System.err.println("Unexpected error ");
        }
        
        return transaction;
    }
}