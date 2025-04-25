package com.example.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.http.config.EnableIntegrationGraphController;

@SpringBootApplication
@EnableIntegrationGraphController
public class SpringIntegrationSamplesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringIntegrationSamplesApplication.class, args);
    }

}
