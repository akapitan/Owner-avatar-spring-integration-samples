package com.example.basic;

import com.example.basic.model.Order;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface RestaurantGateway {

    @Gateway(requestChannel = "orders.input")
    void placeOrder(Order order);
}
