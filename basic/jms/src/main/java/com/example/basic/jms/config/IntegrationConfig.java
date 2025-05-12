package com.example.basic.jms.config;

import com.example.basic.jms.service.JmsAggregator;
import com.example.basic.jms.service.JmsErrorHandler;
import com.example.basic.jms.service.MessageEchoService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.integration.store.MessageStore;

import jakarta.jms.ConnectionFactory;

import java.util.stream.Collectors;

/**
 * Spring Integration configuration for JMS using Java DSL with IntegrationFlows.
 */
@Configuration
public class IntegrationConfig {

    @Value("${jms.queue:jms-queue}")
    private String defaultQueue;

    @Value("${jms.topic:jms-topic}")
    private String defaultTopic;

    // Channels (some can be implicitly created by IntegrationFlows, but explicit definition can be useful)
    @Bean
    public MessageChannel errorChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel loggingChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public MessageChannel toJmsChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel fromJmsChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel aggregatorInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel aggregatorOutputChannel() {
        return new PublishSubscribeChannel();
    }

    // Integration Flows
    @Bean
    public IntegrationFlow toJmsFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow.from("toJmsChannel")
                .log()
                .handle(Jms.outboundAdapter(connectionFactory)
                        .destinationExpression("headers['jms_destination'] ?: '" + this.defaultQueue + "'"))
                .get();
    }

    @Bean
    public IntegrationFlow fromJmsPollingFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Jms.inboundAdapter(connectionFactory)
                        .destination(this.defaultQueue)
                        .configureJmsTemplate(template -> template.receiveTimeout(1000)))
                .channel(fromJmsChannel())
                .get();
    }

    @Bean
    public IntegrationFlow fromJmsListeningFlow(ConnectionFactory connectionFactory, MessageEchoService messageEchoService) {
        return IntegrationFlow
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination(this.defaultQueue)
                        .configureListenerContainer(container ->
                                container.sessionTransacted(true)
                                        .concurrentConsumers(1)
                                        .maxConcurrentConsumers(5))
                        .errorChannel("errorChannel"))
                .handle(messageEchoService, "echo")
                .get();
    }

    @Bean
    public IntegrationFlow jmsTopicFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination(this.defaultTopic))
                .channel("loggingChannel")
                .get();
    }

    @Bean
    public IntegrationFlow loggingFlow() {
        return IntegrationFlow.from("loggingChannel")
                .log()
                .handle(new LoggingHandler(LoggingHandler.Level.INFO.name()))
                .get();
    }

    @Bean
    public IntegrationFlow errorHandlingFlow(JmsErrorHandler jmsErrorHandler) {
        return IntegrationFlow.from("errorChannel")
                .handle(jmsErrorHandler, "handle")
                .get();
    }

    @Bean
    public IntegrationFlow outboundJmsGatewayFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow.from("outboundGatewayChannel")
                .handle(Jms.outboundGateway(connectionFactory)
                        .requestDestination(this.defaultQueue + ".request"))
                .get();
    }

    @Bean
    public IntegrationFlow inboundJmsGatewayFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow.from(Jms.inboundGateway(connectionFactory)
                        .requestDestination(this.defaultQueue + ".request"))
                .transform(String.class, String::toUpperCase) // Simple transformation as an example
                .get();
    }

    // Aggregation
    @Bean
    public IntegrationFlow aggregationFlow(JmsAggregator aggregator) {
        return IntegrationFlow.from("aggregatorInputChannel")
                .handle(aggregator, "aggregate")
                .channel("aggregatorOutputChannel")
                .get();
    }

    @Bean
    public IntegrationFlow processAggregatedMessages() {
        return IntegrationFlow.from("aggregatorOutputChannel")
                .handle(message -> {
                    System.out.println("=== AGGREGATION RESULT ===");
                    System.out.println(message.getPayload());
                    System.out.println("=========================");
                })
                .get();
    }

    // Service Beans
    @Bean
    public JmsErrorHandler customJmsErrorHandler() {
        return new JmsErrorHandler();
    }

    @Bean
    @ServiceActivator(inputChannel = "fromJmsChannel")
    public MessageHandler messageEchoActivator(MessageEchoService messageEchoService) {
        return messageEchoService::echo;
    }

    @Bean
    public MessageEchoService messageEchoService() {
        return new MessageEchoService();
    }

    @Bean
    public JmsAggregator jmsAggregator() {
        return new JmsAggregator();
    }
}