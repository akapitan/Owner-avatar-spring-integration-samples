package com.example.basic.xml.service;

import com.example.basic.xml.domain.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExternalResupply {
    private static final Logger logger = LoggerFactory.getLogger(ExternalResupply.class);

    public OrderItem orderResupply(OrderItem payload) {
        logger.info("Resupply order sent to external supplier with payload: " + payload);
        return payload;
    }
}