package ru.study.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Order {

    private BigDecimal queue;
    private String companyName;
    private LocalDateTime orderTime;
    private BigDecimal quantity;
    private BigDecimal discount;
    private BigDecimal orderPrice;

    public Order(int queue, String companyName, LocalDateTime orderTime,
                 int quantity, int discount, double orderPrice) {
        this.queue       = BigDecimal.valueOf(queue);
        this.companyName = companyName;
        this.orderTime   = orderTime;
        this.quantity    = BigDecimal.valueOf(quantity);
        this.discount    = BigDecimal.valueOf(discount);
        this.orderPrice  = BigDecimal.valueOf(orderPrice);
    }

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