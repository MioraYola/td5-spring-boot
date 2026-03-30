package org.example.td5_spring_boo.entity;

import org.example.td5_spring_boo.enums.CategoryEnum;

import java.util.List;

public record Ingredient(int id, String name, double price, CategoryEnum category, List<StockMovement> stockMovementList) {

}
