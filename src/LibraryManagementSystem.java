import service.*;
import dto.User;
import dto.Book;
import dto.Transaction;
import java.util.List;
import java.util.Scanner;

/**
 * Library Management System - Main CLI Application
 * Provides console-based interface for library operations
 */
public class LibraryManagementSystem {

    private static LoginService loginService;
    private static UserService userService;
    private static BookService bookService;
    private static TransactionService transactionService;
    private static Scanner scanner;

    public static void main(String[] args) {
        // Initialize services
        loginService = new LoginService();
        userService = new UserService();
        bookService = new BookService();
        transactionService = new TransactionService();
        scanner = new Scanner(System.in);

        // Initialize application
        initializeApplication();

        // Main application loop
        runApplication();

        // Cleanup
        scanner.close();
        System.out.println("Thank you for using Library Management System!");
    }

    /**
     * Initialize the application and database
     */
    private static void initializeApplication() {
        System.out.println("=".repeat(60));
        System.out.println("      LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println("Initializing application...");

        try {
            // Test database connection
            dao.DatabaseService dbTestService = new dao.DatabaseService();
            if (dbTestService.testConnection()) {
                System.out.println("Database connection established successfully.");
            } else {
                System.err.println("\nCRITICAL: Database connection failed.");
                System.err.println("Please check your Oracle Database configuration in 'src/utils/DatabaseConnection.java'.");
                System.err.println("Ensure the database is running and your credentials are correct.");
                System.err.println("Exiting application.");
                System.exit(1);
            }

            // Initialize default admin user if needed
            loginService.initializeAdminUser();

            System.out.println("Application initialized successfully.");
            System.out.println("=".repeat(60));

        } catch (Exception e) {
            System.err.println("An unexpected error occurred during initialization: " + e.getMessage());
            System.err.println("Please check your database configuration and table schema.");
            System.exit(1);
        }
    }

    /**
     * Main application loop
     */
    private static void runApplication() {
        boolean running = true;

        while (running) {
            try {
                if (!loginService.isLoggedIn()) {
                    running = showLoginMenu();
                } else if (loginService.isCurrentUserAdmin()) {
                    running = showAdminMenu();
                } else {
                    running = showUserMenu();
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    /**
     * Show login menu
     * @return true to continue, false to exit
     */
    private static boolean showLoginMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           LOGIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.println("=".repeat(50));
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    return handleLogin();
                case 2:
                    System.out.println("Goodbye!");
                    return false;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return true;
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
            return true;
        }
    }

    /**
     * Handle user login
     * @return true to continue, false to exit
     */
    private static boolean handleLogin() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("LOGIN");
        System.out.println("-".repeat(30));

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        boolean loginSuccess = loginService.login(email, password);

        if (!loginSuccess) {
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }

        return true;
    }

    /**
     * Show admin menu
     * @return true to continue, false to exit
     */
    private static boolean showAdminMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         ADMIN PANEL - " + loginService.getCurrentUserName());
        System.out.println("=".repeat(60));
        System.out.println("User Management:");
        System.out.println("  1. Add User");
        System.out.println("  2. Remove User");
        System.out.println("  3. List All Users");
        System.out.println();
        System.out.println("Book Management:");
        System.out.println("  4. Add Book");
        System.out.println("  5. Remove Book");
        System.out.println("  6. Update Book Quantity");
        System.out.println("  7. List All Books");
        System.out.println();
        System.out.println("Transaction Management:");
        System.out.println("  8. View Pending Requests");
        System.out.println("  9. Approve/Deny Requests");
        System.out.println(" 10. View All Transactions");
        System.out.println(" 11. View Issued Books");
        System.out.println(" 12. Return Books (With Fine Collection)");
        System.out.println();
        System.out.println("System:");
        System.out.println(" 13. Change Password");
        System.out.println(" 14. Logout");
        System.out.println(" 15. Exit");
        System.out.println("=".repeat(60));
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: handleAddUser(); break;
                case 2: handleRemoveUser(); break;
                case 3: userService.displayAllUsers(); break;
                case 4: handleAddBook(); break;
                case 5: handleRemoveBook(); break;
                case 6: handleUpdateBookQuantity(); break;
                case 7: bookService.displayAllBooks(); break;
                case 8: transactionService.displayPendingTransactions(); break;
                case 9: handleApproveOrDenyRequest(); break;
                case 10: transactionService.displayAllTransactions(); break;
                case 11: transactionService.displayApprovedTransactions(); break;
                case 12: handleAdminReturnBook(); break;
                case 13: handleChangePassword(); break;
                case 14: loginService.logout(); break;
                case 15: return false;
                default: System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
        }

        if (loginService.isLoggedIn()) {
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }

        return true;
    }

    /**
     * Show user menu
     * @return true to continue, false to exit
     */
    private static boolean showUserMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         STUDENT MENU - " + loginService.getCurrentUserName());
        System.out.println("=".repeat(60));
        System.out.println("  1. View Available Books");
        System.out.println("  2. Search Books");
        System.out.println("  3. Request Book Issue");
        System.out.println("  4. View My Issued Books");
        System.out.println("  5. View My All Requests");
        System.out.println("  6. Change Password");
        System.out.println("  7. Logout");
        System.out.println("  8. Exit");
        System.out.println("=".repeat(60));
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: bookService.displayAvailableBooks(); break;
                case 2: handleSearchBooks(); break;
                case 3: handleRequestBookIssue(); break;
                case 4: handleViewMyIssuedBooks(); break;
                case 5: handleViewMyAllRequests(); break;
                case 6: handleChangePassword(); break;
                case 7: loginService.logout(); break;
                case 8: return false;
                default: System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
        }

        if (loginService.isLoggedIn()) {
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }

        return true;
    }

    /**
     * Handle adding new user
     */
    private static void handleAddUser() {
        if (!loginService.hasAdminPermission()) return;

        System.out.println("\n" + "-".repeat(30));
        System.out.println("ADD NEW USER");
        System.out.println("-".repeat(30));

        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("User Type (USER/ADMIN): ");
        String userType = scanner.nextLine().trim();

        userService.createUser(name, email, password, userType);
    }

    /**
     * Handle removing user
     */
    private static void handleRemoveUser() {
        if (!loginService.hasAdminPermission()) return;

        userService.displayAllUsers();

        System.out.println("\n" + "-".repeat(30));
        System.out.println("REMOVE USER");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Enter User ID to remove: ");
            int userId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (userId == loginService.getCurrentUserId()) {
                System.out.println("You cannot delete your own account.");
                return;
            }

            System.out.print("Are you sure? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes")) {
                userService.deleteUser(userId);
            } else {
                System.out.println("Operation cancelled.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    /**
     * Handle adding new book
     */
    private static void handleAddBook() {
        if (!loginService.hasAdminPermission()) return;

        System.out.println("\n" + "-".repeat(30));
        System.out.println("ADD NEW BOOK");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Title: ");
            String title = scanner.nextLine().trim();

            System.out.print("Author: ");
            String author = scanner.nextLine().trim();

            System.out.print("Category: ");
            String category = scanner.nextLine().trim();

            System.out.print("Quantity: ");
            int quantity = scanner.nextInt();

            System.out.print("Floor: ");
            int floor = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Shelve: ");
            String shelve = scanner.nextLine().trim();

            bookService.createBook(title, author, category, quantity, floor, shelve);
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter valid data.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    /**
     * Handle removing book
     */
    private static void handleRemoveBook() {
        if (!loginService.hasAdminPermission()) return;

        bookService.displayAllBooks();

        System.out.println("\n" + "-".repeat(30));
        System.out.println("REMOVE BOOK");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Enter Book ID to remove: ");
            int bookId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Are you sure? (y/N): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes")) {
                bookService.deleteBook(bookId);
            } else {
                System.out.println("Operation cancelled.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    /**
     * Handle updating book quantity
     */
    private static void handleUpdateBookQuantity() {
        if (!loginService.hasAdminPermission()) return;

        bookService.displayAllBooks();

        System.out.println("\n" + "-".repeat(30));
        System.out.println("UPDATE BOOK QUANTITY");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Enter Book ID: ");
            int bookId = scanner.nextInt();

            System.out.print("Enter New Quantity: ");
            int newQuantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            bookService.updateBookQuantity(bookId, newQuantity);
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter valid numbers.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    /**
     * Handle approving or denying requests
     */
    private static void handleApproveOrDenyRequest() {
        if (!loginService.hasAdminPermission()) return;

        List<Transaction> pendingTransactions = transactionService.getPendingTransactions();

        if (pendingTransactions == null || pendingTransactions.isEmpty()) {
            System.out.println("No pending requests found.");
            return;
        }

        transactionService.displayPendingTransactions();

        System.out.println("\n" + "-".repeat(30));
        System.out.println("APPROVE/DENY REQUEST");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Enter Transaction ID: ");
            int transactionId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.println("1. Approve");
            System.out.println("2. Deny");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    transactionService.approveBookRequest(transactionId);
                    break;
                case 2:
                    transactionService.denyBookRequest(transactionId);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter valid numbers.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    /**
     * Handle admin return book with fine collection
     */
    private static void handleAdminReturnBook() {
        if (!loginService.hasAdminPermission()) return;

        List<Transaction> issuedBooks = transactionService.getApprovedTransactions();

        if (issuedBooks == null || issuedBooks.isEmpty()) {
            System.out.println("No issued books found.");
            return;
        }

        transactionService.displayIssuedBooksWithFines();

        System.out.println("\n" + "-".repeat(50));
        System.out.println("RETURN BOOK WITH FINE COLLECTION");
        System.out.println("-".repeat(50));

        try {
            System.out.print("Enter Transaction ID: ");
            int transactionId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Get transaction details for fine calculation
            Transaction transaction = transactionService.getTransactionById(transactionId);
            if (transaction == null) {
                System.out.println("Transaction not found.");
                return;
            }

            if (!transaction.isActive()) {
                System.out.println("This book is not currently issued.");
                return;
            }

            double fine = transaction.calculateFine();

            if (fine > 0) {
                System.out.println("\n" + "=".repeat(40));
                System.out.println("FINE DETAILS");
                System.out.println("=".repeat(40));
                System.out.println("Student: " + transaction.getUserName());
                System.out.println("Book: " + transaction.getBookTitle());
                System.out.println("Issue Date: " + transaction.getIssueDate());
                System.out.println("Due Date: " + transaction.getDueDate());
                System.out.println("Days Overdue: " + transaction.getOverdueDays());
                System.out.println("Fine Amount: ₹" + String.format("%.2f", fine));
                System.out.println("=".repeat(40));

                System.out.print("Has the fine been collected? (y/N): ");
                String fineCollected = scanner.nextLine().trim().toLowerCase();

                boolean collectFine = fineCollected.equals("y") || fineCollected.equals("yes");

                System.out.print("Proceed with book return? (y/N): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if (confirm.equals("y") || confirm.equals("yes")) {
                    transactionService.adminReturnBook(transactionId, collectFine);
                } else {
                    System.out.println("Operation cancelled.");
                }
            } else {
                System.out.println("No fine applicable for this book.");
                System.out.print("Proceed with book return? (y/N): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if (confirm.equals("y") || confirm.equals("yes")) {
                    transactionService.adminReturnBook(transactionId, false);
                } else {
                    System.out.println("Operation cancelled.");
                }
            }

        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    /**
     * Handle searching books
     */
    private static void handleSearchBooks() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("SEARCH BOOKS");
        System.out.println("-".repeat(30));
        System.out.println("1. Search by Title");
        System.out.println("2. Search by Author");
        System.out.println("3. Search by Category");
        System.out.println("4. General Search");
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter search term: ");
            String searchTerm = scanner.nextLine().trim();

            List<Book> results = null;

            switch (choice) {
                case 1:
                    results = bookService.searchBooksByTitle(searchTerm);
                    break;
                case 2:
                    results = bookService.searchBooksByAuthor(searchTerm);
                    break;
                case 3:
                    results = bookService.searchBooksByCategory(searchTerm);
                    break;
                case 4:
                    results = bookService.searchBooks(searchTerm);
                    break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            if (results != null) {
                bookService.displaySearchResults(results, searchTerm);
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please try again.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    /**
     * Handle requesting book issue
     */
    private static void handleRequestBookIssue() {
        if (!loginService.hasUserPermission()) return;

        bookService.displayAvailableBooks();

        System.out.println("\n" + "-".repeat(30));
        System.out.println("REQUEST BOOK ISSUE");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Enter Book ID to request: ");
            int bookId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            transactionService.requestBookIssue(loginService.getCurrentUserId(), bookId);
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid Book ID.");
            scanner.nextLine(); // Clear invalid input
        }
    }

    /**
     * Handle viewing user's issued books
     */
    private static void handleViewMyIssuedBooks() {
        if (!loginService.hasUserPermission()) return;

        transactionService.displayUserActiveTransactions(
                loginService.getCurrentUserId(),
                loginService.getCurrentUserName()
        );
    }

    /**
     * Handle viewing user's all requests
     */
    private static void handleViewMyAllRequests() {
        if (!loginService.hasUserPermission()) return;

        transactionService.displayUserTransactions(
                loginService.getCurrentUserId(),
                loginService.getCurrentUserName()
        );
    }

    /**
     * Handle password change
     */
    private static void handleChangePassword() {
        if (!loginService.hasUserPermission()) return;

        System.out.println("\n" + "-".repeat(30));
        System.out.println("CHANGE PASSWORD");
        System.out.println("-".repeat(30));

        System.out.print("Current Password: ");
        String currentPassword = scanner.nextLine().trim();

        System.out.print("New Password: ");
        String newPassword = scanner.nextLine().trim();

        System.out.print("Confirm New Password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match.");
            return;
        }

        loginService.changePassword(currentPassword, newPassword);
    }

    /**
     * Display welcome message and instructions
     */
    private static void displayWelcomeMessage() {
        System.out.println("Welcome to the Library Management System!");
        System.out.println("Please log in to continue.");
        System.out.println();
        System.out.println("Default Admin Credentials:");
        System.out.println("Email: admin@library.com");
        System.out.println("Password: admin123");
        System.out.println();
    }
}
