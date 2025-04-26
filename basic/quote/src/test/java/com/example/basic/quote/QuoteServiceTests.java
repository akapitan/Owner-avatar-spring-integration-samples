package com.example.basic.quote;

import com.example.basic.quote.domain.Quote;
import com.example.basic.quote.service.QuoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class QuoteServiceTests {

    @Autowired
    private QuoteService quoteService;

    @Test
    void testLookupQuote() {
        // Given a ticker symbol
        String ticker = "ABC";

        // When we request a quote
        Quote quote = quoteService.lookupQuote(ticker);

        // Then we should get a valid quote with the correct ticker
        assertNotNull(quote);
        assertEquals(ticker, quote.getTicker());
        assertNotNull(quote.getPrice());

        // The price should have 2 decimal places
        assertEquals(2, quote.getPrice().scale());
    }
}