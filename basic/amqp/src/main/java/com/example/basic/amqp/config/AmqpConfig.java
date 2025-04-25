package com.example.basic.amqp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

@Configuration
public class AmqpConfig {

    private static final String EXCHANGE_NAME = "si.test.exchange";
    private static final String QUEUE_NAME = "si.test.queue";
    private static final String ROUTING_KEY = "si.test.binding";
    private static final String CONFIRMS_CHANNEL = "confirmsChannel";
    private static final String RETURNS_CHANNEL = "returnsChannel";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }

    @Bean
    public AmqpTemplate amqpTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange(EXCHANGE_NAME);
        rabbitTemplate.setRoutingKey(ROUTING_KEY);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    // Define the channels for confirms and returns
    @Bean
    public MessageChannel confirmsChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel returnsChannel() {
        return MessageChannels.direct().getObject();
    }

    // Input channel where messages will be sent
    @Bean
    public MessageChannel inputChannel() {
        return MessageChannels.direct().getObject();
    }

    // Output channel where AMQP received messages will be sent
    @Bean
    public MessageChannel outputChannel() {
        return MessageChannels.direct().getObject();
    }

    // Flow for publishing messages to AMQP
    @Bean
    public IntegrationFlow amqpOutboundFlow() {
        return IntegrationFlow.from(inputChannel())
                .handle(Amqp.outboundAdapter(amqpTemplate())
                        .exchangeName(EXCHANGE_NAME)
                        .routingKey(ROUTING_KEY)
                        .confirmCorrelationExpression("payload")
                        .confirmAckChannel(confirmsChannel())
                        .confirmNackChannel(confirmsChannel())
                        .returnChannel(returnsChannel()))
                .get();
    }

    // Flow for handling publisher confirms
    @Bean
    public IntegrationFlow confirmsFlow() {
        return IntegrationFlow.from(CONFIRMS_CHANNEL)
                .handle(message -> System.out.println("Confirm received: " + message.getPayload()))
                .get();
    }

    // Flow for handling publisher returns
    @Bean
    public IntegrationFlow returnsFlow() {
        return IntegrationFlow.from(RETURNS_CHANNEL)
                .log(LoggingHandler.Level.INFO, "returnsFlow")
                .handle(message -> System.out.println("Return received: " + message.getPayload()))
                .get();
    }

    // Flow for consuming messages from AMQP
    @Bean
    public IntegrationFlow amqpInboundFlow() {
        return IntegrationFlow
                .from(Amqp.inboundAdapter(connectionFactory(), QUEUE_NAME))
                .log(LoggingHandler.Level.INFO, "amqpInboundFlow")
                .channel(outputChannel())
                .get();
    }

    // Message handler for processing the received messages
    @Bean
    public IntegrationFlow processReceivedMessages() {
        return IntegrationFlow.from(outputChannel())
                .log(LoggingHandler.Level.INFO, "Received message")
                .handle(message -> System.out.println("Received message: " + message.getPayload()))
                .get();
    }
}