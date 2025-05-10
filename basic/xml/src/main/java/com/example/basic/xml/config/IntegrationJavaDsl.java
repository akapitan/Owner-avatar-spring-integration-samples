package com.example.basic.xml.config;

import com.example.basic.xml.service.ExternalResupply;
import com.example.basic.xml.service.OrderItemTransformer;
import com.example.basic.xml.service.StockChecker;
import com.example.basic.xml.service.WarehouseDispatch;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.xml.splitter.XPathMessageSplitter;
import org.springframework.messaging.Message;

import javax.xml.transform.dom.DOMSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Integration configuration using Java DSL
 */
@Configuration
public class IntegrationJavaDsl {

    private final OrderItemTransformer orderItemTransformer;

    public IntegrationJavaDsl(OrderItemTransformer orderItemTransformer) {
        this.orderItemTransformer = orderItemTransformer;
    }

    @Bean
    public StockChecker stockChecker() {
        return new StockChecker();
    }

    @Bean
    public WarehouseDispatch warehouseDispatch() {
        return new WarehouseDispatch();
    }

    @Bean
    public ExternalResupply externalResupply() {
        return new ExternalResupply();
    }

/*    @Bean
    public OrderItemTransformer orderItemTransformer() {
        return new OrderItemTransformer();
    }*/

    @Bean
    public QueueChannel ordersChannel() {
        return new QueueChannel();
    }

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
     * Main integration flow using Java DSL
     */
    @Bean
    public IntegrationFlow orderProcessingFlow() {
        return IntegrationFlow
                .from(ordersChannel())
                .split(orderItemSplitter())
                .<String, Boolean>route(this::isInStock,
                        mapping -> mapping
                                .subFlowMapping(true, sf -> sf
                                        .handle(warehouseDispatch(), "dispatch"))
                                .subFlowMapping(false, sf -> sf
                                        .handle(this.orderItemTransformer, "transformToSupplierFormat")
                                        .handle(externalResupply(), "orderResupply")))
                .get();
    }

    /**
     * Polling flow to initiate order processing
     */
    @Bean
    public IntegrationFlow orderPollingFlow() {
        return IntegrationFlow
                .from(ordersChannel())
                .bridge(e -> e.poller(p -> p.fixedRate(1000)))
                .get();
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
            boolean inStock = Boolean.parseBoolean(doc.getDocumentElement().getAttribute("in-stock"));
            return inStock;
        } catch (Exception e) {
            // Consider adding logging here, e.g., logger.error("Failed to check stock", e);
            return false;
        }
    }
}