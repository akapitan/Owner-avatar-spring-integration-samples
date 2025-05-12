package com.example.basic.jms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.basic.jms.gateway.JmsGateway;

/**
 * Integration tests for JMS Gateways.
 * Tests the request-reply pattern using JMS gateways.
 */
@SpringBootTest
@ActiveProfiles("test")
public class JmsGatewayTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsGatewayTests.class);

    @Autowired
    private JmsGateway jmsGateway;

    /**
     * Tests the request-reply pattern using JMS gateways.
     * Sends a message and expects a transformed (uppercase) reply.
     */
    @Test
    public void testRequestReply() {
        String request = "test-request-" + System.currentTimeMillis();
        LOGGER.info("Sending request: {}", request);

        String reply = jmsGateway.sendAndReceive(request);

        LOGGER.info("Received reply: {}", reply);

        assertNotNull(reply);
        assertThat(reply).isEqualTo(request.toUpperCase());
    }
}