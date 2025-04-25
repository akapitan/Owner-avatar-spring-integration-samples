package com.example.basic.amqp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.http.config.EnableIntegrationGraphController;

@SpringBootApplication
@EnableIntegrationGraphController
public class AmqpSpringIntegration {

    public static void main(String[] args) {
        SpringApplication.run(AmqpSpringIntegration.class, args);
    }

}
