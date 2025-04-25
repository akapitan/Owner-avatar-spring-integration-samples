package com.example.basic.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.messaging.MessageChannel;

@Configuration
public class HttpInboundGatewayConfig {

    @Bean
    public MessageChannel receiveChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel replyChannel() {
        return new DirectChannel();
    }

    @Bean
    public HttpRequestHandlingMessagingGateway httpInbound() {
        HttpRequestHandlingMessagingGateway gateway = new HttpRequestHandlingMessagingGateway(true);
        RequestMapping mapping = new RequestMapping();
        mapping.setMethods(HttpMethod.POST);
        mapping.setPathPatterns("/greeting");
        gateway.setRequestMapping(mapping);
        gateway.setRequestChannel(receiveChannel());
        gateway.setReplyChannel(replyChannel());
        return gateway;
    }

    @ServiceActivator(inputChannel = "receiveChannel", outputChannel = "replyChannel")
    public String handleHttpRequest(String name) {
        System.out.println("Received request with name: " + name);
        return "Hello, " + name;
    }
}