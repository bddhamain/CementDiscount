package ru.study.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class NoExtOrderSourceAdapter implements OrderSource {

    private static final int EXPECTED_COLUMNS = 3; // For order file validation
    private static final String SEPARATOR = "#"; // In case separator gets changed

    private final NoExtOrderReader reader;
    private final Path file;

    // 1. The constructor to pass the file
    public NoExtOrderSourceAdapter(Path file) {
        this.reader = new NoExtOrderReader(file);
        this.file = file;
    }

    @Override
    public List<Order> read() {
        List<Order> orders = new ArrayList<>();

        // File-level try/catch block
        try {
            List<String> lines = reader.loadLines();

            for (int i = 0; i < lines.size(); i++) {
                int lineNumber = i + 1;
                String line = lines.get(i);

                if (line.isBlank()) {
                    continue;
                }

                // Line-level try/catch block
                try {
                    String[] parts = line.split(SEPARATOR, -1);

                    if (parts.length != EXPECTED_COLUMNS) {
                        throw new IllegalArgumentException(
                                "expected " + EXPECTED_COLUMNS + " fields, got " + parts.length);
                    }

                    LocalDateTime dateTime = LocalDateTime.parse(parts[0].trim());
                    String companyName = parts[1].trim();
                    int quantity = Integer.parseInt(parts[2].trim());

                    if (companyName.isEmpty() || quantity <= 0) {
                        throw new IllegalArgumentException("Empty company or quantity <= 0");
                    }

                    orders.add(new Order(0, companyName, dateTime, quantity, 0, 0));

                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                            "Corrupted line No. " + lineNumber + " in file " + file + "; line: " + line, e);
                }

            }

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + file, e);
        }

        return orders;

    }

}