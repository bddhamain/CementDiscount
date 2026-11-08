package ru.study.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrdersProcessor {

    //Sorts orders by orderTime (earliest first)
    public List<Order> process(List<Order> orders) {
        return orders.stream()
                .sorted(Comparator.comparing(order -> order.getOrderTime()))
                .toList();
    }

    // Assigns queue positions, discounts, and total costs, based on the order's position in the list
    public void applyDiscounts(List<Order> sortedOrders) {
        int discountStep = 5; // Discount decrement
        int startDiscount = 50; // Max. dicsount
        int pricePerKg = 100; // Cost for 1 kg of Cement in USD

        for (int queue = 0; queue < sortedOrders.size(); queue++) {
            Order order = sortedOrders.get(queue);
            order.setQueue(queue);

            int discountMinus = queue * discountStep;
            if (discountMinus > startDiscount) {
                order.setDiscount(0);
            } else {
                order.setDiscount(startDiscount - discountMinus);
            }

            order.setOrderPice((int) (order.getQuantity() * pricePerKg * (1 - order.getDiscount() / 100.0)));

        }
    }

    //Maps the orders, excluding duplicates while adding their total order price1
    public Map<String, Integer> mapTheOrders(List<Order> sortedOrders) {
        return sortedOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCompanyName(),
                        Collectors.summingInt(order -> order.getOrderPrice())
                ));
    }

}