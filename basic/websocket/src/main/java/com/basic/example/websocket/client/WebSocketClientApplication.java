package com.basic.example.websocket.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.websocket.ClientWebSocketContainer;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

/**
 * WebSocket Client Application using Spring Integration Java DSL config
 */
@SpringBootApplication
@EnableIntegration
public class WebSocketClientApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(WebSocketClientApplication.class, args);
        System.out.println("WebSocket Client is running. Hit 'Enter' to terminate.");
        System.in.read();
        ctx.close();
    }

    @Bean
    public ClientWebSocketContainer clientWebSocketContainer() {
        return new ClientWebSocketContainer(new StandardWebSocketClient(),
                "ws://localhost:8080/time/websocket");
    }

    @Bean
    public IntegrationFlow webSocketFlow() {
        WebSocketInboundChannelAdapter adapter = new WebSocketInboundChannelAdapter(clientWebSocketContainer());
        return IntegrationFlow.from(adapter)
                .log(LoggingHandler.Level.INFO, "wsLog")
                .nullChannel();
    }
}