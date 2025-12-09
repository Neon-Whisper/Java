package entity;

public class Order {
    private int orderId;
    private int userId;
    private int itemId;
    private String orderStatus;
    private double transactionPrice;

    // 构造方法、getter和setter
    public Order() {}

    public Order(int orderId, int userId, int itemId, String orderStatus, double transactionPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.itemId = itemId;
        this.orderStatus = orderStatus;
        this.transactionPrice = transactionPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(double transactionPrice) {
        this.transactionPrice = transactionPrice;
    }
}