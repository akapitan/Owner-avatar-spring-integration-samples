package com.example.basic.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.PollableChannel;

import java.util.Objects;

@SpringBootApplication
public class PollerApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(PollerApp.class, args);
        PollableChannel outputChannel = context.getBean("outputChannel2", PollableChannel.class);

        System.out.println("Waiting for messages...");

        // Receive and print messages from the output channel
        for (int i = 0; i < 3; i++) {
            System.out.println("Message: " + Objects.requireNonNull(outputChannel.receive(10000)).getPayload());
        }

        context.close();
    }
}