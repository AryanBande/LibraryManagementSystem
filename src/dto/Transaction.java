package dto;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Transaction {
    private int id;
    private int userId;
    private int bookId;
    private String status;
    private Date issueDate;
    private Date returnDate;

    private String userName;
    private String bookTitle;
    private String bookAuthor;

    private static final int RETURN_PERIOD_DAYS = 7;
    private static final double FINE_PER_DAY = 10.0;

    public Transaction() {}


    public Transaction(int userId, int bookId, String status) {
        this.userId = userId;
        this.bookId = bookId;
        this.status = status;
        this.issueDate = new Date(System.currentTimeMillis());
    }

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


    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(status);
    }

    public boolean isDenied() {
        return "DENIED".equalsIgnoreCase(status);
    }

    public void approve() {
        this.status = "APPROVED";
    }

    public void deny() {
        this.status = "DENIED";
    }

    public boolean isActive() {
        return isApproved() && returnDate == null;
    }


    public LocalDate getDueDate() {
        if (issueDate == null) return null;
        return issueDate.toLocalDate().plusDays(RETURN_PERIOD_DAYS);
    }


    public boolean isOverdue() {
        if (issueDate == null || !isActive()) return false;
        return LocalDate.now().isAfter(getDueDate());
    }


    public long getOverdueDays() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(getDueDate(), LocalDate.now());
    }


    public double calculateFine() {
        long overdueDays = getOverdueDays();
        return overdueDays > 0 ? overdueDays * FINE_PER_DAY : 0.0;
    }


    public String getFineStatus() {
        if (!isActive()) return "N/A";

        double fine = calculateFine();
        if (fine == 0) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), getDueDate());
            if (daysLeft >= 0) {
                return "No fine (" + daysLeft + " days left)";
            } else {
                return "No fine (due today)";
            }
        } else {
            return "₹" + String.format("%.2f", fine) + " (" + getOverdueDays() + " days overdue)";
        }
    }


    public long getDaysUntilDue() {
        if (issueDate == null || !isActive()) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), getDueDate());
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

    /**
     * Get detailed display info with fine information
     * @return Formatted string with fine details
     */
    public String getDetailedDisplayInfo() {
        String returnInfo = returnDate != null ? returnDate.toString() : "Not Returned";
        String userInfo = userName != null ? userName : "User ID: " + userId;
        String bookInfo = bookTitle != null ? bookTitle : "Book ID: " + bookId;
        String fineInfo = isActive() ? getFineStatus() : "N/A";

        return String.format("ID: %-3d | User: %-20s | Book: %-25s | Status: %-8s | Issue: %s | Return: %s | Fine: %s",
                id, userInfo, bookInfo, status, issueDate, returnInfo, fineInfo);
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
