package service;

import dao.TransactionDao;
import dao.BookDao;
import dto.Transaction;
import dto.Book;
import java.sql.Date;
import java.util.List;

/**
 * Transaction Service
 * Handles transaction management business logic
 */
public class TransactionService {
    private TransactionDao transactionDao;
    private BookDao bookDao;

    public TransactionService() {
        this.transactionDao = new TransactionDao();
        this.bookDao = new BookDao();
    }

    /**
     * Request book issue (create pending transaction)
     * @param userId User ID
     * @param bookId Book ID
     * @return true if request created successfully, false otherwise
     */
    public boolean requestBookIssue(int userId, int bookId) {
        try {
            // Validate inputs
            if (userId <= 0 || bookId <= 0) {
                System.out.println("Invalid user ID or book ID.");
                return false;
            }

            // Check if book exists and is available
            Book book = bookDao.getBookById(bookId);
            if (book == null) {
                System.out.println("Book not found.");
                return false;
            }

            if (book.getQuantity() <= 0) {
                System.out.println("Book is currently not available.");
                return false;
            }

            // Check if user already has an active request for this book
            if (transactionDao.hasActiveRequest(userId, bookId)) {
                System.out.println("You already have an active request or issued copy of this book.");
                return false;
            }

            // Create transaction
            Transaction transaction = new Transaction(userId, bookId, "PENDING");
            boolean success = transactionDao.createTransaction(transaction);

            if (success) {
                System.out.println("Book request submitted successfully.");
                System.out.println("Your request for \"" + book.getTitle() + "\" is pending approval.");
                return true;
            } else {
                System.out.println("Failed to submit book request. Please try again.");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error requesting book issue: " + e.getMessage());
            return false;
        }
    }

    /**
     * Approve book request
     * @param transactionId Transaction ID
     * @return true if approved successfully, false otherwise
     */
    public boolean approveBookRequest(int transactionId) {
        try {
            // Get transaction
            Transaction transaction = transactionDao.getTransactionById(transactionId);
            if (transaction == null) {
                System.out.println("Transaction not found.");
                return false;
            }

            if (!transaction.isPending()) {
                System.out.println("Transaction is not pending approval.");
                return false;
            }

            // Check if book is still available
            Book book = bookDao.getBookById(transaction.getBookId());
            if (book == null || book.getQuantity() <= 0) {
                System.out.println("Book is no longer available.");
                return false;
            }

            // Update transaction status
            boolean statusUpdated = transactionDao.updateTransactionStatus(transactionId, "APPROVED");
            if (!statusUpdated) {
                System.out.println("Failed to approve request. Please try again.");
                return false;
            }

            // Decrease book quantity
            boolean quantityUpdated = bookDao.updateBookQuantity(book.getId(), book.getQuantity() - 1);
            if (!quantityUpdated) {
                // Rollback transaction status
                transactionDao.updateTransactionStatus(transactionId, "PENDING");
                System.out.println("Failed to update book quantity. Please try again.");
                return false;
            }

            System.out.println("Book request approved successfully.");
            return true;

        } catch (Exception e) {
            System.err.println("Error approving book request: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deny book request
     * @param transactionId Transaction ID
     * @return true if denied successfully, false otherwise
     */
    public boolean denyBookRequest(int transactionId) {
        try {
            // Get transaction
            Transaction transaction = transactionDao.getTransactionById(transactionId);
            if (transaction == null) {
                System.out.println("Transaction not found.");
                return false;
            }

            if (!transaction.isPending()) {
                System.out.println("Transaction is not pending approval.");
                return false;
            }

            // Update transaction status
            boolean success = transactionDao.updateTransactionStatus(transactionId, "DENIED");

            if (success) {
                System.out.println("Book request denied.");
                return true;
            } else {
                System.out.println("Failed to deny request. Please try again.");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error denying book request: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return book (for students - existing functionality)
     * @param transactionId Transaction ID
     * @return true if returned successfully, false otherwise
     */
    public boolean returnBook(int transactionId) {
        try {
            // Get transaction
            Transaction transaction = transactionDao.getTransactionById(transactionId);
            if (transaction == null) {
                System.out.println("Transaction not found.");
                return false;
            }

            if (!transaction.isApproved() || transaction.getReturnDate() != null) {
                System.out.println("Book is not currently issued or already returned.");
                return false;
            }

            // Mark book as returned
            Date returnDate = new Date(System.currentTimeMillis());
            boolean statusUpdated = transactionDao.returnBook(transactionId, returnDate);
            if (!statusUpdated) {
                System.out.println("Failed to return book. Please try again.");
                return false;
            }

            // Increase book quantity
            Book book = bookDao.getBookById(transaction.getBookId());
            if (book != null) {
                bookDao.updateBookQuantity(book.getId(), book.getQuantity() + 1);
            }

            System.out.println("Book returned successfully.");
            return true;

        } catch (Exception e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Admin return book with fine collection
     * @param transactionId Transaction ID
     * @param collectFine Whether fine was collected
     * @return true if returned successfully, false otherwise
     */
    public boolean adminReturnBook(int transactionId, boolean collectFine) {
        try {
            // Get transaction
            Transaction transaction = transactionDao.getTransactionById(transactionId);
            if (transaction == null) {
                System.out.println("Transaction not found.");
                return false;
            }

            if (!transaction.isApproved() || transaction.getReturnDate() != null) {
                System.out.println("Book is not currently issued or already returned.");
                return false;
            }

            // Calculate fine
            double fine = transaction.calculateFine();

            // Display fine information
            if (fine > 0) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println("FINE CALCULATION");
                System.out.println("=".repeat(50));
                System.out.println("Book: " + transaction.getBookTitle());
                System.out.println("Student: " + transaction.getUserName());
                System.out.println("Issue Date: " + transaction.getIssueDate());
                System.out.println("Due Date: " + transaction.getDueDate());
                System.out.println("Overdue Days: " + transaction.getOverdueDays());
                System.out.println("Fine Amount: ₹" + String.format("%.2f", fine));
                System.out.println("=".repeat(50));

                if (collectFine) {
                    System.out.println("✓ Fine collected: ₹" + String.format("%.2f", fine));
                } else {
                    System.out.println("⚠ Fine NOT collected: ₹" + String.format("%.2f", fine));
                }
            } else {
                System.out.println("✓ No fine applicable - book returned on time.");
            }

            // Mark book as returned
            Date returnDate = new Date(System.currentTimeMillis());
            boolean statusUpdated = transactionDao.returnBook(transactionId, returnDate);
            if (!statusUpdated) {
                System.out.println("Failed to return book. Please try again.");
                return false;
            }

            // Increase book quantity
            Book book = bookDao.getBookById(transaction.getBookId());
            if (book != null) {
                bookDao.updateBookQuantity(book.getId(), book.getQuantity() + 1);
            }

            System.out.println("\n✓ Book returned successfully by admin.");
            if (fine > 0 && collectFine) {
                System.out.println("✓ Fine of ₹" + String.format("%.2f", fine) + " collected.");
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all transactions
     * @return List of all transactions
     */
    public List<Transaction> getAllTransactions() {
        try {
            return transactionDao.getAllTransactions();
        } catch (Exception e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get pending transactions
     * @return List of pending transactions
     */
    public List<Transaction> getPendingTransactions() {
        try {
            return transactionDao.getPendingTransactions();
        } catch (Exception e) {
            System.err.println("Error getting pending transactions: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get approved transactions (currently issued books)
     * @return List of approved transactions
     */
    public List<Transaction> getApprovedTransactions() {
        try {
            return transactionDao.getApprovedTransactions();
        } catch (Exception e) {
            System.err.println("Error getting approved transactions: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get transactions by user ID
     * @param userId User ID
     * @return List of user's transactions
     */
    public List<Transaction> getTransactionsByUserId(int userId) {
        try {
            if (userId <= 0) {
                System.out.println("Invalid user ID.");
                return null;
            }

            return transactionDao.getTransactionsByUserId(userId);
        } catch (Exception e) {
            System.err.println("Error getting transactions by user ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get active transactions for a user (issued books not yet returned)
     * @param userId User ID
     * @return List of active transactions
     */
    public List<Transaction> getActiveTransactionsByUserId(int userId) {
        try {
            if (userId <= 0) {
                System.out.println("Invalid user ID.");
                return null;
            }

            return transactionDao.getActiveTransactionsByUserId(userId);
        } catch (Exception e) {
            System.err.println("Error getting active transactions by user ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Display all transactions in a formatted table
     */
    public void displayAllTransactions() {
        try {
            List<Transaction> transactions = getAllTransactions();
            displayTransactionsList(transactions, "ALL TRANSACTIONS");
        } catch (Exception e) {
            System.err.println("Error displaying all transactions: " + e.getMessage());
        }
    }

    /**
     * Display pending transactions in a formatted table
     */
    public void displayPendingTransactions() {
        try {
            List<Transaction> transactions = getPendingTransactions();
            displayTransactionsList(transactions, "PENDING BOOK REQUESTS");
        } catch (Exception e) {
            System.err.println("Error displaying pending transactions: " + e.getMessage());
        }
    }

    /**
     * Display approved transactions in a formatted table
     */
    public void displayApprovedTransactions() {
        try {
            List<Transaction> transactions = getApprovedTransactions();
            displayTransactionsList(transactions, "ISSUED BOOKS (NOT YET RETURNED)");
        } catch (Exception e) {
            System.err.println("Error displaying approved transactions: " + e.getMessage());
        }
    }

    /**
     * Display approved transactions with fine information for admin
     */
    public void displayIssuedBooksWithFines() {
        try {
            List<Transaction> transactions = getApprovedTransactions();
            displayTransactionsListWithFines(transactions, "ISSUED BOOKS - RETURN MANAGEMENT");
        } catch (Exception e) {
            System.err.println("Error displaying issued books with fines: " + e.getMessage());
        }
    }

    /**
     * Display user's transactions in a formatted table
     * @param userId User ID
     * @param userName User name for display
     */
    public void displayUserTransactions(int userId, String userName) {
        try {
            List<Transaction> transactions = getTransactionsByUserId(userId);
            String title = "TRANSACTIONS FOR: " + userName;
            displayTransactionsList(transactions, title);
        } catch (Exception e) {
            System.err.println("Error displaying user transactions: " + e.getMessage());
        }
    }

    /**
     * Display user's active transactions in a formatted table
     * @param userId User ID
     * @param userName User name for display
     */
    public void displayUserActiveTransactions(int userId, String userName) {
        try {
            List<Transaction> transactions = getActiveTransactionsByUserId(userId);
            String title = "BOOKS ISSUED TO: " + userName;
            displayTransactionsList(transactions, title);
        } catch (Exception e) {
            System.err.println("Error displaying user active transactions: " + e.getMessage());
        }
    }

    /**
     * Display transactions list in a formatted table
     * @param transactions List of transactions to display
     * @param title Table title
     */
    public void displayTransactionsList(List<Transaction> transactions, String title) {
        try {
            if (transactions == null || transactions.isEmpty()) {
                System.out.println("No transactions found.");
                return;
            }

            System.out.println("\n" + "=".repeat(150));
            System.out.println(title);
            System.out.println("=".repeat(150));
            System.out.printf("%-4s | %-20s | %-30s | %-15s | %-10s | %-12s | %-12s%n",
                    "ID", "User", "Book", "Author", "Status", "Issue Date", "Return Date");
            System.out.println("-".repeat(150));

            for (Transaction transaction : transactions) {
                String userName = transaction.getUserName() != null ?
                        transaction.getUserName() : "ID: " + transaction.getUserId();
                String bookTitle = transaction.getBookTitle() != null ?
                        transaction.getBookTitle() : "ID: " + transaction.getBookId();
                String bookAuthor = transaction.getBookAuthor() != null ?
                        transaction.getBookAuthor() : "";
                String returnDate = transaction.getReturnDate() != null ?
                        transaction.getReturnDate().toString() : "Not Returned";

                System.out.printf("%-4d | %-20s | %-30s | %-15s | %-10s | %-12s | %-12s%n",
                        transaction.getId(),
                        truncateString(userName, 20),
                        truncateString(bookTitle, 30),
                        truncateString(bookAuthor, 15),
                        transaction.getStatus(),
                        transaction.getIssueDate(),
                        returnDate);
            }

            System.out.println("-".repeat(150));
            System.out.println("Total transactions: " + transactions.size());

        } catch (Exception e) {
            System.err.println("Error displaying transactions list: " + e.getMessage());
        }
    }

    /**
     * Display transactions list with fine information
     * @param transactions List of transactions to display
     * @param title Table title
     */
    public void displayTransactionsListWithFines(List<Transaction> transactions, String title) {
        try {
            if (transactions == null || transactions.isEmpty()) {
                System.out.println("No issued books found.");
                return;
            }

            System.out.println("\n" + "=".repeat(170));
            System.out.println(title);
            System.out.println("=".repeat(170));
            System.out.printf("%-4s | %-18s | %-25s | %-12s | %-12s | %-10s | %-30s%n",
                    "ID", "User", "Book", "Issue Date", "Due Date", "Status", "Fine Status");
            System.out.println("-".repeat(170));

            double totalFines = 0.0;
            int overdueCount = 0;

            for (Transaction transaction : transactions) {
                String userName = transaction.getUserName() != null ?
                        transaction.getUserName() : "ID: " + transaction.getUserId();
                String bookTitle = transaction.getBookTitle() != null ?
                        transaction.getBookTitle() : "ID: " + transaction.getBookId();

                double fine = transaction.calculateFine();
                if (fine > 0) {
                    totalFines += fine;
                    overdueCount++;
                }

                System.out.printf("%-4d | %-18s | %-25s | %-12s | %-10s | %-10s | %-30s%n",
                        transaction.getId(),
                        truncateString(userName, 18),
                        truncateString(bookTitle, 25),
                        transaction.getIssueDate(),
                        transaction.getDueDate(),
                        transaction.isOverdue() ? "OVERDUE" : "ON TIME",
                        transaction.getFineStatus());
            }

            System.out.println("-".repeat(170));
            System.out.println("Total issued books: " + transactions.size());
            System.out.println("Overdue books: " + overdueCount);
            System.out.println("Total pending fines: ₹" + String.format("%.2f", totalFines));

        } catch (Exception e) {
            System.err.println("Error displaying transactions list with fines: " + e.getMessage());
        }
    }

    /**
     * Get transaction by ID
     * @param transactionId Transaction ID
     * @return Transaction object if found, null otherwise
     */
    public Transaction getTransactionById(int transactionId) {
        try {
            if (transactionId <= 0) {
                System.out.println("Invalid transaction ID.");
                return null;
            }

            return transactionDao.getTransactionById(transactionId);
        } catch (Exception e) {
            System.err.println("Error getting transaction by ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if user has already requested a specific book
     * @param userId User ID
     * @param bookId Book ID
     * @return true if user has active request, false otherwise
     */
    public boolean hasActiveRequest(int userId, int bookId) {
        try {
            return transactionDao.hasActiveRequest(userId, bookId);
        } catch (Exception e) {
            System.err.println("Error checking active request: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get transactions by status
     * @param status Transaction status
     * @return List of transactions with the specified status
     */
    public List<Transaction> getTransactionsByStatus(String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                System.out.println("Status cannot be empty.");
                return null;
            }

            return transactionDao.getTransactionsByStatus(status.trim().toUpperCase());
        } catch (Exception e) {
            System.err.println("Error getting transactions by status: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get total number of transactions
     * @return Total number of transactions
     */
    public int getTotalTransactionsCount() {
        try {
            List<Transaction> transactions = transactionDao.getAllTransactions();
            return transactions != null ? transactions.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting transactions count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get number of pending requests
     * @return Number of pending requests
     */
    public int getPendingRequestsCount() {
        try {
            List<Transaction> transactions = transactionDao.getPendingTransactions();
            return transactions != null ? transactions.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting pending requests count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get number of currently issued books
     * @return Number of issued books
     */
    public int getIssuedBooksCount() {
        try {
            List<Transaction> transactions = transactionDao.getApprovedTransactions();
            return transactions != null ? transactions.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting issued books count: " + e.getMessage());
            return 0;
        }
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
     * Delete transaction by ID
     * @param transactionId Transaction ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteTransaction(int transactionId) {
        try {
            if (transactionId <= 0) {
                System.out.println("Invalid transaction ID.");
                return false;
            }

            // Get transaction first
            Transaction transaction = transactionDao.getTransactionById(transactionId);
            if (transaction == null) {
                System.out.println("Transaction not found.");
                return false;
            }

            // If it's an approved transaction that hasn't been returned, restore book quantity
            if (transaction.isApproved() && transaction.getReturnDate() == null) {
                Book book = bookDao.getBookById(transaction.getBookId());
                if (book != null) {
                    bookDao.updateBookQuantity(book.getId(), book.getQuantity() + 1);
                }
            }

            // Delete transaction
            boolean success = transactionDao.deleteTransaction(transactionId);

            if (success) {
                System.out.println("Transaction deleted successfully.");
                return true;
            } else {
                System.out.println("Failed to delete transaction. Please try again.");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
            return false;
        }
    }
}
