package ru.study;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import ru.study.service.*;
import ru.study.service.Order;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Produced by deepseek
class TwoSourcesIntegrationTest {

    @TempDir
    Path tempDir;

    // ---------- helpers ----------

    private static void assertBD(String expected, BigDecimal actual) {
        assertEquals(0, actual.compareTo(new BigDecimal(expected)),
                () -> "expected " + expected + " but was " + actual);
    }

    private Path writeNoExt(String... lines) throws IOException {
        Path file = tempDir.resolve("orders_noext.txt");
        Files.write(file, List.of(lines));
        return file;
    }

    private Path writeCsv(String... lines) throws IOException {
        Path file = tempDir.resolve("orders.csv");
        Files.write(file, List.of(lines));
        return file;
    }

    // ---------- test 1: read + combine ----------

    @Test
    void readsAndCombinesTwoFiles() throws IOException {
        Path noExt = writeNoExt(
                "2024-01-01T10:00#Acme#1",
                "2024-01-02T10:00#Beta#2"
        );
        Path csv = writeCsv(
                "2024-01-03T10:00|Gamma|3",
                "2024-01-04T10:00|Acme|4"
        );

        // как в main
        OrderSource sourceNoExt = new NoExtOrderSourceAdapter(noExt);
        OrderSource sourceCsv   = new CsvOrderSourceAdapter(csv);

        List<Order> ordersNoExt = sourceNoExt.read();
        List<Order> ordersCsv   = sourceCsv.read();

        assertEquals(2, ordersNoExt.size());
        assertEquals(2, ordersCsv.size());

        List<Order> orders = new ArrayList<>(ordersNoExt);
        orders.addAll(ordersCsv);

        assertEquals(4, orders.size());

        // проверяем не только размер, но и содержимое
        Order first = orders.get(0);
        assertEquals("Acme", first.getCompanyName());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), first.getOrderTime());
        assertBD("1", first.getQuantity());

        Order last = orders.get(3);
        assertEquals("Acme", last.getCompanyName());
        assertEquals(LocalDateTime.of(2024, 1, 4, 10, 0), last.getOrderTime());
        assertBD("4", last.getQuantity());
    }

    // ---------- test 2: full pipeline (same as main) ----------

    @Test
    void fullPipelineOnTwoFiles() throws IOException {
        Path noExt = writeNoExt(
                "2024-01-03T10:00#Acme#1",   // 3-й по времени
                "2024-01-01T10:00#Beta#2"    // 1-й по времени
        );
        Path csv = writeCsv(
                "2024-01-02T10:00|Acme|3",   // 2-й по времени
                "2024-01-04T10:00|Gamma|4"   // 4-й по времени
        );

        // как в main
        List<Order> orders = new ArrayList<>();
        orders.addAll(new NoExtOrderSourceAdapter(noExt).read());
        orders.addAll(new CsvOrderSourceAdapter(csv).read());

        OrdersProcessor processor = new OrdersProcessor(
                BigDecimal.valueOf(100), 50, 5);

        List<Order> sortedOrders = processor.process(orders);
        processor.applyDiscounts(sortedOrders);
        Map<String, BigDecimal> result = processor.mapTheOrders(sortedOrders);

        // после сортировки по времени:
        //   queue 0: Beta   qty 2, 50% -> 2*100*0.50 = 100
        //   queue 1: Acme   qty 3, 45% -> 3*100*0.55 = 165
        //   queue 2: Acme   qty 1, 40% -> 1*100*0.60 =  60
        //   queue 3: Gamma  qty 4, 35% -> 4*100*0.65 = 260
        assertBD("100", result.get("Beta"));
        assertBD("225", result.get("Acme"));    // 165 + 60
        assertBD("260", result.get("Gamma"));
    }

    // ---------- test 3: broken line reports file + line number ----------

    @Test
    void brokenCsvLineReportsFileAndLineNumber() throws IOException {
        Path bad = writeCsv(
                "2024-01-01T10:00|Acme|1",
                "2024-01-02T10:00|Acme"      // не хватает поля
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CsvOrderSourceAdapter(bad).read());

        String msg = ex.getMessage();
        assertTrue(msg.contains(bad.toString()), "message should name the file: " + msg);
        assertTrue(msg.contains("line 2"),       "message should name the line: " + msg);
    }
}
