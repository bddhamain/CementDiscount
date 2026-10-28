package ru.study.service;

import java.util.List;

public class OrdersProcessor {

    public void process(List<Order> orders) {

        int queue = 0;
        int discountStep = 5;
        int startDiscount = 50;
        int discountMinus; // to keep discount above 0 %, equals to queue * discountStep

        for (Order order : orders) { // now only applies discount
                                    // to add: total order price for company including discount

            order.setQueue(queue);
            discountMinus = queue * discountStep;

            if (discountMinus > startDiscount) {

                order.setDiscount(0);

            }

            else {

                order.setDiscount(startDiscount - discountMinus);

            }

        queue++;


        }

    }

}