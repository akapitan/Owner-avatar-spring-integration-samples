package com.example.advanced.scattergather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;

@SpringBootApplication
public class ScatterGatherApplication {
    public static void main(String[] args) {

        Logger LOG = LoggerFactory.getLogger(ScatterGatherApplication.class);

        ConfigurableApplicationContext ctx = SpringApplication.run(ScatterGatherApplication.class, args);
        LOG.info("Sending message...");
        Message<?> hello = ctx.getBean(ScatterGatherGateway.class).exchange("Hello");
        LOG.info("Payload: {}", hello.getPayload());
    }
}
