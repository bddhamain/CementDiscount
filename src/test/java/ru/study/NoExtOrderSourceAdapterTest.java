package ru.study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import ru.study.service.NoExtOrderSourceAdapter;
import ru.study.service.Order;

import static org.junit.jupiter.api.Assertions.*;
import static ru.study.OrdersProcessorTest.assertBD;

class NoExtOrderSourceAdapterTest {

    @TempDir
    Path tempDir;

    private Path writeFile(String content) throws IOException {
        Path file = tempDir.resolve("orders.txt");
        Files.writeString(file, content);
        return file;
    }

    @Test
    @DisplayName("read parses each line into an Order, in file order")
    void readsAllLines() throws IOException {
        Path file = writeFile(
                "2024-01-01T10:00#Acme#5\n" +
                        "2024-01-02T10:00#Beta#7\n" +
                        "2024-01-03T10:00#Acme#2\n");

        List<Order> orders = new NoExtOrderSourceAdapter(file).read();

        assertEquals(3, orders.size());
        assertEquals("Acme", orders.get(0).getCompanyName());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), orders.get(0).getOrderTime());
        assertBD(5, orders.get(0).getQuantity());
        assertEquals("Beta", orders.get(1).getCompanyName());
        assertBD(7, orders.get(1).getQuantity());
        assertEquals("Acme", orders.get(2).getCompanyName());
        assertBD(2, orders.get(2).getQuantity());
    }

    @Test
    @DisplayName("read sets queue, discount and price to 0")
    void initialValuesAreZero() throws IOException {
        Path file = writeFile("2024-01-01T10:00#Acme#5\n");

        Order o = new NoExtOrderSourceAdapter(file).read().get(0);

        assertBD(0, o.getQueue());
        assertBD(0, o.getDiscount());
        assertBD(0, o.getOrderPrice());
    }

    @Test
    @DisplayName("read trims whitespace around each field")
    void trimsWhitespace() throws IOException {
        Path file = writeFile("  2024-01-01T10:00  #  Acme  #  5  \n");

        Order o = new NoExtOrderSourceAdapter(file).read().get(0);

        assertEquals("Acme", o.getCompanyName());
        assertBD(5, o.getQuantity());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), o.getOrderTime());
    }

    @Test
    @DisplayName("read on an empty file returns an empty list")
    void emptyFile() throws IOException {
        Path file = writeFile("");

        assertTrue(new NoExtOrderSourceAdapter(file).read().isEmpty());
    }

    @Test
    @DisplayName("invalid date propagates IllegalArgumentException")
    void invalidDate() throws IOException {
        Path file = writeFile("not-a-date#Acme#5\n");

        assertThrows(IllegalArgumentException.class,
                () -> new NoExtOrderSourceAdapter(file).read());
    }

    @Test
    @DisplayName("invalid quantity propagates IllegalArgumentException")
    void invalidQuantity() throws IOException {
        Path file = writeFile("2024-01-01T10:00#Acme#five\n");

        assertThrows(IllegalArgumentException.class,
                () -> new NoExtOrderSourceAdapter(file).read());
    }

    @Test
    @DisplayName("missing fields throws IllegalArgumentException")
    void missingFields() throws IOException {
        Path file = writeFile("2024-01-01T10:00#Acme\n");

        assertThrows(IllegalArgumentException.class,
                () -> new NoExtOrderSourceAdapter(file).read());
    }

    @Test
    @DisplayName("missing file is wrapped in UncheckedIOException")
    void missingFile() {
        Path missing = tempDir.resolve("does-not-exist.txt");

        UncheckedIOException ex = assertThrows(UncheckedIOException.class,
                () -> new NoExtOrderSourceAdapter(missing).read());

        assertNotNull(ex.getCause());
        assertInstanceOf(IOException.class, ex.getCause());
    }
}