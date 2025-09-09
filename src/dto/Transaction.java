package dto;

import java.sql.Date;

/**
 * Transaction Data Transfer Object
 * Represents a book transaction (issue/return) in the library management system
 */
public class Transaction {
    private int id;
    private int userId;
    private int bookId;
    private String status; // 'PENDING', 'APPROVED', 'DENIED'
    private Date issueDate;
    private Date returnDate;
    
    // Additional fields for display purposes
    private String userName;
    private String bookTitle;
    private String bookAuthor;
    
    // Default constructor
    public Transaction() {}
    
    // Constructor with all fields
    public Transaction(int id, int userId, int bookId, String status, Date issueDate, Date returnDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.status = status;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }
    
    // Constructor without id (for new transaction creation)
    public Transaction(int userId, int bookId, String status) {
        this.userId = userId;
        this.bookId = bookId;
        this.status = status;
        this.issueDate = new Date(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getBookId() {
        return bookId;
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
    
    public Date getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public String getBookAuthor() {
        return bookAuthor;
    }
    
    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    
    // Status check methods
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }
    
    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(status);
    }
    
    public boolean isDenied() {
        return "DENIED".equalsIgnoreCase(status);
    }
    
    // Status update methods
    public void approve() {
        this.status = "APPROVED";
    }
    
    public void deny() {
        this.status = "DENIED";
    }
    
    // Check if transaction is active (approved and not returned)
    public boolean isActive() {
        return isApproved() && returnDate == null;
    }
    
    @Override
    public String toString() {
        return String.format("Transaction{id=%d, userId=%d, bookId=%d, status='%s', issueDate=%s, returnDate=%s}", 
                           id, userId, bookId, status, issueDate, returnDate);
    }
    
    // Method to display transaction info in a formatted way for console output
    public String getDisplayInfo() {
        String returnInfo = returnDate != null ? returnDate.toString() : "Not Returned";
        String userInfo = userName != null ? userName : "User ID: " + userId;
        String bookInfo = bookTitle != null ? bookTitle : "Book ID: " + bookId;
        
        return String.format("ID: %-3d | User: %-20s | Book: %-25s | Status: %-8s | Issue: %s | Return: %s", 
                           id, userInfo, bookInfo, status, issueDate, returnInfo);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction transaction = (Transaction) obj;
        return id == transaction.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}