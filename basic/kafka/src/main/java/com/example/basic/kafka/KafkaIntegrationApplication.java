package com.example.basic.kafka;

import com.example.basic.kafka.gateway.KafkaGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KafkaIntegrationApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(KafkaIntegrationApplication.class, args);
        ctx.getBean(KafkaGateway.class).sendToKafka("Hello Kafka!");
    }
}
