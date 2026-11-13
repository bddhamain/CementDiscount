package ru.study.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvOrderSourceAdapter implements OrderSource {

    private static final int EXPECTED_COLUMNS = 3;
    private static final String SEPARATOR = "[|]";

    private final CsvOrderReader reader;
    private final Path file;

    public CsvOrderSourceAdapter(Path file) {
        this.reader = new CsvOrderReader(file);
        this.file = file;
    }

    @Override
    public List<Order> read() {
        List<Order> orders = new ArrayList<>();

        try { // File-level try/catch block
            List<String> lines = reader.loadLines();

            for (int i = 0; i < lines.size(); i++) {
                int lineNumber = i + 1;
                String line = lines.get(i);

                if (line.isBlank()) {
                    continue;
                }

                try { // Line-level try/catch block
                    String[] parts = line.split(SEPARATOR, -1);

                    if (parts.length != EXPECTED_COLUMNS) {
                        throw new IllegalArgumentException(
                                "expected " + EXPECTED_COLUMNS + " columns, got " + parts.length);
                    }

                    LocalDateTime dateTime = LocalDateTime.parse(parts[0].trim());
                    String companyName     = parts[1].trim();
                    int quantity           = Integer.parseInt(parts[2].trim());

                    if (companyName.isEmpty() || quantity <= 0) {
                        throw new IllegalArgumentException("empty company or quantity <= 0");
                    }

                    orders.add(new Order(0, companyName, dateTime, quantity, 0, 0));

                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                            "Corrupted line " + lineNumber + " in file " + file + "; line: "+ line, e);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + file, e);
        }

        return orders;
    }
}