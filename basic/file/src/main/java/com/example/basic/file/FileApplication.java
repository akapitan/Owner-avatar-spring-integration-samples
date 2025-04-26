package com.example.basic.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Spring Integration File sample.
 */
@SpringBootApplication
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);

        System.out.println("=================================================");
        System.out.println("Welcome to Spring Integration File Sample");
        System.out.println("=================================================");
        System.out.println("\n");
        System.out.println("    The application creates two integration flows:");
        System.out.println("    1. Simple file copying from input to output directory");
        System.out.println("    2. File processing with transformation");
        System.out.println("\n");
        System.out.println("    See application.properties for directory configuration");
        System.out.println("\n");
        System.out.println("=================================================");
    }
}