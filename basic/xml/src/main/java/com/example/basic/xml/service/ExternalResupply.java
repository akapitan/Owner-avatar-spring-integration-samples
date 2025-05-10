package com.example.basic.xml.service;

import com.example.basic.xml.domain.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;

@Component
public class ExternalResupply {
    private static final Logger logger = LoggerFactory.getLogger(ExternalResupply.class);

    public void orderResupply(OrderItem payload) {
        try {
            if (payload instanceof Source) {
                logger.info("Resupply order sent to external supplier (Source)");
            } else {
                logger.info("Resupply order sent to external supplier (Unknown type): " + payload.getClass().getName());
            }
        } catch (Exception e) {
            logger.error("Error sending resupply order", e);
        }
    }
}