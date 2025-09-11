package dto;

public class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private int quantity;
    private int floor;
    private String shelve;
    
    public Book() {}
    
    public Book(String title, String author, String category, int quantity, int floor, String shelve) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
        this.floor = floor;
        this.shelve = shelve;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public int getFloor() {
        return floor;
    }
    
    public void setFloor(int floor) {
        this.floor = floor;
    }
    
    public String getShelve() {
        return shelve;
    }
    
    public void setShelve(String shelve) {
        this.shelve = shelve;
    }
    
    public boolean isAvailable() {
        return quantity > 0;
    }
    
    public void decrementQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }
    
    public void incrementQuantity() {
        quantity++;
    }
    
    @Override
    public String toString() {
        return String.format("Book{id=%d, title='%s', author='%s', category='%s', quantity=%d, floor=%d, shelve='%s'}", 
                           id, title, author, category, quantity, floor, shelve);
    }
    
    public String getDisplayInfo() {
        String availability = quantity > 0 ? "Available (" + quantity + ")" : "Not Available";
        return String.format("ID: %-3d | Title: %-25s | Author: %-20s | Category: %-12s | %s | Location: Floor %d, Shelf %s", 
                           id, title, author, category, availability, floor, shelve);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return id == book.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}