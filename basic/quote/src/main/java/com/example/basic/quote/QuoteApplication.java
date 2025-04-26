package com.example.basic.quote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main entry point for the Quote application.
 * This application demonstrates Spring Integration with Java DSL by
 * generating random stock ticker symbols, looking up quotes, and outputting
 * the results to the console.
 */
@SpringBootApplication
public class QuoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuoteApplication.class, args);

        System.out.println("======================================================");
        System.out.println("          Spring Integration Quote Demo");
        System.out.println("======================================================");
        System.out.println("  Generating random stock quotes every 300ms...");
        System.out.println("  Press Ctrl+C to exit.");
        System.out.println("======================================================");
    }
}