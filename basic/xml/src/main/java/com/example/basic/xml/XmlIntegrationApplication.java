package com.example.basic.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.PollableChannel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@SpringBootApplication
public class XmlIntegrationApplication {
    private static final Logger logger = LoggerFactory.getLogger(XmlIntegrationApplication.class);

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(XmlIntegrationApplication.class, args);

        // Read sample order XML
        String orderXml = readSampleXml();
//        logger.info("Order XML loaded: {}", orderXml);

        try {
            // Get the order channel and send the order
            PollableChannel ordersChannel = context.getBean("ordersChannel", PollableChannel.class);
            for (int i = 0; i < 10; i++) {
                boolean sent = ordersChannel.send(MessageBuilder.withPayload(orderXml).build());
            logger.info("Order sent to channel: {}", sent);
            logger.info("Order processing flow started...");
//            Thread.sleep(100);
            }

            // Keep the application running for a while to allow message flow completion
            Thread.sleep(20000);
        } catch (Exception e) {
            logger.error("Error in processing", e);
        } finally {
            context.close();
        }
    }

    private static String readSampleXml() throws IOException {
        ClassPathResource resource = new ClassPathResource("META-INF/spring/integration/sample-order.xml");
        return new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
    }
}