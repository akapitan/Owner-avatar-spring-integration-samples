package com.example.basic.model;

public class Delivery {
    private int orderNumber;
    private String dessertName;
    private String dishName;
    private String drinkName;

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDessertName() {
        return dessertName;
    }

    public void setDessertName(String dessertName) {
        this.dessertName = dessertName;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "orderNumber=" + orderNumber +
                ", dessertName='" + dessertName + '\'' +
                ", dishName='" + dishName + '\'' +
                ", drinkName='" + drinkName + '\'' +
                '}';
    }
}
