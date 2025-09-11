package service;

import dao.UserDao;
import dto.User;

public class LoginService {
    private UserDao userDao;
    private User currentUser;
    
    public LoginService() {
        this.userDao = new UserDao();
        this.currentUser = null;
    }
    

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
            
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid login credentials format: " + e.getMessage());
            System.out.println("Please check your email and password format.");
            return false;
        } catch (RuntimeException e) {
            System.err.println("System error during login: " + e.getMessage());
            System.out.println("Login service is temporarily unavailable. Please try again later.");
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            System.out.println("An unexpected error occurred during login. Please try again.");
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
            
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input for password change: " + e.getMessage());
            return false;
        } catch (RuntimeException e) {
            System.err.println("System error during password change: " + e.getMessage());
            System.out.println("Password change service is temporarily unavailable. Please try again later.");
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error during password change: " + e.getMessage());
            System.out.println("An unexpected error occurred. Please try again.");
            return false;
        }
    }
}