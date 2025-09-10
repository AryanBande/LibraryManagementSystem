package service;

import dao.UserDao;
import dto.User;
import java.util.List;
import java.util.regex.Pattern;


public class UserService {
    private UserDao userDao;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    public UserService() {
        this.userDao = new UserDao();
    }
    

    public boolean createUser(String name, String email, String password, String userType) {
        try {

            if (!validateUserInput(name, email, password, userType)) {
                return false;
            }

            if (userDao.emailExists(email)) {
                System.out.println("Email already exists. Please use a different email address.");
                return false;
            }
            

            User user = new User(name.trim(), email.trim().toLowerCase(), password, userType.toUpperCase());
            

            boolean success = userDao.createUser(user);
            
            if (success) {
                System.out.println("User created successfully: " + name);
                return true;
            } else {
                System.out.println("Failed to create user. Please try again.");
                return false;
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input for user creation: " + e.getMessage());
            return false;
        } catch (RuntimeException e) {
            System.err.println("System error during user creation: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error during user creation: " + e.getMessage());
            return false;
        }
    }
    

    public List<User> getAllUsers() {
        try {
            return userDao.getAllUsers();
        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
            return null;
        }
    }
    

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
    
//
//    public boolean updateUser(int userId, String name, String email, String password, String userType) {
//        try {
//            // Get existing user
//            User existingUser = userDao.getUserById(userId);
//            if (existingUser == null) {
//                System.out.println("User not found.");
//                return false;
//            }
//
//            // Validate input
//            if (!validateUserInput(name, email, password, userType)) {
//                return false;
//            }
//
//            // Check if email is being changed and if new email already exists
//            if (!existingUser.getEmail().equals(email.trim().toLowerCase())) {
//                if (userDao.emailExists(email)) {
//                    System.out.println("Email already exists. Please use a different email address.");
//                    return false;
//                }
//            }
//
//            // Update user object
//            existingUser.setName(name.trim());
//            existingUser.setEmail(email.trim().toLowerCase());
//            existingUser.setPassword(password);
//            existingUser.setUserType(userType.toUpperCase());
//
//            // Save to database
//            boolean success = userDao.updateUser(existingUser);
//
//            if (success) {
//                System.out.println("User updated successfully: " + name);
//                return true;
//            } else {
//                System.out.println("Failed to update user. Please try again.");
//                return false;
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error updating user: " + e.getMessage());
//            return false;
//        }
//    }
    

    public boolean deleteUser(int userId) {
        try {
            if (userId <= 0) {
                System.out.println("Invalid user ID.");
                return false;
            }
            
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
            
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input for user deletion: " + e.getMessage());
            return false;
        } catch (RuntimeException e) {
            System.err.println("System error during user deletion: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error during user deletion: " + e.getMessage());
            return false;
        }
    }
    

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
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid user type: " + e.getMessage());
            return null;
        } catch (RuntimeException e) {
            System.err.println("System error while getting users by type: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error while getting users by type: " + e.getMessage());
            return null;
        }
    }


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
            
        } catch (RuntimeException e) {
            System.err.println("System error while displaying users: " + e.getMessage());
            System.out.println("Unable to display users at this time. Please try again later.");
        } catch (Exception e) {
            System.err.println("Unexpected error while displaying users: " + e.getMessage());
            System.out.println("Unable to display users at this time. Please try again later.");
        }
    }
    

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

    private boolean validateUserInput(String name, String email, String password, String userType) {

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
    

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    

    public boolean userExists(int userId) {
        try {
            return userDao.getUserById(userId) != null;
        } catch (Exception e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }
    

    public int getTotalUsersCount() {
        try {
            List<User> users = userDao.getAllUsers();
            return users != null ? users.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting users count: " + e.getMessage());
            return 0;
        }
    }
    

    public int getAdminsCount() {
        try {
            List<User> admins = userDao.getUsersByType("ADMIN");
            return admins != null ? admins.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting admins count: " + e.getMessage());
            return 0;
        }
    }
    

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