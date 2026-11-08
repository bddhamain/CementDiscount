package ru.study;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ru.study.service.Order;
import ru.study.service.OrdersProcessor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrdersProcessorTest {

    private OrdersProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new OrdersProcessor();
    }

    /** Build an Order via the full constructor (queue/discount/price filled later). */
    private static Order order(String company, int qty, int day) {
        return new Order(
                0,
                company,
                LocalDateTime.of(2024, 1, day, 0, 0),
                qty,
                0,
                0
        );
    }

    // ==================================================================
    // process(): sorts by orderTime, earliest first
    // ==================================================================

    @Test
    @DisplayName("process sorts orders by time, earliest first")
    void processSortsByTime() {
        Order late  = order("A", 1, 3);
        Order early = order("B", 1, 1);
        Order mid   = order("C", 1, 2);

        List<Order> result = processor.process(List.of(late, early, mid));

        assertEquals(List.of(early, mid, late), result);
    }

    @Test
    @DisplayName("process on empty list returns empty list")
    void processEmpty() {
        assertTrue(processor.process(List.of()).isEmpty());
    }

    // ==================================================================
    // applyDiscounts(): queue index + discount per position
    // ==================================================================

    @ParameterizedTest(name = "queue {0} -> discount {1}")
    @CsvSource({
            "0, 50",
            "1, 45",
            "2, 40",
            "5, 25",
            "9,  5",
            "10, 0",   // 10*5 = 50, not > 50 -> 0
            "11, 0",   // 55 > 50 -> guard -> 0
            "20, 0"
    })
    @DisplayName("applyDiscounts assigns expected discount per queue position")
    void discountPerPosition(int queue, int expected) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i <= queue; i++) orders.add(order("C" + i, 1, 1));

        processor.applyDiscounts(orders);

        assertEquals(expected, orders.get(queue).getDiscount());
    }

    @Test
    @DisplayName("applyDiscounts sets queue = list index")
    void applyDiscountsSetsQueue() {
        List<Order> orders = new ArrayList<>(List.of(
                order("A", 1, 1),
                order("B", 1, 1),
                order("C", 1, 1)));

        processor.applyDiscounts(orders);

        for (int i = 0; i < orders.size(); i++) {
            assertEquals(i, orders.get(i).getQueue());
        }
    }

    // ==================================================================
    // Price: now really applies the discount
    // ==================================================================

    @ParameterizedTest(name = "queue {0}, qty {1} -> price {2}")
    @CsvSource({
            // queue, qty, expected price
            "0, 1,  50",   // 100 * 0.50
            "0, 5, 250",   // 500 * 0.50
            "1, 1,  55",   // 100 * 0.55
            "1, 3, 165",   // 300 * 0.55
            "2, 4, 240",   // 400 * 0.60
            "5, 2, 150",   // 200 * 0.75
            "9, 2, 190",   // 200 * 0.95
            "10, 2, 200",  // 200 * 1.00 (no discount)
            "20, 2, 200"   // still 200
    })
    @DisplayName("price = qty * 100 * (1 - discount/100)")
    void priceAppliesDiscount(int queue, int qty, int expectedPrice) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i <= queue; i++) orders.add(order("C" + i, qty, 1));

        processor.applyDiscounts(orders);

        assertEquals(expectedPrice, orders.get(queue).getOrderPrice());
    }

    // ==================================================================
    // mapTheOrders(): group by company, sum prices
    // ==================================================================

    @Test
    @DisplayName("mapTheOrders groups by company and sums prices")
    void mapGroupsAndSums() {
        List<Order> orders = new ArrayList<>(List.of(
                order("Acme", 1, 1),   // queue 0, 50% off ->  50
                order("Acme", 2, 2),   // queue 1, 45% off -> 110
                order("Beta", 3, 3))); // queue 2, 40% off -> 180

        processor.applyDiscounts(orders);
        Map<String, Integer> map = processor.mapTheOrders(orders);

        assertEquals(2, map.size());
        assertEquals(160, map.get("Acme"));   // 50 + 110
        assertEquals(180, map.get("Beta"));
    }

    @Test
    @DisplayName("mapTheOrders on empty list returns empty map")
    void mapEmpty() {
        assertTrue(processor.mapTheOrders(List.of()).isEmpty());
    }

    // ==================================================================
    // Full pipeline: sort -> applyDiscounts -> map
    // ==================================================================

    @Test
    @DisplayName("end-to-end: sort, discount, aggregate")
    void endToEnd() {
        Order acmeLate  = order("Acme", 1, 3);   // sorted last   -> queue 2, 40% off
        Order betaEarly = order("Beta", 2, 1);   // sorted first  -> queue 0, 50% off
        Order acmeMid   = order("Acme", 3, 2);   // sorted middle -> queue 1, 45% off

        List<Order> sorted = processor.process(
                new ArrayList<>(List.of(acmeLate, betaEarly, acmeMid)));
        processor.applyDiscounts(sorted);
        Map<String, Integer> map = processor.mapTheOrders(sorted);

        // Queue positions follow the sorted order.
        assertEquals(0, betaEarly.getQueue());
        assertEquals(1, acmeMid.getQueue());
        assertEquals(2, acmeLate.getQueue());

        // Prices:
        //   betaEarly: 2*100*0.50 = 100
        //   acmeMid  : 3*100*0.55 = 165
        //   acmeLate : 1*100*0.60 =  60
        assertEquals(100, betaEarly.getOrderPrice());
        assertEquals(165, acmeMid.getOrderPrice());
        assertEquals(60,  acmeLate.getOrderPrice());

        assertEquals(225, map.get("Acme"));   // 165 + 60
        assertEquals(100, map.get("Beta"));
    }
}