package com.example.basic;

import com.example.basic.model.Dessert;
import com.example.basic.model.Dish;
import com.example.basic.model.Drink;
import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class KitchenService {
    private static final Logger LOG = LoggerFactory.getLogger(KitchenService.class);

    private final AtomicInteger drinkCounter = new AtomicInteger();
    private final AtomicInteger dishCounter = new AtomicInteger();
    private final AtomicInteger dessertCounter = new AtomicInteger();

    public Drink prepareDrink(Drink drink) {
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        LOG.info("Preparing drink {} for order #{}:{} ", drinkCounter.incrementAndGet(), drink.drinkName(), drink.orderNumber());
        return drink;
    }

    public Dessert prepareDessert(Dessert dessert) {
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        LOG.info("Preparing dessert {} for order #{}:{} ", dessertCounter.incrementAndGet(), dessert.dessertName(), dessert.orderNumber());
        return dessert;
    }

    public Dish prepareDish(Dish dish) {
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        LOG.info("Preparing dish {} for order #{}:{} ", dishCounter.incrementAndGet(), dish.dishName(), dish.orderNumber());
        return dish;
    }
}
