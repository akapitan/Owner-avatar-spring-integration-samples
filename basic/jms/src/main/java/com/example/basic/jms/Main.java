package com.example.basic.jms;

import com.example.basic.jms.gateway.JmsGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.UUID;

/**
 * Main class providing interactive console functionality for the JMS sample.
 * Users can send messages to JMS destinations and see responses.
 */
@Component
@Profile("!test")
public class Main implements CommandLineRunner {

    private final JmsGateway jmsGateway;
    private final MessageChannel aggregatorInputChannel;

    @Autowired
    public Main(JmsGateway jmsGateway, @Qualifier("aggregatorInputChannel") MessageChannel aggregatorInputChannel) {
        this.jmsGateway = jmsGateway;
        this.aggregatorInputChannel = aggregatorInputChannel;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("\n==== Spring Integration JMS Sample ====");
        System.out.println("Send messages to JMS destinations and see responses.");
        System.out.println("Type 'exit' to quit the application.");
        System.out.println("==================================");

        while (!exit) {
            System.out.print("\nEnter a message to send (or choose an option):\n");
            System.out.println("1. Send to default queue");
            System.out.println("2. Send to custom destination");
            System.out.println("3. Send request-reply message");
            System.out.println("4. Aggregation Demo");
            System.out.println("5. Exit");
            System.out.print("\nSelect an option: ");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equals("5")) {
                exit = true;
                System.out.println("Exiting...");
            } else if (input.equals("1")) {
                sendToDefaultQueue(scanner);
            } else if (input.equals("2")) {
                sendToCustomDestination(scanner);
            } else if (input.equals("3")) {
                sendRequestReply(scanner);
            } else if (input.equals("4")) {
                runAggregationDemo(scanner);
            } else {
                // Any other input is treated as a message to send to the default queue
                jmsGateway.sendToJms(input);
                System.out.println("Message sent to default queue: " + input);
            }
        }

        // Give a moment for any async processes to complete before exit
        System.out.println("Waiting for any pending messages to process...");
        Thread.sleep(1000);
    }

    private void sendToDefaultQueue(Scanner scanner) {
        System.out.print("Enter message to send to default queue: ");
        String message = scanner.nextLine();
        jmsGateway.sendToJms(message);
        System.out.println("Message sent to default queue: " + message);
    }

    private void sendToCustomDestination(Scanner scanner) {
        System.out.print("Enter destination name: ");
        String destination = scanner.nextLine();

        System.out.print("Enter message to send: ");
        String message = scanner.nextLine();

        jmsGateway.sendToJms(message, destination);
        System.out.println("Message sent to destination '" + destination + "': " + message);
    }

    private void sendRequestReply(Scanner scanner) {
        System.out.print("Enter request message: ");
        String message = scanner.nextLine();

        // Use the request-reply gateway method
        try {
            String response = jmsGateway.sendAndReceive(message);
            System.out.println("Received response: " + response);
        } catch (Exception e) {
            System.out.println("Error getting reply: " + e.getMessage());
            System.out.println("Request was sent but response timed out or failed.");
        }
    }

    private void runAggregationDemo(Scanner scanner) {
        System.out.println("\n=== Aggregation Demo ===");
        System.out.println("This demo will send multiple messages with the same correlation ID.");
        System.out.println("Messages will be aggregated when they contain 'END' word.");
        System.out.println("Enter messages (type 'done' when finished):");

        String correlationId = UUID.randomUUID().toString();
        System.out.println("Using correlation ID: " + correlationId);

        boolean done = false;
        int count = 1;

        while (!done) {
            System.out.print("Message " + count + ": ");
            String message = scanner.nextLine();

            if (message.equalsIgnoreCase("done")) {
                done = true;
            } else {
                // Send message with correlation ID
                aggregatorInputChannel.send(
                        MessageBuilder.withPayload(message)
                                .setHeader("correlationId", correlationId)
                                .build()
                );

                System.out.println("Message sent for aggregation: " + message);
                count++;

                // If message contains END, notify user that aggregation will happen
                if (message.toUpperCase().contains("END")) {
                    System.out.println("Message contains 'END', aggregation will happen if at least 2 messages received.");
                }
            }
        }

        System.out.println("Aggregation demo complete. Check console for aggregation results.");
    }
}