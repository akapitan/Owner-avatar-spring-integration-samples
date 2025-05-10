package com.example.basic.xml.config;

import com.example.basic.xml.domain.Order;
import com.example.basic.xml.domain.OrderItem;
import com.example.basic.xml.service.ExternalResupply;
import com.example.basic.xml.service.OrderItemTransformer;
import com.example.basic.xml.service.StockChecker;
import com.example.basic.xml.service.WarehouseDispatch;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.QueueChannelSpec;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.xml.splitter.XPathMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

/**
 * Spring Integration configuration using Java DSL
 */
@Configuration
public class IntegrationJavaDsl {

    private final OrderItemTransformer orderItemTransformer;
    private final ExternalResupply externalResupply;
    private final WarehouseDispatch warehouseDispatch;
    private final StockChecker stockChecker;

    public IntegrationJavaDsl(OrderItemTransformer orderItemTransformer, ExternalResupply externalResupply, WarehouseDispatch warehouseDispatch, StockChecker stockChecker) {
        this.orderItemTransformer = orderItemTransformer;
        this.externalResupply = externalResupply;
        this.warehouseDispatch = warehouseDispatch;
        this.stockChecker = stockChecker;
    }

    @Bean
    public QueueChannelSpec ordersChannel() {
        return MessageChannels.queue().wireTap("loggingChannel");
    }


    @Bean
    public MessageSource queueChannelMessageSource() {
        return () -> ordersChannel().getObject().receive();
    }



    /**
     * Main integration flow using Java DSL
     */
    @Bean
    public IntegrationFlow orderProcessingFlow() {
        return IntegrationFlow
                .from(queueChannelMessageSource(), e -> e.poller(p -> p.fixedRate(1000)))
                .handle(this.orderItemTransformer, "transformToSupplierFormat")
                .split(Order.class, Order::getOrderItems)
                .<OrderItem, Boolean>route(this::isInStock, mapping -> mapping
                        .subFlowMapping(true, sf -> sf.handle(this.warehouseDispatch, "dispatch"))
                        .subFlowMapping(false, sf -> sf.handle(this.externalResupply, "orderResupply").handle(this.externalResupply, "orderResupply")))
                .get();
    }

    /*
    @Bean
    public IntegrationFlow orderPollingFlow() {
        return IntegrationFlow
                .from(ordersChannel())
                .bridge(e -> e.poller(p -> p.fixedRate(1000)))
                .get();
    }*/

    @Bean
    public XPathMessageSplitter orderItemSplitter() {
        XPathMessageSplitter splitter = new XPathMessageSplitter("/order/orderItem");
        splitter.setCreateDocuments(true);
        return splitter;
    }

    /**
     * Determines if an item is in stock based on the message payload
     */
    private boolean isInStock(OrderItem orderItem) {
        return this.stockChecker.checkStock(orderItem.getIsbn());
    }


    /**
     * Logging channel for debugging purposes
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
    public IntegrationFlow loggingFlow(LoggingHandler loggingHandler) {
        return IntegrationFlow.from("loggingChannel")
                .handle(loggingHandler)
                .get();
    }
}