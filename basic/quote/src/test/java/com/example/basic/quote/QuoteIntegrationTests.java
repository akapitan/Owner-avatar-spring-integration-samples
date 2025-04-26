package com.example.basic.quote;

import com.example.basic.quote.domain.Quote;
import com.example.basic.quote.service.QuoteService;
import com.example.basic.quote.service.TickerStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@SpringIntegrationTest
@DirtiesContext
class QuoteIntegrationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private QuoteService quoteService;

    @MockBean
    private TickerStream tickerStream;

    @Test
    void testQuoteIntegrationFlow() {
        // Get the channel from the application context
        MessageChannel loggingChannel = applicationContext.getBean("loggingChannel", MessageChannel.class);

        // Given a fixed quote response
        Quote testQuote = new Quote("TEST", new BigDecimal("123.45"));
        when(quoteService.lookupQuote(anyString())).thenReturn(testQuote);

        // When we send a ticker symbol to the channel
        loggingChannel.send(MessageBuilder.withPayload("TEST").build());

        // Then the quote service should be called
        verify(quoteService, timeout(1000)).lookupQuote("TEST");
    }
}