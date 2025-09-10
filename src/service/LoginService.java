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

            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                System.out.println("Email and password cannot be empty.");
                return false;
            }
            
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

    public void logout() {
        if (currentUser != null) {
            System.out.println("Goodbye, " + currentUser.getName() + "!");
            currentUser = null;
        }
    }
    

    public User getCurrentUser() {
        return currentUser;
    }
    

    public boolean isLoggedIn() {
        return currentUser != null;
    }
    

    public boolean isCurrentUserAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    

    public boolean isCurrentUserRegularUser() {
        return currentUser != null && currentUser.isUser();
    }
    

    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    

    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : "Guest";
    }
    

//    public boolean validateSession() {
//        if (currentUser == null) {
//            return false;
//        }
//
//        try {
//            // Check if user still exists in database
//            User dbUser = userDao.getUserById(currentUser.getId());
//
//            if (dbUser == null) {
//                System.out.println("Your account no longer exists. Please contact administrator.");
//                logout();
//                return false;
//            }
//
//            // Update current user data in case it was modified
//            currentUser = dbUser;
//            return true;
//
//        } catch (Exception e) {
//            System.err.println("Error validating session: " + e.getMessage());
//            return false;
//        }
//    }

//    public boolean hasAdminPermission() {
//
//        if (!isCurrentUserAdmin()) {
//            System.out.println("You don't have permission to perform this operation. Admin access required.");
//            return false;
//        }
//
//        return true;
//        return validateSession();
//    }
    

//    public boolean hasUserPermission() {
//        if (!isLoggedIn()) {
//            System.out.println("Please log in first.");
//            return false;
//        }
//
//        return validateSession();
//    }
    

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
    

    public boolean changePassword(String oldPassword, String newPassword) {
//        if (!hasUserPermission()) {
//            return false;
//        }
        
        if (!currentUser.getPassword().equals(oldPassword)) {
            System.out.println("Current password is incorrect.");
            return false;
        }
        
        if (newPassword == null || newPassword.trim().length() < 6) {
            System.out.println("New password must be at least 6 characters long.");
            return false;
        }
        
        try {
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

//    public boolean initializeAdminUser() {
//        try {
//            // Check if admin user already exists
//            User adminUser = userDao.getUserByEmail("admin@library.com");
//
//            if (adminUser == null) {
//                // Create default admin user
//                User newAdmin = new User("Library Admin", "admin@library.com", "admin123", "ADMIN");
//                boolean created = userDao.createUser(newAdmin);
//
//                if (created) {
//                    System.out.println("Default admin user created:");
//                    System.out.println("Email: admin@library.com");
//                    System.out.println("Password: admin123");
//                    return true;
//                } else {
//                    System.err.println("Failed to create default admin user.");
//                    return false;
//                }
//            }
//
//            return true;
//
//        } catch (Exception e) {
//            System.err.println("Error initializing admin user: " + e.getMessage());
//            return false;
//        }
//    }
}