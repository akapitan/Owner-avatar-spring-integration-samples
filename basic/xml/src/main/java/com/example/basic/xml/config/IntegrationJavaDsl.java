package com.example.basic.xml.config;

import com.example.basic.xml.domain.Order;
import com.example.basic.xml.domain.OrderItem;
import com.example.basic.xml.service.ExternalResupply;
import com.example.basic.xml.service.OrderItemTransformer;
import com.example.basic.xml.service.StockChecker;
import com.example.basic.xml.service.WarehouseDispatch;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PriorityChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;

/**
 * Spring Integration configuration using Java DSL
 */
@Configuration(proxyBeanMethods = false)
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

/*    @Bean
    public ChannelMessageStore myInMemoryChannelMessageStore() {
        // SimpleMessageStore is an in-memory, non-persistent store
        // Good for testing or when persistence is not required but grouping is.
        return new PriorityCapableChannelMessageStore() {
        };
    }*/

    @Bean(name = "ordersChannel.input")
    public PriorityChannel ordersChannel(@Qualifier("loggingChannel") MessageChannel loggingChannel) {
        return MessageChannels.priority("ordersChannel.input").wireTap(loggingChannel).getObject();
    }

    /**
     * Main integration flow using Java DSL
     */
    @Bean
    public IntegrationFlow orderProcessingFlow(PriorityChannel ordersChannel) {
        return IntegrationFlow
                .fromSupplier(ordersChannel::receive, e -> e.poller(p ->
                                p.fixedRate(5000)
                                  .maxMessagesPerPoll(2)
                                        /*.transactional()*/))
                .handle(this.orderItemTransformer, "transformToSupplierFormat")
                .split(Order.class, Order::getOrderItems)
                .<OrderItem, Boolean>route(this::isInStock, mapping -> mapping
                        .subFlowMapping(true, sf -> sf.handle(this.warehouseDispatch, "dispatch"))
                        .subFlowMapping(false, sf -> sf.handle(this.externalResupply, "orderResupply")))
                .aggregate()
                .handle(x -> {
                    System.out.println("Order processed: " + x.getPayload());
                    System.out.println("Headers: " + x.getHeaders());
                })
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

    /**
     * Determines if an item is in stock based on the message payload
     */
    private boolean isInStock(OrderItem orderItem) {
        return this.stockChecker.checkStock(orderItem.getIsbn());
    }

}