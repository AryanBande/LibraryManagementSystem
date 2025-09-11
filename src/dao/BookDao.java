package dao;

import dto.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Book Data Access Object
 * Handles all book-related database operations
 */
public class BookDao extends DatabaseService {
    
    public BookDao() {
        super();
    }
    

    public boolean createBook(Book book) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "INSERT INTO books (title, author, category, quantity, floor, shelve) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setInt(4, book.getQuantity());
            statement.setInt(5, book.getFloor());
            statement.setString(6, book.getShelve());
            
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating book: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    

    public Book getBookById(int bookId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        String query = "SELECT id, title, author, category, quantity, floor, shelve FROM books WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, bookId);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToBook(resultSet);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    

    public List<Book> getAllBooks() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Book> books = new ArrayList<>();
        
        String query = "SELECT id, title, author, category, quantity, floor, shelve FROM books ORDER BY title";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return books;
    }

    public List<Book> searchBooksByTitle(String title) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Book> books = new ArrayList<>();
        
        String query = "SELECT id, title, author, category, quantity, floor, shelve FROM books WHERE UPPER(title) LIKE UPPER(?) ORDER BY title";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, "%" + title + "%");
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching books by title: " + e.getMessage());
        }catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
        }
        finally {
            closeResources(connection, statement, resultSet);
        }
        
        return books;
    }
    
    /**
     * Search books by author
     * @param author Author to search for (partial match)
     * @return List of matching books
     */
    public List<Book> searchBooksByAuthor(String author) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Book> books = new ArrayList<>();
        
        String query = "SELECT id, title, author, category, quantity, floor, shelve FROM books WHERE UPPER(author) LIKE UPPER(?) ORDER BY title";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, "%" + author + "%");
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching books by author: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return books;
    }
    

    public List<Book> searchBooksByCategory(String category) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Book> books = new ArrayList<>();
        
        String query = "SELECT id, title, author, category, quantity, floor, shelve FROM books WHERE UPPER(category) LIKE UPPER(?) ORDER BY title";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, "%" + category + "%");
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching books by category: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return books;
    }
    

    public List<Book> searchBooks(String searchTerm) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Book> books = new ArrayList<>();
        
        String query = "SELECT id, title, author, category, quantity, floor, shelve FROM books " +
                      "WHERE UPPER(title) LIKE UPPER(?) OR UPPER(author) LIKE UPPER(?) OR UPPER(category) LIKE UPPER(?) " +
                      "ORDER BY title";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            String searchPattern = "%" + searchTerm + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return books;
    }
    
    /**
     * Update book information
     * @param book Book object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateBook(Book book) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "UPDATE books SET title = ?, author = ?, category = ?, quantity = ?, floor = ?, shelve = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setInt(4, book.getQuantity());
            statement.setInt(5, book.getFloor());
            statement.setString(6, book.getShelve());
            statement.setInt(7, book.getId());
            
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    

    public boolean updateBookQuantity(int bookId, int newQuantity) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "UPDATE books SET quantity = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, newQuantity);
            statement.setInt(2, bookId);
            
            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating book quantity: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    

    public boolean deleteBook(int bookId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        String query = "DELETE FROM books WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, bookId);
            
            int rowsAffected = statement.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }


    public List<Book> getAvailableBooks() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Book> books = new ArrayList<>();
        
        String query = "SELECT id, title, author, category, quantity, floor, shelve FROM books WHERE quantity > 0 ORDER BY title";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting available books: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return books;
    }
    
    /**
     * Get books by category
     * @param category Category name
     * @return List of books in the category
     */
    public List<Book> getBooksByCategory(String category) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Book> books = new ArrayList<>();
        
        String query = "SELECT id, title, author, category, quantity, floor, shelve FROM books WHERE category = ? ORDER BY title";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, category);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting books by category: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error:" + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return books;
    }
    
    /**
     * Check if book exists by title and author
     * @param title Book title
     * @param author Book author
     * @return true if book exists, false otherwise
     */
    public boolean bookExists(String title, String author) {
        String query = "SELECT COUNT(*) FROM books WHERE UPPER(title) = UPPER(?) AND UPPER(author) = UPPER(?)";
        return executeCountQuery(query, title, author) > 0;
    }
    
    /**
     * Helper method to map ResultSet to Book object
     * @param resultSet ResultSet from query
     * @return Book object
     * @throws SQLException if error accessing result set
     */
    private Book mapResultSetToBook(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getInt("id"));
        book.setTitle(resultSet.getString("title"));
        book.setAuthor(resultSet.getString("author"));
        book.setCategory(resultSet.getString("category"));
        book.setQuantity(resultSet.getInt("quantity"));
        book.setFloor(resultSet.getInt("floor"));
        book.setShelve(resultSet.getString("shelve"));
        return book;
    }
}