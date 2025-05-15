package com.example.basic;

import com.example.basic.model.Delivery;
import com.example.basic.model.Dessert;
import com.example.basic.model.Dish;
import com.example.basic.model.Drink;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PublishSubscribeChannelSpec;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.store.SimpleMessageGroup;
import org.springframework.messaging.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration(proxyBeanMethods = false)
public class IntegrationFlowConfiguration {

    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
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
    public PublishSubscribeChannelSpec outputChannel() {
        return MessageChannels.publishSubscribe("output-flow"/*, EXECUTOR_SERVICE*/);
    }

    @Bean
    IntegrationFlow orders(@Qualifier("drinkFlow") IntegrationFlow drinkFlow,
                           @Qualifier("dishFlow") IntegrationFlow dishFlow,
                           @Qualifier("dessertFlow") IntegrationFlow dessertFlow) {
        return f -> f
                .routeToRecipients(c ->
                        c.recipientFlow(drinkFlow)
                                .recipientFlow(dishFlow)
                                .recipientFlow(dessertFlow));
    }


    @Bean
    public IntegrationFlow drinkFlow(@Qualifier("outputChannel") PublishSubscribeChannelSpec outputChannel) {
        return f ->
                f.split("payload.drink")
                        .channel(c -> c.executor(EXECUTOR))
                        .handle(Drink.class, (payload, _) -> kitchenService.prepareDrink(payload))
                        .channel(outputChannel);
    }

    @Bean
    public IntegrationFlow dishFlow(@Qualifier("outputChannel") PublishSubscribeChannelSpec outputChannel) {
        return f -> f
                .split("payload.dish")
                .channel(c -> c.executor(EXECUTOR))
                .handle(Dish.class, (payload, _) -> kitchenService.prepareDish(payload))
                .channel(outputChannel);
    }

    @Bean
    public IntegrationFlow dessertFlow(@Qualifier("outputChannel") PublishSubscribeChannelSpec outputChannel) {
        return f -> f
                .split("payload.dessert")
                .channel(c -> c.executor(EXECUTOR))
                .handle(Dessert.class, (payload, _) -> kitchenService.prepareDessert(payload))
                .channel(outputChannel);
    }

    @Bean
    public IntegrationFlow resultFlow(PublishSubscribeChannelSpec outputChannel) {
        return IntegrationFlow
                .from(outputChannel)
                .aggregate(aggregator -> aggregator
                        .outputProcessor(objects -> {
                            Delivery delivery = new Delivery();
                            SimpleMessageGroup simpleMessageGroupObjects = (SimpleMessageGroup) objects;
                            for (Message<?> o : simpleMessageGroupObjects.getMessages()) {
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