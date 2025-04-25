package com.example.basic.amqp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class AmqpService {

    @Autowired
    @Qualifier("inputChannel")
    private MessageChannel inputChannel;

    /**
     * Sends a message to the AMQP exchange
     *
     * @param payload the message content
     * @return true if the message was sent successfully
     */
    public boolean sendMessage(String payload) {
        Message<String> message = MessageBuilder
                .withPayload(payload)
                .build();

        return inputChannel.send(message);
    }
}