package com.example.basic.jms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates multiple JMS messages with the same correlation ID into a single message.
 * The aggregated message contains all part messages separated by commas.
 */
@Component
public class JmsAggregator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsAggregator.class);

    /**
     * Aggregates a group of messages into a single message.
     * The payloads are combined into a comma-separated string.
     *
     * @param messages Messages to aggregate
     * @return Aggregated message payload
     */
    @Aggregator(inputChannel = "aggregatorInputChannel", outputChannel = "aggregatorOutputChannel")
    public String aggregate(List<Message<?>> messages) {
        LOGGER.info("Aggregating {} messages", messages.size());

        String result = messages.stream()
                .map(message -> message.getPayload().toString())
                .collect(Collectors.joining(", "));

        LOGGER.info("Aggregation result: {}", result);
        return result;
    }

    /**
     * Determines whether a message group is complete and ready for aggregation.
     * In this case, we consider a group complete when:
     * 1. It has at least 2 messages
     * 2. One of the messages contains the word "END" (case insensitive)
     *
     * @param messages Messages in the group
     * @return true if the group is complete, false otherwise
     */
    @ReleaseStrategy
    public boolean canRelease(List<Message<?>> messages) {
        // Release when we have at least 2 messages and one contains "END"
        boolean hasEndMarker = messages.stream()
                .anyMatch(message ->
                        message.getPayload().toString().toUpperCase().contains("END"));

        return messages.size() >= 2 && hasEndMarker;
    }

    /**
     * Extracts the correlation ID from a message.
     * Uses the "correlationId" header if present, otherwise uses a default value.
     *
     * @param message The message
     * @return Correlation ID
     */
    @CorrelationStrategy
    public Object correlateBy(Message<?> message) {
        return message.getHeaders().getOrDefault("correlationId", "DEFAULT_GROUP");
    }
}