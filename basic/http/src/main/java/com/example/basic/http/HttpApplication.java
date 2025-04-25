package com.example.basic.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class HttpApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(HttpApplication.class, args);
        System.out.println("Spring Integration HTTP Sample is running...");
        System.out.println("Send a POST request to http://localhost:8080/greeting with a text body");
        System.out.println("Or run the HttpClientDemo class to test the outbound gateway");
    }
}