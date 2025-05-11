package com.example.basic.xml.service;

import com.example.basic.xml.domain.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WarehouseDispatch {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseDispatch.class);

    public OrderItem dispatch(OrderItem payload) {
        logger.info("Order dispatched to warehouse for processing with payload: " + payload);
        return payload;
    }
}