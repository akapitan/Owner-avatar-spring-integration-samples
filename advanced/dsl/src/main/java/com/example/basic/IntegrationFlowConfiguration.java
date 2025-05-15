package com.example.basic;

import com.example.basic.model.Delivery;
import com.example.basic.model.Dessert;
import com.example.basic.model.Dish;
import com.example.basic.model.Drink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.store.SimpleMessageGroup;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class IntegrationFlowConfiguration {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
    private static final String ORDER_COMPLETED_TEMPLATE = """
            \n==== ORDER #%d COMPLETE ====
            "Drink: %s
            "Dish: %s
            "Dessert: %s
            "========================\n
            """;
    private final KitchenService kitchenService;

    public IntegrationFlowConfiguration(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Bean
    public MessageChannel drinkChannel() {
        return MessageChannels.executor("drink-flow", EXECUTOR_SERVICE).getObject();
    }

    @Bean
    public MessageChannel dishChannel() {
        return MessageChannels.executor("dish-flow", EXECUTOR_SERVICE).getObject();
    }

    @Bean
    public MessageChannel dessertChannel() {
        return MessageChannels.executor("dessert-flow", EXECUTOR_SERVICE).getObject();
    }

    @Bean
    public MessageChannel outputChannel() {
        return MessageChannels.direct("output-flow"/*, EXECUTOR_SERVICE*/).getObject();
    }

/*    @Bean("orders.input")
    public MessageChannel orderChannel() {
        return MessageChannels.direct("output-flow"*//*, EXECUTOR_SERVICE*//*).getObject();
    }*/

    @Bean
    IntegrationFlow orders() {
        return f -> f
                .routeToRecipients(c ->
                        c.recipient(drinkChannel())
                                .recipient(dishChannel())
                                .recipient(dessertChannel()));
    }


    @Bean
    public IntegrationFlow drinkFlow() {
        return IntegrationFlow
                .from(drinkChannel())
                .split("payload.drink")
                .handle(kitchenService, "prepareDrink")
                .channel(outputChannel())
                .get();
    }

    @Bean
    public IntegrationFlow dishFlow() {
        return IntegrationFlow
                .from(dishChannel())
                .split("payload.dish")
                .handle(kitchenService, "prepareDish")
                .channel(outputChannel())
                .get();
    }

    @Bean
    public IntegrationFlow dessertFlow() {
        return IntegrationFlow
                .from(dessertChannel())
                .split("payload.dessert")
                .handle(kitchenService, "prepareDessert")
                .channel(outputChannel())
                .get();
    }

    @Bean
    public IntegrationFlow resultFlow() {
        return IntegrationFlow
                .from(outputChannel())
                .aggregate(aggregator -> aggregator
                        .outputProcessor(objects -> {
                            Delivery delivery = new Delivery();
                            SimpleMessageGroup objects1 = (SimpleMessageGroup) objects;
                            for (Message<?> o : objects1.getMessages()) {
                                Object payload = o.getPayload();
                                switch (payload) {
                                    case Drink drink -> {
                                        delivery.setDrinkName(drink.drinkName());
                                        delivery.setOrderNumber(drink.orderNumber());
                                    }
                                    case Dish dish -> delivery.setDishName(dish.dishName());
                                    case Dessert desert -> delivery.setDessertName(desert.dessertName());
                                    default -> {
                                    }
                                }
                            }
                            return delivery;
                        })
                        .releaseStrategy(group -> group.size() == 3)
                        .correlationStrategy(message -> {
                            Object payload = message.getPayload();
                            return switch (payload) {
                                case Drink drink -> drink.orderNumber();
                                case Dish dish -> dish.orderNumber();
                                case Dessert desert -> desert.orderNumber();
                                default -> null;
                            };
                        })
                        .groupTimeout(5000) // 5 second timeout for incomplete groups
                        .sendPartialResultOnExpiry(true)
                        .expireGroupsUponCompletion(true)
                )
                .<Delivery, String>transform(delivery ->
                        String.format(
                                ORDER_COMPLETED_TEMPLATE,
                                delivery.getOrderNumber(),
                                delivery.getDrinkName(),
                                delivery.getDishName(),
                                delivery.getDessertName()))
//                .handle(CharacterStreamWritingMessageHandler.stdout())
                .log(LoggingHandler.Level.INFO, "Order Complete", "payload")
                .get();
    }

}