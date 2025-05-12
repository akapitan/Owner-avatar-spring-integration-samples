package com.example.basic.jms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for JMS Adapters.
 * Tests the JMS inbound and outbound channel adapters.
 */
@SpringBootTest
@ActiveProfiles("test")
public class JmsAdapterTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsAdapterTests.class);

    @Autowired
    @Qualifier("toJmsChannel")
    private MessageChannel inputChannel;

    @Autowired
    @Qualifier("fromJmsOutput")
    private PollableChannel outputChannel;

    /**
     * Tests sending a message to JMS using the outbound adapter and receiving it
     * back using the inbound adapter.
     */
    @Test
    public void testJmsAdapters() {
        String messageText = "Hello, world! " + System.currentTimeMillis();
        inputChannel.send(new GenericMessage<>(messageText));

        LOGGER.info("Sent message to JMS: {}", messageText);

        Message<?> reply = outputChannel.receive(10000);
        assertThat(reply).isNotNull();
        assertThat(reply.getPayload()).isEqualTo(messageText);

        LOGGER.info("Received message from JMS: {}", reply.getPayload());
    }

    /**
     * Test configuration that adds a QueueChannel to capture messages from the JMS inbound adapter.
     */
    @Configuration
    public static class TestConfig {

        @Bean
        public PollableChannel fromJmsOutput() {
            return new QueueChannel();
        }

        @ServiceActivator(inputChannel = "fromJmsChannel")
        @Bean
        public MessageHandler forwardToOutput() {
            return message -> {
                LOGGER.info("Forwarding message to output channel: {}", message.getPayload());
                fromJmsOutput().send(message);
            };
        }
    }
}