package com.example.basic.quote.service;

import com.example.basic.quote.domain.Quote;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Service that generates random stock quotes for a given ticker symbol.
 */
@Service
public class QuoteService {

    /**
     * Looks up a quote for a given ticker symbol.
     * In a real application, this might call an external API, but here we generate random prices.
     *
     * @param ticker the stock ticker symbol
     * @return a quote for the requested ticker
     */
    public Quote lookupQuote(String ticker) {
        // Generate a random price between 0 and 100
        BigDecimal price = new BigDecimal(new Random().nextDouble() * 100);
        // Round to 2 decimal places
        return new Quote(ticker, price.setScale(2, RoundingMode.HALF_EVEN));
    }
}