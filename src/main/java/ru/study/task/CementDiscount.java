package ru.study.task;

import ru.study.service.*;
import java.nio.file.Path;
import java.util.List;

public class CementDiscount {

    public static void main(String[] args) {
        Path file = Path.of("discount_day.txt");
        OrderSource source = new CsvOrderSourceAdapter(file);

        List<Order> orders = source.read();

        OrdersProcessor processor = new OrdersProcessor();
        processor.process(orders);

        orders.forEach(System.out::println);
    }

}