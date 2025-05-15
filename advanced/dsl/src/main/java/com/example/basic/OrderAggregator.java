package com.example.basic;

import com.example.basic.model.Delivery;
import com.example.basic.model.Dessert;
import com.example.basic.model.Dish;
import com.example.basic.model.Drink;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderAggregator {
    @Aggregator
    public Delivery output(List<Object> objects) {
        Delivery delivery = new Delivery();
        for (Object o : objects) {
            if (o instanceof Drink(int orderNumber, String drinkName)) {
                delivery.setDrinkName(drinkName);
                delivery.setOrderNumber(orderNumber);
            } else if (o instanceof Dish dish) {
                delivery.setDishName(dish.dishName());
            } else if (o instanceof Dessert dessert) {
                delivery.setDessertName(dessert.dessertName());
            }
        }

        return delivery;
    }

    @CorrelationStrategy
    public int correlateBy(Object object) {
        return switch (object) {
            case Drink drink -> drink.orderNumber();
            case Dish dish -> dish.orderNumber();
            case Dessert drink -> drink.orderNumber();
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }

    @ReleaseStrategy
    public boolean releaseChecker(List<Message<Object>> messages) {
        return messages.size() == 3;
    }
}
