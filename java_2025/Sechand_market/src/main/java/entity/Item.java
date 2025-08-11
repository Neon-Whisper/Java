package entity;
import java.util.Date;

public class Item {
    private int itemId;
    private int userId;
    private String itemName;
    private String category;
    private double price;
    private String description;
    private String status;
    private String imagePath; // 新增字段

    // 构造方法、getter和setter
    public Item() {}

    public Item(int itemId, int userId, String itemName, String category, double price, String description, String status) {
        this.itemId = itemId;
        this.userId = userId;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.status = status;
        this.imagePath = imagePath; // 新增赋值
    }
    public int getItemId() {
        return itemId;
    }
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}