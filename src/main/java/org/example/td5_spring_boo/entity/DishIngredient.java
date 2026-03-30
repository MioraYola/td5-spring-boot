package org.example.td5_spring_boo.entity;

import org.example.td5_spring_boo.enums.UnitTypeEnum;

public record DishIngredient (int id, Dish dish, Ingredient ingredient, Double quantityRequired, UnitTypeEnum unit) {
}
