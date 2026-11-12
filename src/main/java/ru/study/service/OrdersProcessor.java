package ru.study.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrdersProcessor {

    // Added discount parameters constructor into a OrderProcessor
    private final BigDecimal pricePerKg;
    private final int startDiscount;
    private final int discountStep;

        public OrdersProcessor(BigDecimal pricePerKg, int startDiscount, int discountStep) {
            this.pricePerKg = pricePerKg;
            this.startDiscount = startDiscount;
            this.discountStep = discountStep;
        }

        public List<Order> process(List<Order> orders) {
            return orders.stream()
                    .sorted(Comparator.comparing(Order::getOrderTime))
                    .toList();
        }

        public void applyDiscounts(List<Order> sortedOrders) {
            BigDecimal hundred = BigDecimal.valueOf(100);

            for (int queue = 0; queue < sortedOrders.size(); queue++) {
                Order order = sortedOrders.get(queue);
                order.setQueue(BigDecimal.valueOf(queue));

                int discountMinus = queue * discountStep;
                int discountValue = (discountMinus > startDiscount)
                        ? 0
                        : startDiscount - discountMinus;
                order.setDiscount(BigDecimal.valueOf(discountValue));

                BigDecimal discountFraction = BigDecimal.valueOf(discountValue)
                        .divide(hundred, 4, RoundingMode.HALF_UP);
                BigDecimal multiplier = BigDecimal.ONE.subtract(discountFraction);

                BigDecimal total = order.getQuantity()
                        .multiply(pricePerKg)
                        .multiply(multiplier)
                        .setScale(2, RoundingMode.HALF_UP);

                order.setOrderPrice(total);
            }
        }

    // Maps the orders, excluding duplicates while adding their total order price
    public Map<String, BigDecimal> mapTheOrders(List<Order> sortedOrders) {
        return sortedOrders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCompanyName,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Order::getOrderPrice,
                                BigDecimal::add
                        )
                ));
    }
}