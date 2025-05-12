package com.example.basic.jms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JmsErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsErrorHandler.class);

    public void handle(Exception e) {
        LOGGER.error("Error while processing JMS message: {}", e.getMessage(), e);
        // Implement custom error handling logic here, e.g., sending to a dead-letter queue
    }
}