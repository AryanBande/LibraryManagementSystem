package service;

import dao.UserDao;
import dto.User;

/**
 * Login Service
 * Handles user authentication and session management
 */
public class LoginService {
    private UserDao userDao;
    private User currentUser;
    
    public LoginService() {
        this.userDao = new UserDao();
        this.currentUser = null;
    }
    
    /**
     * Authenticate user login
     * @param email User email
     * @param password User password
     * @return true if login successful, false otherwise
     */
    public boolean login(String email, String password) {
        try {
            // Validate input
            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                System.out.println("Email and password cannot be empty.");
                return false;
            }
            
            // Attempt authentication
            User user = userDao.authenticateUser(email.trim(), password);
            
            if (user != null) {
                this.currentUser = user;
                System.out.println("Login successful! Welcome, " + user.getName());
                return true;
            } else {
                System.out.println("Invalid email or password. Please try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("Goodbye, " + currentUser.getName() + "!");
            currentUser = null;
        }
    }
    
    /**
     * Get currently logged in user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if current user is admin
     * @return true if current user is admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * Check if current user is regular user
     * @return true if current user is regular user, false otherwise
     */
    public boolean isCurrentUserRegularUser() {
        return currentUser != null && currentUser.isUser();
    }
    
    /**
     * Get current user ID
     * @return Current user ID or -1 if not logged in
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    /**
     * Get current user name
     * @return Current user name or "Guest" if not logged in
     */
    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : "Guest";
    }
    
    /**
     * Validate user session (check if user still exists in database)
     * @return true if session is valid, false otherwise
     */
    public boolean validateSession() {
        if (currentUser == null) {
            return false;
        }
        
        try {
            // Check if user still exists in database
            User dbUser = userDao.getUserById(currentUser.getId());
            
            if (dbUser == null) {
                System.out.println("Your account no longer exists. Please contact administrator.");
                logout();
                return false;
            }
            
            // Update current user data in case it was modified
            currentUser = dbUser;
            return true;
            
        } catch (Exception e) {
            System.err.println("Error validating session: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if user has permission to perform admin operations
     * @return true if user has admin permissions, false otherwise
     */
    public boolean hasAdminPermission() {
        if (!isLoggedIn()) {
            System.out.println("Please log in first.");
            return false;
        }
        
        if (!isCurrentUserAdmin()) {
            System.out.println("You don't have permission to perform this operation. Admin access required.");
            return false;
        }
        
        return validateSession();
    }
    
    /**
     * Check if user has permission to perform user operations
     * @return true if user has user permissions, false otherwise
     */
    public boolean hasUserPermission() {
        if (!isLoggedIn()) {
            System.out.println("Please log in first.");
            return false;
        }
        
        return validateSession();
    }
    
    /**
     * Display current user information
     */
    public void displayCurrentUserInfo() {
        if (currentUser == null) {
            System.out.println("No user logged in.");
        } else {
            System.out.println("Current User: " + currentUser.getName());
            System.out.println("Email: " + currentUser.getEmail());
            System.out.println("User Type: " + currentUser.getUserType());
            System.out.println("User ID: " + currentUser.getId());
        }
    }
    
    /**
     * Change current user password
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!hasUserPermission()) {
            return false;
        }
        
        // Validate old password
        if (!currentUser.getPassword().equals(oldPassword)) {
            System.out.println("Current password is incorrect.");
            return false;
        }
        
        // Validate new password
        if (newPassword == null || newPassword.trim().length() < 6) {
            System.out.println("New password must be at least 6 characters long.");
            return false;
        }
        
        try {
            // Update password
            currentUser.setPassword(newPassword);
            boolean success = userDao.updateUser(currentUser);
            
            if (success) {
                System.out.println("Password changed successfully.");
                return true;
            } else {
                System.out.println("Failed to change password. Please try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Initialize admin user if none exists
     * @return true if admin exists or was created, false otherwise
     */
    public boolean initializeAdminUser() {
        try {
            // Check if admin user already exists
            User adminUser = userDao.getUserByEmail("admin@library.com");
            
            if (adminUser == null) {
                // Create default admin user
                User newAdmin = new User("Library Admin", "admin@library.com", "admin123", "ADMIN");
                boolean created = userDao.createUser(newAdmin);
                
                if (created) {
                    System.out.println("Default admin user created:");
                    System.out.println("Email: admin@library.com");
                    System.out.println("Password: admin123");
                    return true;
                } else {
                    System.err.println("Failed to create default admin user.");
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error initializing admin user: " + e.getMessage());
            return false;
        }
    }
}