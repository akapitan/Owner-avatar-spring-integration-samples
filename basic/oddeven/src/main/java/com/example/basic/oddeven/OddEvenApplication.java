package com.example.basic.oddeven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for the OddEven Integration sample.
 */
@SpringBootApplication
public class OddEvenApplication {

    private OddEvenApplication() {
        // Utility class constructor is private to prevent instantiation.
    }

    public static void main(String[] args) {
        SpringApplication.run(OddEvenApplication.class, args);
    }
}
