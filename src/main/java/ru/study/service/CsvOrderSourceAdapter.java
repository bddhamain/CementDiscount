package ru.study.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvOrderSourceAdapter implements OrderSource {

    private final CsvOrderReader reader;

    public CsvOrderSourceAdapter(Path file) {
        this.reader = new CsvOrderReader(file);
    }

    @Override
    public List<Order> read() {
        List<Order> orders = new ArrayList<>();

        try { // prod. by DeepSeek :D
            // 2. loadLines() throws IOException, so we need a try-catch block
            for (String line : reader.loadLines()) {
                String[] parts = line.split("[|]");

                String dateTime = parts[0].trim();
                String companyName = parts[1].trim();
                int quantity = Integer.parseInt(parts[2].trim());

                // 3. Pass the 5 arguments (0s for queue and discount)
                Order order = new Order(0, companyName, dateTime, quantity, 0);
                orders.add(order);
            }
        } catch (IOException e) {
            // 4. Since OrderSource.read() probably doesn't declare 'throws IOException',
            // we wrap it in a RuntimeException (UncheckedIOException is best here).
            throw new UncheckedIOException("Failed to read CSV file", e);
        }

        return orders;
    }
}