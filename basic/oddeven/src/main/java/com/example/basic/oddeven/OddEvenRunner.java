package com.example.basic.oddeven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Runner that sends a stream of numbers to the OddEvenGateway at application startup.
 */
@Component
public class OddEvenRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(OddEvenRunner.class);
    private final OddEvenGateway oddEvenGateway;

    public OddEvenRunner(OddEvenGateway oddEvenGateway) {
        this.oddEvenGateway = oddEvenGateway;
    }

    @Override
    public void run(String... args) throws InterruptedException {
        Thread.sleep(2000);
        LOGGER.info("Sending a stream of numbers to check for odd/even...");
        for (int i = 1; i <= 1000; i++) {
            oddEvenGateway.process(i);
        }
    }
}
