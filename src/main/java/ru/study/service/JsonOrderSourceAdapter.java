package ru.study.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class JsonOrderSourceAdapter implements OrderSource {

    private final JsonOrderReader reader;

    // 1. The constructor to pass the file
    public JsonOrderSourceAdapter(Path file) {
        this.reader = new JsonOrderReader(file);
    }

    @Override
    public List<Order> read() {
        List<Order> orders = new ArrayList<>();

        try {
            for (String line : reader.loadLines()) {
                // 2. Split by the # character
                String[] parts = line.split("#");

                // 3. Parse the data
                LocalDateTime dateTime = LocalDateTime.parse(parts[0].trim());
                String companyName = parts[1].trim();
                int quantity = Integer.parseInt(parts[2].trim());

                // 4. Create the order
                orders.add(new Order(0, companyName, dateTime, quantity, 0, 0));
            }
        } catch (IOException e) {
            // 5. Handle the checked exception from the reader
            throw new UncheckedIOException("Failed to read JSON file", e);
        }

        return orders;
    }
}