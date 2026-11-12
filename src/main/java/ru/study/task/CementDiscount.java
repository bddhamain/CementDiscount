package ru.study.task;

import ru.study.service.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CementDiscount {

    public static void main(String[] args) {
        Path fileNoExt = Path.of("discount_day_without_ext");
        Path fileCsv = Path.of("discount_day.txt");
        OrderSource sourceNoExt = new NoExtOrderSourceAdapter(fileNoExt);
        OrderSource sourceCsv = new CsvOrderSourceAdapter(fileCsv);

        // Reading both files
        List<Order> ordersNoExt = sourceNoExt.read();
        List<Order> ordersCsv = sourceCsv.read();

        // Combining them inside one list
        List<Order> orders = new ArrayList<>(ordersNoExt);
        orders.addAll(ordersCsv);

        OrdersProcessor processor = new OrdersProcessor(
                BigDecimal.valueOf(100),  // pricePerKg
                50,                       // startDiscount
                5                         // discountStep
        );

        List<Order> sortedOrders = processor.process(orders);
        processor.applyDiscounts(sortedOrders);
        Map<String, BigDecimal> finalOrderList = processor.mapTheOrders(sortedOrders);


        // Terminal final list println
        finalOrderList.forEach((companyName, orderPrice) ->
                System.out.println(companyName + ": " + orderPrice + " $"));

        Path output = Path.of("discount_result.txt");

        // Save final list to file
        List<String> lines = new ArrayList<>();
        finalOrderList.forEach((company, price) ->
                lines.add(company + ": " + price + "$"));

        try {
            Files.write(output, lines);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file " + output, e);
        }

    }

}