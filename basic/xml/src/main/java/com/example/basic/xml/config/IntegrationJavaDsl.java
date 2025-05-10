package com.example.basic.xml.config;

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
import org.springframework.integration.xml.splitter.XPathMessageSplitter;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.Map;

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
        return MessageChannels.queue();
    }


    @Bean
    public MessageSource<String> queueChannelMessageSource() {
        return () -> (Message<String>) ordersChannel().getObject().receive();
    }
    /**
     * Main integration flow using Java DSL
     */
    @Bean
    public IntegrationFlow orderProcessingFlow() {
        return IntegrationFlow
                .from(queueChannelMessageSource(), e -> e.poller(p -> p.fixedRate(5000)))
                .split(orderItemSplitter())
                .<String, Boolean>route(this::isInStock, mapping -> mapping
                        .subFlowMapping(true, sf -> sf.handle(this.warehouseDispatch, "dispatch"))
                        .subFlowMapping(false, sf -> sf.handle(this.orderItemTransformer, "transformToSupplierFormat").handle(this.externalResupply, "orderResupply")))
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
    public Map<String, String> orderNamespaceMap() {
        Map<String, String> namespaceMap = new HashMap<>();
        namespaceMap.put("orderNs", "http://www.example.org/orders");
        namespaceMap.put("productNs", "https://www.example.org/products");
        return namespaceMap;
    }

    @Bean
    public XPathMessageSplitter orderItemSplitter() {
        XPathMessageSplitter splitter = new XPathMessageSplitter("/order/orderItem");
        splitter.setCreateDocuments(true);
        return splitter;
    }

    /**
     * Determines if an item is in stock based on the message payload
     */
    private boolean isInStock(String xmlPayload) {
        try {
            org.w3c.dom.Document doc;

            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.xml.sax.InputSource is = new org.xml.sax.InputSource(new java.io.StringReader(xmlPayload));
            doc = builder.parse(is);

            // Check the in-stock attribute
            String isbn = doc.getDocumentElement().getAttribute("isbn");
            return this.stockChecker.checkStock(isbn);
        } catch (Exception e) {
            // Consider adding logging here, e.g., logger.error("Failed to check stock", e);
            return false;
        }
    }
}