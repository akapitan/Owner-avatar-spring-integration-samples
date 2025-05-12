package com.example.basic.jms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.UUID;
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
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for JMS Aggregator.
 * Tests the aggregation of multiple messages with the same correlation ID.
 */
@SpringBootTest
@ActiveProfiles("test")
public class JmsAggregatorTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsAggregatorTests.class);

    @Autowired
    @Qualifier("aggregatorInputChannel")
    private MessageChannel inputChannel;

    @Autowired
    @Qualifier("aggregatedOutputChannel")
    private PollableChannel outputChannel;

    /**
     * Tests the message aggregator by sending multiple messages with the same correlation ID.
     * One of the messages contains "END" to trigger the aggregation, and the result should
     * be a single message with all parts combined.
     */
    @Test
    public void testAggregator() {
        // Generate a correlation ID for this test
        String correlationId = UUID.randomUUID().toString();

        // Send three messages with the same correlation ID
        String message1 = "part1";
        String message2 = "part2";
        String message3 = "END part3";

        LOGGER.info("Sending message 1 with correlation ID {}: {}", correlationId, message1);
        inputChannel.send(MessageBuilder.withPayload(message1)
                .setHeader("correlationId", correlationId)
                .build());

        LOGGER.info("Sending message 2 with correlation ID {}: {}", correlationId, message2);
        inputChannel.send(MessageBuilder.withPayload(message2)
                .setHeader("correlationId", correlationId)
                .build());

        LOGGER.info("Sending message 3 with correlation ID {}: {}", correlationId, message3);
        inputChannel.send(MessageBuilder.withPayload(message3)
                .setHeader("correlationId", correlationId)
                .build());

        // Wait for the aggregated result
        Message<?> result = outputChannel.receive(10000);
        assertThat(result).isNotNull();

        String payload = (String) result.getPayload();
        LOGGER.info("Received aggregated result: {}", payload);

        // Verify the aggregation result contains all parts
        assertThat(payload).contains("part1");
        assertThat(payload).contains("part2");
        assertThat(payload).contains("part3");
    }

    /**
     * Test configuration for capturing aggregated output.
     */
    @Configuration
    public static class TestConfig {

        @Bean
        public PollableChannel aggregatedOutputChannel() {
            return new QueueChannel();
        }

        @ServiceActivator(inputChannel = "aggregatorOutputChannel")
        @Bean
        public MessageHandler forwardAggregatedMessages() {
            return message -> {
                LOGGER.info("Forwarding aggregated message to output channel: {}", message.getPayload());
                aggregatedOutputChannel().send(message);
            };
        }
    }
}