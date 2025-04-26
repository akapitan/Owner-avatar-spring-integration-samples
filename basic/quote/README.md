# Spring Integration Quote Sample

This sample demonstrates the use of Spring Integration's Java DSL for creating a simple integration flow. It's based on
the original XML configuration sample but fully converted to Java DSL.

## Overview

The sample consists of a simple integration flow that:

1. Periodically generates random stock ticker symbols (like "ABC", "XYZ")
2. Looks up a random price for each ticker
3. Outputs the stock ticker and price to the console

## Java DSL vs XML Configuration

The original XML configuration (`quoteDemo.xml`) used the following components:

- Inbound channel adapter with poller
- Message channel
- Service activator
- Stdout channel adapter

This sample demonstrates how to implement the same functionality using Spring Integration's Java DSL, which offers
several advantages:

- Type safety and IDE auto-completion
- Better refactoring support
- Easier testing
- More concise and readable configuration
- Ability to use the full power of Java for complex configurations

## Running the Sample

You can run this sample directly from your IDE or using Gradle:

```bash
./gradlew :basic:quote:bootRun
```

## Key Classes

- `Quote` - Domain class representing a stock quote with ticker symbol and price
- `TickerStream` - Service that generates random stock ticker symbols
- `QuoteService` - Service that provides quotes for ticker symbols
- `QuoteIntegrationConfig` - Integration configuration using Java DSL
- `QuoteApplication` - Main Spring Boot application

## Flow Explanation

The integration flow works as follows:

1. `tickerStream.nextTicker()` is called every 300ms to generate a random ticker symbol
2. The ticker is sent as a message to the transformation step
3. `quoteService.lookupQuote()` transforms the ticker into a Quote object
4. The Quote is output to the console

There's also a secondary flow that logs quotes at the INFO level with a different polling interval.

## Testing

The sample includes two test classes:

- `QuoteServiceTests` - Tests the QuoteService functionality
- `QuoteIntegrationTests` - Tests the integration flow by sending messages to the channel