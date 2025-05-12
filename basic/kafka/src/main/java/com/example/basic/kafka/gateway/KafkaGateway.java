// {{company/project header}}
package com.example.basic.kafka.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * Gateway interface for sending messages to Kafka.
 * Messages sent through this gateway will be directed to the "toKafkaChannel".
 */
@MessagingGateway(defaultRequestChannel = "toKafkaChannel")
public interface KafkaGateway {

    /**
     * Sends a message to the default Kafka topic configured in the toKafkaFlow.
     *
     * @param payload The message payload to send.
     */
    void sendToKafka(String payload);

    /**
     * Sends a message to a specific Kafka topic.
     *
     * @param payload The message payload to send.
     * @param topic   The Kafka topic to send the message to. This will be set as a header.
     */
    @Gateway(requestChannel = "toKafkaChannel")
    // Explicitly state channel, though defaultRequestChannel also applies
    void sendToKafka(String payload, @Header(KafkaHeaders.TOPIC) String topic);

}
