
package com.example.basic.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageEchoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEchoService.class);

    public void echo(Message<?> message) {
        LOGGER.info("Received message: {}", message.getPayload());
        // Implement actual message processing logic here
    }
}
