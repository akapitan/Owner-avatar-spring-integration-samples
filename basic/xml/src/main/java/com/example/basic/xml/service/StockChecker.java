package com.example.basic.xml.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StockChecker {
    private final Map<String, Boolean> stockInventory;

    public StockChecker() {
        this.stockInventory = new HashMap<>();
        // Sample inventory data
        stockInventory.put("1234", true);
        stockInventory.put("5678", false);
    }

    public boolean checkStock(String isbn) {
        Boolean b = this.stockInventory.get(isbn);
        if (b != null) {
            return b;
        }
        return false;
    }

}