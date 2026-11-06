package ru.study.service;

import java.time.LocalDateTime;

public class Order {
    private int queue; // Index order number to calculate a discount
    public String companyName;
    public LocalDateTime orderTime;
    private int quantity; // Order quantity
    private int discount; // Order discount
    public int orderPrice; // Order price with discount

    // 2. Full constructor
    public Order(int queue, String companyName, LocalDateTime orderTime, int quantity, int discount, int orderPrice) {
        this.queue = queue;
        this.companyName = companyName;
        this.orderTime = orderTime;
        this.quantity = quantity;
        this.discount = discount;
        this.orderPrice = orderPrice;
    }

    // 3. Getters and Setters
    public int getQueue() { return queue; }
    public void setQueue(int queue) { this.queue = queue; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }

    public int getOrderPrice() { return orderPrice; }
    public void setOrderPice(int orderPrice) { this.orderPrice = orderPrice; }

    // 4. toString()
    @Override
    public String toString() {
        return "Order{" +
                "queue=" + queue +
                ", companyName='" + companyName + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", quantity=" + quantity +
                ", discount=" + discount +
                ", orderPrice=" + orderPrice +
                '}';
    }
}