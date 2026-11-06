package ru.study.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrdersProcessor {

    /** Sorts orders by orderTime (earliest first). */
    public List<Order> process(List<Order> orders) {
        return orders.stream()
                .sorted(Comparator.comparing(order -> order.orderTime))
                .toList();
    }

    /** Assigns queue positions and discounts, based on the order's position in the list. */
    public void applyDiscounts(List<Order> sortedOrders) {
        int discountStep = 5;
        int startDiscount = 50;
        int pricePerKg = 100;

        for (int queue = 0; queue < sortedOrders.size(); queue++) {
            Order order = sortedOrders.get(queue);
            order.setQueue(queue);

            int discountMinus = queue * discountStep;
            if (discountMinus > startDiscount) {
                order.setDiscount(0);
            } else {
                order.setDiscount(startDiscount - discountMinus);
            }

            order.setOrderPice(order.getQuantity() * pricePerKg * (1 - order.getDiscount()/100));

        }
    }

    public Map<String, Integer> mapTheOrders(List<Order> sortedOrders) {
        return sortedOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.companyName,
                        Collectors.summingInt(order -> order.orderPrice)
                ));
    }

}