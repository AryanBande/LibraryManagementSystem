package service;

import dao.UserDao;
import dto.User;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User Service
 * Handles user management business logic
 */
public class UserService {
    private UserDao userDao;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    public UserService() {
        this.userDao = new UserDao();
    }
    
    /**
     * Create a new user with validation
     * @param name User name
     * @param email User email
     * @param password User password
     * @param userType User type ('USER' or 'ADMIN')
     * @return true if user created successfully, false otherwise
     */
    public boolean createUser(String name, String email, String password, String userType) {
        try {
            // Validate input
            if (!validateUserInput(name, email, password, userType)) {
                return false;
            }
            
            // Check if email already exists
            if (userDao.emailExists(email)) {
                System.out.println("Email already exists. Please use a different email address.");
                return false;
            }
            
            // Create user object
            User user = new User(name.trim(), email.trim().toLowerCase(), password, userType.toUpperCase());
            
            // Save to database
            boolean success = userDao.createUser(user);
            
            if (success) {
                System.out.println("User created successfully: " + name);
                return true;
            } else {
                System.out.println("Failed to create user. Please try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        try {
            return userDao.getAllUsers();
        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        try {
            if (userId <= 0) {
                System.out.println("Invalid user ID.");
                return null;
            }
            
            return userDao.getUserById(userId);
        } catch (Exception e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get user by email
     * @param email User email
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                System.out.println("Email cannot be empty.");
                return null;
            }
            
            return userDao.getUserByEmail(email.trim().toLowerCase());
        } catch (Exception e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Update user information
     * @param userId User ID
     * @param name New name
     * @param email New email
     * @param password New password
     * @param userType New user type
     * @return true if updated successfully, false otherwise
     */
    public boolean updateUser(int userId, String name, String email, String password, String userType) {
        try {
            // Get existing user
            User existingUser = userDao.getUserById(userId);
            if (existingUser == null) {
                System.out.println("User not found.");
                return false;
            }
            
            // Validate input
            if (!validateUserInput(name, email, password, userType)) {
                return false;
            }
            
            // Check if email is being changed and if new email already exists
            if (!existingUser.getEmail().equals(email.trim().toLowerCase())) {
                if (userDao.emailExists(email)) {
                    System.out.println("Email already exists. Please use a different email address.");
                    return false;
                }
            }
            
            // Update user object
            existingUser.setName(name.trim());
            existingUser.setEmail(email.trim().toLowerCase());
            existingUser.setPassword(password);
            existingUser.setUserType(userType.toUpperCase());
            
            // Save to database
            boolean success = userDao.updateUser(existingUser);
            
            if (success) {
                System.out.println("User updated successfully: " + name);
                return true;
            } else {
                System.out.println("Failed to update user. Please try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete user by ID
     * @param userId User ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteUser(int userId) {
        try {
            if (userId <= 0) {
                System.out.println("Invalid user ID.");
                return false;
            }
            
            // Get user first to show confirmation
            User user = userDao.getUserById(userId);
            if (user == null) {
                System.out.println("User not found.");
                return false;
            }
            
            // Delete user
            boolean success = userDao.deleteUser(userId);
            
            if (success) {
                System.out.println("User deleted successfully: " + user.getName());
                return true;
            } else {
                System.out.println("Failed to delete user. Please try again.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get users by type
     * @param userType User type ('USER' or 'ADMIN')
     * @return List of users with specified type
     */
    public List<User> getUsersByType(String userType) {
        try {
            if (userType == null || userType.trim().isEmpty()) {
                System.out.println("User type cannot be empty.");
                return null;
            }
            
            String normalizedUserType = userType.trim().toUpperCase();
            if (!normalizedUserType.equals("USER") && !normalizedUserType.equals("ADMIN")) {
                System.out.println("Invalid user type. Must be 'USER' or 'ADMIN'.");
                return null;
            }
            
            return userDao.getUsersByType(normalizedUserType);
        } catch (Exception e) {
            System.err.println("Error getting users by type: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Display all users in a formatted table
     */
    public void displayAllUsers() {
        try {
            List<User> users = getAllUsers();
            
            if (users == null || users.isEmpty()) {
                System.out.println("No users found.");
                return;
            }
            
            System.out.println("\n" + "=".repeat(100));
            System.out.println("ALL USERS");
            System.out.println("=".repeat(100));
            System.out.printf("%-5s | %-25s | %-30s | %-10s%n", "ID", "Name", "Email", "Type");
            System.out.println("-".repeat(100));
            
            for (User user : users) {
                System.out.printf("%-5d | %-25s | %-30s | %-10s%n", 
                    user.getId(), 
                    truncateString(user.getName(), 25), 
                    truncateString(user.getEmail(), 30), 
                    user.getUserType());
            }
            
            System.out.println("-".repeat(100));
            System.out.println("Total users: " + users.size());
            
        } catch (Exception e) {
            System.err.println("Error displaying users: " + e.getMessage());
        }
    }
    
    /**
     * Display users by type in a formatted table
     * @param userType User type ('USER' or 'ADMIN')
     */
    public void displayUsersByType(String userType) {
        try {
            List<User> users = getUsersByType(userType);
            
            if (users == null || users.isEmpty()) {
                System.out.println("No " + userType.toLowerCase() + "s found.");
                return;
            }
            
            String title = userType.toUpperCase() + "S";
            System.out.println("\n" + "=".repeat(100));
            System.out.println(title);
            System.out.println("=".repeat(100));
            System.out.printf("%-5s | %-25s | %-30s%n", "ID", "Name", "Email");
            System.out.println("-".repeat(100));
            
            for (User user : users) {
                System.out.printf("%-5d | %-25s | %-30s%n", 
                    user.getId(), 
                    truncateString(user.getName(), 25), 
                    truncateString(user.getEmail(), 30));
            }
            
            System.out.println("-".repeat(100));
            System.out.println("Total " + userType.toLowerCase() + "s: " + users.size());
            
        } catch (Exception e) {
            System.err.println("Error displaying users by type: " + e.getMessage());
        }
    }
    
    /**
     * Validate user input
     * @param name User name
     * @param email User email
     * @param password User password
     * @param userType User type
     * @return true if valid, false otherwise
     */
    private boolean validateUserInput(String name, String email, String password, String userType) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return false;
        }
        
        if (name.trim().length() < 2) {
            System.out.println("Name must be at least 2 characters long.");
            return false;
        }
        
        if (name.trim().length() > 100) {
            System.out.println("Name cannot exceed 100 characters.");
            return false;
        }
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Email cannot be empty.");
            return false;
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            System.out.println("Please enter a valid email address.");
            return false;
        }
        
        if (email.trim().length() > 150) {
            System.out.println("Email cannot exceed 150 characters.");
            return false;
        }
        
        // Validate password
        if (password == null || password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return false;
        }
        
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return false;
        }
        
        if (password.length() > 100) {
            System.out.println("Password cannot exceed 100 characters.");
            return false;
        }
        
        // Validate user type
        if (userType == null || userType.trim().isEmpty()) {
            System.out.println("User type cannot be empty.");
            return false;
        }
        
        String normalizedUserType = userType.trim().toUpperCase();
        if (!normalizedUserType.equals("USER") && !normalizedUserType.equals("ADMIN")) {
            System.out.println("Invalid user type. Must be 'USER' or 'ADMIN'.");
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
     * Check if user exists
     * @param userId User ID
     * @return true if user exists, false otherwise
     */
    public boolean userExists(int userId) {
        try {
            return userDao.getUserById(userId) != null;
        } catch (Exception e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get total number of users
     * @return Total number of users
     */
    public int getTotalUsersCount() {
        try {
            List<User> users = userDao.getAllUsers();
            return users != null ? users.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting users count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get total number of admins
     * @return Total number of admin users
     */
    public int getAdminsCount() {
        try {
            List<User> admins = userDao.getUsersByType("ADMIN");
            return admins != null ? admins.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting admins count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get total number of regular users
     * @return Total number of regular users
     */
    public int getRegularUsersCount() {
        try {
            List<User> users = userDao.getUsersByType("USER");
            return users != null ? users.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting regular users count: " + e.getMessage());
            return 0;
        }
    }
}