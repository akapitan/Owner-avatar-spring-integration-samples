package com.example.basic;

import com.example.basic.model.Dessert;
import com.example.basic.model.Dish;
import com.example.basic.model.Drink;
import com.example.basic.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Random;

@SpringBootApplication
public class DslApplication {

    private static final Logger LOG = LoggerFactory.getLogger(DslApplication.class);
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(DslApplication.class, args);

        RestaurantGateway restaurantGateway = ctx.getBean(RestaurantGateway.class);
        for (int i = 1; i <= 10; i++) {
            LOG.info("Placing order {}", i);
            Order order = new Order(i, new Dessert(i, generateDessertName()), new Dish(i, generateDishName()), new Drink(i, generateDrinkName()));
            restaurantGateway.placeOrder(order);
        }

        System.out.println("Hit 'Enter' to terminate");
        System.in.read();
        ctx.close();
    }

    private static String generateDrinkName() {
        List<String> drinkName = List.of("Black Coffee", "Lemon Tea", "Cappuccino", "Mocha", "Iced Tea", "Pepsi", "Coke", "Mountain Dew", "Vodka", "Whiskey");
        int randomNum = RANDOM.nextInt(9);
        return drinkName.get(randomNum);
    }

    private static String generateDishName() {
        List<String> drinkName = List.of("Grilled Salmon", "Burger", "Pizza", "Biryani", "Filet Mignon", "Fish and chips", "Chicken tikka masala", "Bruschetta", "Fried eggplant", "Lasagne");
        int randomNum = RANDOM.nextInt(9);
        return drinkName.get(randomNum);
    }

    private static String generateDessertName() {
        List<String> drinkName = List.of("Chocolate Mouse", "Cheese cake", "Gulab Jamun", "Custard", "Sorbet", "Apple Pie", "Creme Brulee", "Sundae", "Baklava", "Flan");
        int randomNum = RANDOM.nextInt(9);
        return drinkName.get(randomNum);
    }
}
