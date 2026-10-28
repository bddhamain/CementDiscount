//package ru.study.service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class JsonOrderSourceAdapter implements OrderSource {
//
//    private final JsonOrderReader reader = new JsonOrderReader();
//
//    @Override
//    public List<Order> read() {
//        List<Order> orders = new ArrayList<>();
//        for (String line : reader.loadLines()) {   // зовём чужой метод
//            String[] parts = line.split(";");
//            int id = Integer.parseInt(parts[0]);
//            String name = parts[1];
//            int price = Integer.parseInt(parts[2]);
//            orders.add(new Order(id, name, price)); // переводим в наш формат
//        }
//        return orders;
//    }
//}
