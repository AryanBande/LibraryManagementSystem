package service;

import dao.BookDao;
import dto.Book;
import java.util.List;

/**
 * Book Service
 * Handles book management business logic
 */
public class BookService {
    private BookDao bookDao;
    
    public BookService() {
        this.bookDao = new BookDao();
    }
    
    /**
     * Create a new book with validation
     * @param title Book title
     * @param author Book author
     * @param category Book category
     * @param quantity Book quantity
     * @param floor Floor number
     * @param shelve Shelve identifier
     * @return true if book created successfully, false otherwise
     */
    public boolean createBook(String title, String author, String category, int quantity, int floor, String shelve) {
        try {
            // Validate input
            if (!validateBookInput(title, author, category, quantity, floor, shelve)) {
                return false;
            }
            
            // Check if book already exists
            if (bookDao.bookExists(title, author)) {
                System.out.println("Book already exists. Consider updating quantity instead.");
                return false;
            }
            
            // Create book object
            Book book = new Book(title.trim(), author.trim(), category.trim(), quantity, floor, shelve.trim().toUpperCase());
            
            // Save to database
            boolean success = bookDao.createBook(book);
            
            if (success) {
                System.out.println("Book added successfully: " + title + " by " + author);
                return true;
            } else {
                System.out.println("Failed to add book. Please try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error creating book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all books
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        try {
            return bookDao.getAllBooks();
        } catch (Exception e) {
            System.err.println("Error getting all books: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get available books (quantity > 0)
     * @return List of available books
     */
    public List<Book> getAvailableBooks() {
        try {
            return bookDao.getAvailableBooks();
        } catch (Exception e) {
            System.err.println("Error getting available books: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get book by ID
     * @param bookId Book ID
     * @return Book object if found, null otherwise
     */
    public Book getBookById(int bookId) {
        try {
            if (bookId <= 0) {
                System.out.println("Invalid book ID.");
                return null;
            }
            
            return bookDao.getBookById(bookId);
        } catch (Exception e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Search books by multiple criteria
     * @param searchTerm Search term
     * @return List of matching books
     */
    public List<Book> searchBooks(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                System.out.println("Search term cannot be empty.");
                return null;
            }
            
            return bookDao.searchBooks(searchTerm.trim());
        } catch (Exception e) {
            System.err.println("Error searching books: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Search books by title
     * @param title Title to search
     * @return List of matching books
     */
    public List<Book> searchBooksByTitle(String title) {
        try {
            if (title == null || title.trim().isEmpty()) {
                System.out.println("Title cannot be empty.");
                return null;
            }
            
            return bookDao.searchBooksByTitle(title.trim());
        } catch (Exception e) {
            System.err.println("Error searching books by title: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Search books by author
     * @param author Author to search
     * @return List of matching books
     */
    public List<Book> searchBooksByAuthor(String author) {
        try {
            if (author == null || author.trim().isEmpty()) {
                System.out.println("Author cannot be empty.");
                return null;
            }
            
            return bookDao.searchBooksByAuthor(author.trim());
        } catch (Exception e) {
            System.err.println("Error searching books by author: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Search books by category
     * @param category Category to search
     * @return List of matching books
     */
    public List<Book> searchBooksByCategory(String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                System.out.println("Category cannot be empty.");
                return null;
            }
            
            return bookDao.searchBooksByCategory(category.trim());
        } catch (Exception e) {
            System.err.println("Error searching books by category: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Update book information
     * @param bookId Book ID
     * @param title New title
     * @param author New author
     * @param category New category
     * @param quantity New quantity
     * @param floor New floor
     * @param shelve New shelve
     * @return true if updated successfully, false otherwise
     */
    public boolean updateBook(int bookId, String title, String author, String category, int quantity, int floor, String shelve) {
        try {
            // Get existing book
            Book existingBook = bookDao.getBookById(bookId);
            if (existingBook == null) {
                System.out.println("Book not found.");
                return false;
            }
            
            // Validate input
            if (!validateBookInput(title, author, category, quantity, floor, shelve)) {
                return false;
            }
            
            // Update book object
            existingBook.setTitle(title.trim());
            existingBook.setAuthor(author.trim());
            existingBook.setCategory(category.trim());
            existingBook.setQuantity(quantity);
            existingBook.setFloor(floor);
            existingBook.setShelve(shelve.trim().toUpperCase());
            
            // Save to database
            boolean success = bookDao.updateBook(existingBook);
            
            if (success) {
                System.out.println("Book updated successfully: " + title);
                return true;
            } else {
                System.out.println("Failed to update book. Please try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update book quantity
     * @param bookId Book ID
     * @param newQuantity New quantity
     * @return true if updated successfully, false otherwise
     */
    public boolean updateBookQuantity(int bookId, int newQuantity) {
        try {
            if (bookId <= 0) {
                System.out.println("Invalid book ID.");
                return false;
            }
            
            if (newQuantity < 0) {
                System.out.println("Quantity cannot be negative.");
                return false;
            }
            
            // Check if book exists
            Book book = bookDao.getBookById(bookId);
            if (book == null) {
                System.out.println("Book not found.");
                return false;
            }
            
            // Update quantity
            boolean success = bookDao.updateBookQuantity(bookId, newQuantity);
            
            if (success) {
                System.out.println("Book quantity updated successfully. New quantity: " + newQuantity);
                return true;
            } else {
                System.out.println("Failed to update book quantity. Please try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error updating book quantity: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete book by ID
     * @param bookId Book ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteBook(int bookId) {
        try {
            if (bookId <= 0) {
                System.out.println("Invalid book ID.");
                return false;
            }
            
            // Get book first to show confirmation
            Book book = bookDao.getBookById(bookId);
            if (book == null) {
                System.out.println("Book not found.");
                return false;
            }
            
            // Delete book
            boolean success = bookDao.deleteBook(bookId);
            
            if (success) {
                System.out.println("Book deleted successfully: " + book.getTitle());
                return true;
            } else {
                System.out.println("Failed to delete book. Please try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Display all books in a formatted table
     */
    public void displayAllBooks() {
        try {
            List<Book> books = getAllBooks();
            displayBooksList(books, "ALL BOOKS");
        } catch (Exception e) {
            System.err.println("Error displaying all books: " + e.getMessage());
        }
    }
    
    /**
     * Display available books in a formatted table
     */
    public void displayAvailableBooks() {
        try {
            List<Book> books = getAvailableBooks();
            displayBooksList(books, "AVAILABLE BOOKS");
        } catch (Exception e) {
            System.err.println("Error displaying available books: " + e.getMessage());
        }
    }
    
    /**
     * Display search results in a formatted table
     * @param books List of books to display
     * @param searchTerm Search term used
     */
    public void displaySearchResults(List<Book> books, String searchTerm) {
        String title = "SEARCH RESULTS FOR: \"" + searchTerm + "\"";
        displayBooksList(books, title);
    }
    
    /**
     * Display books list in a formatted table
     * @param books List of books to display
     * @param title Table title
     */
    public void displayBooksList(List<Book> books, String title) {
        try {
            if (books == null || books.isEmpty()) {
                System.out.println("No books found.");
                return;
            }
            
            System.out.println("\n" + "=".repeat(140));
            System.out.println(title);
            System.out.println("=".repeat(140));
            System.out.printf("%-4s | %-30s | %-20s | %-12s | %-8s | %-6s | %-8s%n", 
                "ID", "Title", "Author", "Category", "Quantity", "Floor", "Shelve");
            System.out.println("-".repeat(140));
            
            for (Book book : books) {
                String availability = book.getQuantity() > 0 ? 
                    String.valueOf(book.getQuantity()) : "N/A";
                
                System.out.printf("%-4d | %-30s | %-20s | %-12s | %-8s | %-6d | %-8s%n", 
                    book.getId(), 
                    truncateString(book.getTitle(), 30),
                    truncateString(book.getAuthor(), 20),
                    truncateString(book.getCategory(), 12),
                    availability,
                    book.getFloor(),
                    book.getShelve());
            }
            
            System.out.println("-".repeat(140));
            System.out.println("Total books: " + books.size());
            
        } catch (Exception e) {
            System.err.println("Error displaying books list: " + e.getMessage());
        }
    }
    
    /**
     * Get books by category
     * @param category Category name
     * @return List of books in the category
     */
    public List<Book> getBooksByCategory(String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                System.out.println("Category cannot be empty.");
                return null;
            }
            
            return bookDao.getBooksByCategory(category.trim());
        } catch (Exception e) {
            System.err.println("Error getting books by category: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if book is available for issuing
     * @param bookId Book ID
     * @return true if available, false otherwise
     */
    public boolean isBookAvailable(int bookId) {
        try {
            Book book = getBookById(bookId);
            return book != null && book.getQuantity() > 0;
        } catch (Exception e) {
            System.err.println("Error checking book availability: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate book input
     * @param title Book title
     * @param author Book author
     * @param category Book category
     * @param quantity Book quantity
     * @param floor Floor number
     * @param shelve Shelve identifier
     * @return true if valid, false otherwise
     */
    private boolean validateBookInput(String title, String author, String category, int quantity, int floor, String shelve) {
        // Validate title
        if (title == null || title.trim().isEmpty()) {
            System.out.println("Title cannot be empty.");
            return false;
        }
        
        if (title.trim().length() > 200) {
            System.out.println("Title cannot exceed 200 characters.");
            return false;
        }
        
        // Validate author
        if (author == null || author.trim().isEmpty()) {
            System.out.println("Author cannot be empty.");
            return false;
        }
        
        if (author.trim().length() > 150) {
            System.out.println("Author cannot exceed 150 characters.");
            return false;
        }
        
        // Validate category
        if (category == null || category.trim().isEmpty()) {
            System.out.println("Category cannot be empty.");
            return false;
        }
        
        if (category.trim().length() > 100) {
            System.out.println("Category cannot exceed 100 characters.");
            return false;
        }
        
        // Validate quantity
        if (quantity < 0) {
            System.out.println("Quantity cannot be negative.");
            return false;
        }
        
        // Validate floor
        if (floor <= 0) {
            System.out.println("Floor must be a positive number.");
            return false;
        }
        
        // Validate shelve
        if (shelve == null || shelve.trim().isEmpty()) {
            System.out.println("Shelve cannot be empty.");
            return false;
        }
        
        if (shelve.trim().length() > 50) {
            System.out.println("Shelve identifier cannot exceed 50 characters.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Helper method to truncate string for display
     * @param str String to truncate
     * @param maxLength Maximum length
     * @return Truncated string
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Check if book exists
     * @param bookId Book ID
     * @return true if book exists, false otherwise
     */
    public boolean bookExists(int bookId) {
        try {
            return bookDao.getBookById(bookId) != null;
        } catch (Exception e) {
            System.err.println("Error checking if book exists: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get total number of books
     * @return Total number of books
     */
    public int getTotalBooksCount() {
        try {
            List<Book> books = bookDao.getAllBooks();
            return books != null ? books.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting books count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get total number of available books
     * @return Total number of available books
     */
    public int getAvailableBooksCount() {
        try {
            List<Book> books = bookDao.getAvailableBooks();
            return books != null ? books.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting available books count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get total quantity of all books
     * @return Total quantity
     */
    public int getTotalBooksQuantity() {
        try {
            List<Book> books = bookDao.getAllBooks();
            if (books == null) return 0;
            
            return books.stream().mapToInt(Book::getQuantity).sum();
        } catch (Exception e) {
            System.err.println("Error getting total books quantity: " + e.getMessage());
            return 0;
        }
    }
}