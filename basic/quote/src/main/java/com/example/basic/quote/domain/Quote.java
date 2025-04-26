package com.example.basic.quote.domain;

import java.math.BigDecimal;

/**
 * Represents a stock ticker quote.
 */
public class Quote {

    private final String ticker;
    private final BigDecimal price;

    public Quote(String ticker, BigDecimal price) {
        this.ticker = ticker;
        this.price = price;
    }

    public String getTicker() {
        return ticker;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return ticker + ": " + price;
    }
}