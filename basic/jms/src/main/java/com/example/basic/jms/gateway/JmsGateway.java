package com.example.basic.jms.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;

/**
 * Gateway interface for sending messages to JMS.
 * Messages sent through this gateway will be directed to the "toJmsChannel".
 */
@MessagingGateway(defaultRequestChannel = "toJmsChannel")
public interface JmsGateway {

    /**
     * Sends a message to the default JMS queue configured in the toJmsFlow.
     *
     * @param payload The message payload to send.
     */
    void sendToJms(String payload);

    /**
     * Sends a message to a specific JMS destination.
     *
     * @param payload     The message payload to send.
     * @param destination The JMS destination to send the message to.
     */
    @Gateway(requestChannel = "toJmsChannel")
    void sendToJms(String payload, @Header("jms_destination") String destination);

    /**
     * Sends a message to JMS and waits for a reply.
     * This demonstrates the request-reply pattern using JMS.
     *
     * @param payload The request message to send.
     * @return The response received from the JMS reply destination.
     */
    @Gateway(requestChannel = "outboundGatewayChannel", replyTimeout = 5000)
    String sendAndReceive(String payload);
}