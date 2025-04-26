package com.example.basic.quote.service;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Service that provides a stream of random stock ticker symbols.
 */
@Service
public class TickerStream {

    /**
     * Generates a random 3-character ticker symbol.
     *
     * @return a random stock ticker symbol
     */
    public String nextTicker() {
        char[] chars = new char[3];
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            chars[i] = (char) (random.nextInt(26) + 'A');
        }
        return new String(chars);
    }
}