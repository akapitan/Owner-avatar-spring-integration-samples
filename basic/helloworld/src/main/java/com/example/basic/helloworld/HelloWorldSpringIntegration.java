package com.example.basic.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HelloWorldSpringIntegration {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(HelloWorldSpringIntegration.class, args);
        HelloWorldConfiguration.HelloGateway gateway = context.getBean(HelloWorldConfiguration.HelloGateway.class);

        System.out.println("==> " + gateway.sendMessage("Spring Integration"));
    }
}