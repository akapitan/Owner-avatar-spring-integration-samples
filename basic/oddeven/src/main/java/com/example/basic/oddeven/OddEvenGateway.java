package com.example.basic.oddeven;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Gateway interface for sending numbers to the inputChannel.
 */
@MessagingGateway
public interface OddEvenGateway {

    /**
     * Sends an integer to the input channel for odd/even processing.
     * If not specified otherwise, the default channel is created.
     * Channel name is derived from the method name and ".input" suffix.
     * @param number the integer to process
     */
    @Gateway(requestChannel = "oddEvenFlow.input")
    void process(int number);
}
