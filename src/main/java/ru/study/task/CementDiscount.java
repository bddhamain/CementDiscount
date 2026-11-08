package ru.study.task;

import ru.study.service.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class CementDiscount {

    public static void main(String[] args) {
        Path file = Path.of("discount_day_without_ext");
        OrderSource source = new NoExtOrderSourceAdapter(file);

        List<Order> orders = source.read();

        OrdersProcessor processor = new OrdersProcessor();

        List<Order> sortedOrders = processor.process(orders);
        processor.applyDiscounts(sortedOrders);
        Map<String, Integer> finalOrderList = processor.mapTheOrders(sortedOrders);

        finalOrderList.forEach((companyName, orderPrice) -> System.out.println(companyName + ": " + orderPrice + "$"));
    }

}