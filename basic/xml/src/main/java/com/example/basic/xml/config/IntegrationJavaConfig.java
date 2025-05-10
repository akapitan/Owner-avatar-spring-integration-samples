package com.example.basic.xml.config;

import com.example.basic.xml.service.ExternalResupply;
import com.example.basic.xml.service.OrderItemTransformer;
import com.example.basic.xml.service.StockChecker;
import com.example.basic.xml.service.WarehouseDispatch;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.integration.xml.splitter.XPathMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.xml.transform.dom.DOMSource;
import java.util.HashMap;
import java.util.Map;

//@Configuration
public class IntegrationJavaConfig {

    @Bean
    public Map<String, String> orderNamespaceMap() {
        Map<String, String> namespaceMap = new HashMap<>();
        namespaceMap.put("orderNs", "http://www.example.org/orders");
        namespaceMap.put("productNs", "https://www.example.org/products");
        return namespaceMap;
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

    @Bean
    public OrderItemTransformer orderItemTransformer() {
        return new OrderItemTransformer();
    }

    @Bean
    public QueueChannel ordersChannel() {
        return new QueueChannel();
    }

    @Bean
    public MessageChannel stockCheckerChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel orderRoutingChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel warehouseDispatchChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel outOfStockChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel resupplyOrderChannel() {
        return new DirectChannel();
    }

    @Bean
    public XPathMessageSplitter orderItemSplitter() {
        // Basic splitter with no namespace configuration for now
        XPathMessageSplitter splitter = new XPathMessageSplitter("/order/orderItem");
        splitter.setCreateDocuments(true);
        return splitter;
    }

    @Bean
    public PollingConsumer splitterEndpoint() {
        AbstractMessageSplitter splitter = orderItemSplitter();
        splitter.setOutputChannel(stockCheckerChannel());
        PollingConsumer consumer = new PollingConsumer(ordersChannel(), splitter);
        consumer.setTrigger(new PeriodicTrigger(1000));
        return consumer;
    }
/*
    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(1000));
        return pollerMetadata;
    }*/

/*
    @ServiceActivator(inputChannel = "stockCheckerChannel")
    public Message<?> checkStock(Message<?> message) {
        Message<?> result = stockChecker().checkStock(message);
        orderRoutingChannel().send(result);
        return result;
    }
*/

    @Router(inputChannel = "orderRoutingChannel")
    public String routeOrder(Message<?> message) {
        try {
            Object payload = message.getPayload();
            org.w3c.dom.Document doc;

            if (payload instanceof DOMSource) {
                doc = (org.w3c.dom.Document) ((DOMSource) payload).getNode();
            } else if (payload instanceof String) {
                javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
                org.xml.sax.InputSource is = new org.xml.sax.InputSource(new java.io.StringReader((String) payload));
                doc = builder.parse(is);
            } else {
                throw new IllegalArgumentException("Unsupported payload type: " + payload.getClass().getName());
            }

            boolean inStock = Boolean.parseBoolean(doc.getDocumentElement().getAttribute("in-stock"));
            return inStock ? "warehouseDispatchChannel" : "outOfStockChannel";
        } catch (Exception e) {
            return "outOfStockChannel";
        }
    }

    @ServiceActivator(inputChannel = "warehouseDispatchChannel")
    public void dispatchOrder(Object payload) {
        warehouseDispatch().dispatch(payload);
    }

    @Transformer(inputChannel = "outOfStockChannel")
    public Message<?> transformOrder(Message<?> message) {
        Message<?> result = orderItemTransformer().transformToSupplierFormat(message);
        resupplyOrderChannel().send(result);
        return result;
    }

    @ServiceActivator(inputChannel = "resupplyOrderChannel")
    public void orderResupply(Object payload) {
        externalResupply().orderResupply(payload);
    }
}