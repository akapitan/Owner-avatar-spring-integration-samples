# XML Processing with Spring Integration

This module demonstrates XML processing with Spring Integration using annotation-based Java configuration. It implements
an order
processing flow that:

1. Takes an XML order as input
2. Splits it into individual order items
3. Checks stock availability for each item
4. Routes the order item based on stock availability:
    - If in stock: sends to warehouse for dispatch
    - If out of stock: transforms the order item to a different format and sends it to an external supplier

## Java vs. XML Configuration

This example uses annotation-based Java configuration instead of XML configuration. The original XML configuration file
that served
as the basis for this example is available at:
`https://github.com/spring-projects/spring-integration-samples/blob/main/basic/xml/src/main/resources/META-INF/spring/integration/orderProcessingSample.xml`

Key differences in this implementation:

1. Uses Spring Integration annotations instead of XML
2. Implements a custom Java transformer instead of XSLT
3. Uses Spring's channel abstractions for message routing
4. Provides more type-safety through Java classes

## Flow Components

The integration flow uses these components:

- **XPath Splitter**: Splits incoming orders into individual order items
- **Service Activator**: Uses the StockChecker to check inventory
- **Router**: Routes based on the 'in-stock' attribute
- **Custom Java Transformer**: Transforms out-of-stock items to the external supplier format

## Running the Example

Build and run using Gradle:
