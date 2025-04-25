package com.example.basic.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.messaging.MessageChannel;

@Configuration
public class HttpOutboundGatewayConfig {

    @Bean
    public MessageChannel requestChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel responseChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "requestChannel")
    public HttpRequestExecutingMessageHandler httpOutbound() {
        HttpRequestExecutingMessageHandler handler =
                new HttpRequestExecutingMessageHandler("http://localhost:8080/greeting");
        handler.setHttpMethod(HttpMethod.POST);
        handler.setExpectedResponseType(String.class);
        handler.setOutputChannel(responseChannel());
        return handler;
    }

    @MessagingGateway
    public interface GreetingGateway {
        @Gateway(requestChannel = "requestChannel", replyChannel = "responseChannel")
        String sendMessage(String name);
    }
}