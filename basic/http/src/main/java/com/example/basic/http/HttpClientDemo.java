package com.example.basic.http;

import com.example.basic.http.java.HttpOutboundGatewayConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HttpClientDemo {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(HttpClientDemo.class, args);
        HttpOutboundGatewayConfig.GreetingGateway gateway = context.getBean(HttpOutboundGatewayConfig.GreetingGateway.class);

        System.out.println("Sending request through HTTP outbound gateway...");
        String response = gateway.sendMessage("Spring Integration");
        System.out.println("Received response: " + response);

        context.close();
    }
}