package com.example.basic.xml.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

@Order(0)
@Configuration(proxyBeanMethods = false)
public class OrderFlowConfiguration {

    /**
     * Logging channel for debugging purposes
     *
     * @return
     */
    @Bean
    public MessageChannel loggingChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public LoggingHandler loggingHandler() {
        LoggingHandler adapter = new LoggingHandler(LoggingHandler.Level.INFO);
        adapter.setLoggerName("ordersChannelLogger");
        adapter.setLogExpressionString("'Incoming message on ordersChannel: ' + payload");
        return adapter;
    }

    @Bean
    public IntegrationFlow loggingFlow(@Qualifier("loggingChannel") MessageChannel loggingChannel, LoggingHandler loggingHandler) {
        return IntegrationFlow.from(loggingChannel)
                .handle(loggingHandler)
                .get();
    }
}
