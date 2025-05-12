package com.example.basic.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KafkaErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaErrorHandler.class);

    public void handle(Exception e) {
        LOGGER.error("Error while processing Kafka message: {}", e.getMessage(), e);
        // Implement custom error handling logic here, e.g., sending to a dead-letter queue
    }
}
