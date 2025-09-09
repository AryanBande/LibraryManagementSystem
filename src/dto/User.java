package dto;

/**
 * User Data Transfer Object
 * Represents a user in the library management system
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String userType; // 'USER' or 'ADMIN'
    
    // Default constructor
    public User() {}
    
    // Constructor with all fields
    public User(int id, String name, String email, String password, String userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }
    
    // Constructor without id (for new user creation)
    public User(String name, String email, String password, String userType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    // Check if user is admin
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(userType);
    }
    
    // Check if user is regular user
    public boolean isUser() {
        return "USER".equalsIgnoreCase(userType);
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', userType='%s'}", 
                           id, name, email, userType);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}