package com.example.basic;

import com.example.basic.model.Dessert;
import com.example.basic.model.Dish;
import com.example.basic.model.Drink;
import com.example.basic.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@SpringIntegrationTest
@DirtiesContext
@DisabledInAotMode
class IntegrationFlowConfigurationTest {

    @Autowired
    private RestaurantGateway restaurantGateway;

    @MockitoBean
    private KitchenService kitchenService;

    @Test
    void ordersFlow_shouldProperlySplitAndProcessOrder() {
        // Given
        Order order = new Order(1,
                new Dessert(1, "Ice Cream"),
                new Dish(1, "Pizza"),
                new Drink(1, "Coke"));

        // When
        restaurantGateway.placeOrder(order);

        // Then
        // Verify that each item was processed separately
        verify(kitchenService).prepareDrink(any(Drink.class));
        verify(kitchenService).prepareDish(any(Dish.class));
        verify(kitchenService).prepareDessert(any(Dessert.class));
    }

    @Test
    void ordersFlow_shouldProcessMultipleOrdersIndependently() {
        // Given
        Order order1 = new Order(1,
                new Dessert(1, "Ice Cream"),
                new Dish(1, "Pizza"),
                new Drink(1, "Coke"));

        Order order2 = new Order(2,
                new Dessert(2, "Cheesecake"),
                new Dish(2, "Burger"),
                new Drink(2, "Coffee"));

        // When
        restaurantGateway.placeOrder(order1);
        restaurantGateway.placeOrder(order2);

        // Then
        // Verify that both orders were processed
        verify(kitchenService).prepareDrink(new Drink(1, "Coke"));
        verify(kitchenService).prepareDrink(new Drink(2, "Coffee"));
        verify(kitchenService).prepareDish(new Dish(1, "Pizza"));
        verify(kitchenService).prepareDish(new Dish(2, "Burger"));
        verify(kitchenService).prepareDessert(new Dessert(1, "Ice Cream"));
        verify(kitchenService).prepareDessert(new Dessert(2, "Cheesecake"));
    }
}