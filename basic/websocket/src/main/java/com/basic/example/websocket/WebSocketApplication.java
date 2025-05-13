package com.basic.example.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main WebSocket application that can run either the server or client based on properties.
 * <p>
 * To run as server:
 * --spring.main.web-application-type=servlet
 * <p>
 * To run as client:
 * --spring.main.sources=com.example.websocket.client.WebSocketClientApplication
 */
@SpringBootApplication
public class WebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }
}