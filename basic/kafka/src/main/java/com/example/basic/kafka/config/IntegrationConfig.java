package com.example.basic.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import com.example.basic.kafka.service.KafkaErrorHandler;
import com.example.basic.kafka.service.MessageEchoService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Spring Integration configuration for Kafka using Java DSL with IntegrationFlows.
 */
@Configuration
public class IntegrationConfig {

    @Value("${kafka.topic:kafka-topic}")
    private String defaultTopic;

    @Value("${kafka.group:kafka-group}")
    private String defaultGroupId;

    @Value("${kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // Channels (some can be implicitly created by IntegrationFlows, but explicit definition can be useful)
//    @Bean
//    public MessageChannel errorChannel() {
//        return MessageChannels.direct().getObject(); // Example: using Integration DSL for channels too
//    }

    @Bean
    public MessageChannel loggingChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

//    @Bean
//    public MessageChannel toKafkaChannel() {
//        return MessageChannels.direct().getObject();
//    }

    @Bean
    public MessageChannel fromKafkaChannel() {
        return MessageChannels.direct().getObject();
    }

    // Producer Configuration
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Add any other producer properties here
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // Consumer Configuration
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.defaultGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // Integration Flows using Java DSL

    @Bean
    public IntegrationFlow toKafkaFlow(KafkaTemplate<String, String> kafkaTemplate) {
        return IntegrationFlow.from("toKafkaChannel")// Input channel name
                .log()
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate)
                        .topicExpression("headers[T(org.springframework.kafka.support.KafkaHeaders).TOPIC] ?: '" + this.defaultTopic + "'")
                        .sync(true)
//                        .sendFailureChannel(errorChannel())
                        .sendSuccessChannel(loggingChannel()))
                .get();
    }

    @Bean
    public IntegrationFlow fromKafkaFlow(ConsumerFactory<String, String> consumerFactory, MessageEchoService messageEchoService) {
        ContainerProperties containerProperties = new ContainerProperties(this.defaultTopic);
        containerProperties.setGroupId(this.defaultGroupId);

        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(
                                new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties),
                                KafkaMessageDrivenChannelAdapter.ListenerMode.record)
//                                .errorChannel(errorChannel())
                )
                .handle(messageEchoService, "echo") // Assumes MessageEchoService has an "echo" method
                .get();
    }

    @Bean
    public IntegrationFlow loggingFlow() {
        return IntegrationFlow.from(loggingChannel())
                .log()
                .handle(new LoggingHandler(LoggingHandler.Level.INFO.name())) // INFO level logger
                .get();
    }

    @Bean
    public IntegrationFlow errorHandlingFlow(KafkaErrorHandler kafkaErrorHandler) {
        return IntegrationFlow.from("errorChannel")
                .handle(kafkaErrorHandler, "handle") // Assumes KafkaErrorHandler has a "handle" method
                .get();
    }

    // Service Beans (if not already component-scanned)
    @Bean
    public KafkaErrorHandler customKafkaErrorHandler() {
        return new KafkaErrorHandler();
    }

    @Bean
    @ServiceActivator(inputChannel = "fromKafkaChannel")
    public MessageHandler messageEchoActivator(MessageEchoService messageEchoService) {
        return messageEchoService::echo;
    }

    @Bean
    public MessageEchoService messageEchoService() {
        return new MessageEchoService();
    }
}