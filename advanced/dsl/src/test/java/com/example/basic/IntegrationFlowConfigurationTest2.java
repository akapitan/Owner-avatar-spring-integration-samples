package com.example.basic;

import com.example.basic.model.Dessert;
import com.example.basic.model.Dish;
import com.example.basic.model.Drink;
import com.example.basic.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({IntegrationFlowConfiguration.class, KitchenService.class, RestaurantGateway.class})
@ImportAutoConfiguration(IntegrationAutoConfiguration.class)
@SpringIntegrationTest
//@SpringBootTest
class IntegrationFlowConfigurationTest2 {

    @Autowired
    private RestaurantGateway restaurantGateway;

    @Autowired
    private QueueChannel testOutputChannel;

    @BeforeEach
    void setUp() {
        this.testOutputChannel.clear();
    }

    @Test
    void ordersFlow_shouldProperlySplitAndProcessOrder() {
        // Given
        Order order = getOrder();
        List results = new ArrayList<>();
        // When
        restaurantGateway.placeOrder(order);

        for (int i = 0; i < 3; i++) {
            Message<?> result = testOutputChannel.receive(5000);
            results.add(result);
        }
        System.out.println(results);
        // Then
        // Assert that we received 3 messages
        assertThat(results).hasSize(3);
        // Extract payloads for easier assertion
        List payloads = results.stream()
                .map(m -> ((Message<?>) m).getPayload())
                .toList();
        // Assert that the payloads contain the expected items, order might not be guaranteed
        assertThat(payloads).containsExactlyInAnyOrder(
                new Drink(1, "Coke"),
                new Dessert(1, "Ice Cream"),
                new Dish(1, "Pizza")
        );
    }

    private static Order getOrder() {
        Order order = new Order(1,
                new Dessert(1, "Ice Cream"),
                new Dish(1, "Pizza"),
                new Drink(1, "Coke"));
        return order;
    }

    @Test
    void ordersFlow_shouldProperlySplitAndProcessOrder2() {
        // Given
        Order order1 = getOrder();
        Order order2 = new Order(2,
                new Dessert(2, "Custard"),
                new Dish(2, "Pizza"),
                new Drink(2, "Cappuccino"));
        Order order3 = new Order(3,
                new Dessert(3, "Flan"),
                new Dish(3, "Lasagne"),
                new Drink(3, "Whiskey"));
        List results = new ArrayList<>();
        // When
        restaurantGateway.placeOrder(order1);
        restaurantGateway.placeOrder(order2);
        restaurantGateway.placeOrder(order3);

        for (int i = 0; i < 9; i++) {
            results.add(this.testOutputChannel.receive(5000));
        }
        System.out.println(results);
        // Then
        // Assert that we received 3 messages
        assertThat(results).hasSize(9);
        // Extract payloads for easier assertion
        List payloads = results.stream()
                .map(m -> ((Message<?>) m).getPayload())
                .toList();
        // Assert that the payloads contain the expected items, order might not be guaranteed
        assertThat(payloads).containsExactlyInAnyOrder(
                new Drink(1, "Coke"),
                new Dessert(1, "Ice Cream"),
                new Dish(1, "Pizza"),
                new Dessert(2, "Custard"),
                new Dish(2, "Pizza"),
                new Drink(2, "Cappuccino"),
                new Dessert(3, "Flan"),
                new Dish(3, "Lasagne"),
                new Drink(3, "Whiskey")
        );
    }

    @TestConfiguration
    static class Config {

        @Bean
        QueueChannel testOutputChannel() {
            return new QueueChannel(12);
        }

/*        @Bean
        IntegrationFlow bridgeFlow(PublishSubscribeChannelSpec outputChannel, PollableChannel testOutputChannel) {
            return IntegrationFlow.from(outputChannel).channel(testOutputChannel).get();
        }*/

        @Bean
        public IntegrationFlow wireTapFlow(MessageChannelSpec outputChannel) {
            return IntegrationFlow.from(outputChannel).wireTap(testOutputChannel()).get();
        }

    }

}