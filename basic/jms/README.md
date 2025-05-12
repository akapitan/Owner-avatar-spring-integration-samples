# Spring Integration JMS Sample

This sample demonstrates the use of Spring Integration's JMS support. It uses Java DSL configuration instead of the XML
configuration found in the reference sample.

## Overview

This sample demonstrates the following aspects of the JMS support available with Spring Integration:

1. JMS Message-Driven Channel Adapter
2. JMS Inbound Gateway
3. JMS Outbound Gateway
4. JMS Outbound Channel Adapter
5. Message Aggregation with JMS Messages

The sample is configured to use an embedded ActiveMQ broker by default.

## Running the Sample

Run the `JmsIntegrationApplication` class as a Spring Boot application. Then, follow the prompts in the console.

The application provides several options:

1. Send a message to the default JMS queue
2. Send a message to a specific destination
3. Send a request-reply message through JMS gateways
4. Use the Aggregation Demo to combine multiple messages

## Aggregation Demo

The Aggregation Demo showcases how to combine multiple related messages into a single aggregated message. The demo:

1. Assigns a correlation ID to group related messages
2. Collects messages until a release condition is met:
    - At least 2 messages are received
    - One message contains the word "END"
3. Aggregates the message payloads into a comma-separated list
4. Outputs the result to the console

This demonstrates the [Aggregator](https://www.enterpriseintegrationpatterns.com/patterns/messaging/Aggregator.html)
pattern from Enterprise Integration Patterns.

## Configuration

By default, the sample uses an embedded ActiveMQ broker with an in-memory store:
