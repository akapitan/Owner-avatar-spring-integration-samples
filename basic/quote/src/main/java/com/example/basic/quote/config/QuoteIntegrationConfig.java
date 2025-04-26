package com.example.basic.quote.config;

import com.example.basic.quote.service.QuoteService;
import com.example.basic.quote.service.TickerStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

import java.time.Duration;

/**
 * Spring Integration configuration using Java DSL.
 * This replaces the XML configuration from quoteDemo.xml.
 */
@Configuration
@EnableIntegration
public class QuoteIntegrationConfig {

    private final QuoteService quoteService;
    private final TickerStream tickerStream;

    public QuoteIntegrationConfig(QuoteService quoteService, TickerStream tickerStream) {
        this.quoteService = quoteService;
        this.tickerStream = tickerStream;
    }

    /**
     * Main quote integration flow that generates ticker symbols, looks up quotes,
     * and outputs the results to the console.
     */
    @Bean
    public IntegrationFlow quoteFlow() {
        return IntegrationFlow
                .fromSupplier(tickerStream::nextTicker,
                        c -> c.poller(Pollers.fixedDelay(Duration.ofMillis(300))
                                .maxMessagesPerPoll(1)))
                .transform(quoteService::lookupQuote)
                .handle(message -> System.out.println(message.getPayload()))
                .get();
    }

    /**
     * Logging channel for testing.
     */
    @Bean
    public MessageChannel loggingChannel() {
        return new DirectChannel();
    }

    /**
     * Alternative configuration that logs quotes at INFO level.
     */
    @Bean
    public IntegrationFlow quoteLoggingFlow() {
        return IntegrationFlow
                .fromSupplier(tickerStream::nextTicker,
                        c -> c.poller(Pollers.fixedDelay(Duration.ofSeconds(1))
                                .maxMessagesPerPoll(1)))
                .channel(loggingChannel())
                .transform(quoteService::lookupQuote)
                .log(LoggingHandler.Level.INFO, message -> "New quote: " + message.getPayload())
                .get();
    }

    /**
     * Service activator method that receives quotes and processes them.
     * This demonstrates an alternative way to handle messages using annotations.
     */
    @ServiceActivator(inputChannel = "loggingChannel")
    public void processQuote(String ticker) {
        // Additional processing could be done here
    }
}