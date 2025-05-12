package com.example.basic.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;
import java.nio.file.Files;

@SpringBootApplication
public class XmlIntegrationApplication {
    private static final Logger logger = LoggerFactory.getLogger(XmlIntegrationApplication.class);

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(XmlIntegrationApplication.class, args);
        OrdersProcessingGateway ordersProcessingGateway = ctx.getBean(OrdersProcessingGateway.class);

        // Read sample order XML
        String orderXml = readSampleXml();
        // Get the order channel and send the order
        for (int i = 0; i < 10; i++) {
            ordersProcessingGateway.createOrder(orderXml, i);
            logger.info("Order sent to channel with priority: {}", i);
        }
    }

    @MessagingGateway()
    public interface OrdersProcessingGateway {

        @Gateway(requestChannel = "ordersChannel.input")
        void createOrder(String xmlString, @Header(IntegrationMessageHeaderAccessor.PRIORITY) int priority);

    }

    private static String readSampleXml() throws IOException {
        ClassPathResource resource = new ClassPathResource("META-INF/spring/integration/sample-order.xml");
        return Files.readString(resource.getFile().toPath());
    }
}