package ru.study.service;

public class Order {
    private int queue; // Index order number to calculate a discount
    private String companyName;
    private String orderTime;
    private int quantity; // Order quantity
    private int discount; // Order discount

    // 1. AI told me to add this for JSON files
    public Order() {
    }

    // 2. Full constructor
    public Order(int queue, String companyName, String orderTime, int quantity, int discount) {
        this.queue = queue;
        this.companyName = companyName;
        this.orderTime = orderTime;
        this.quantity = quantity;
        this.discount = discount;
    }

    // 3. Getters and Setters
    public int getQueue() { return queue; }
    public void setQueue(int queue) { this.queue = queue; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getOrderTime() { return orderTime; }
    public void setOrderTime(String orderTime) { this.orderTime = orderTime; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }

    // 4. toString()
    @Override
    public String toString() {
        return "Order{" +
                "queue=" + queue +
                ", companyName='" + companyName + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", quantity=" + quantity +
                ", discount=" + discount +
                '}';
    }
}