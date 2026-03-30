package org.example.td5_spring_boo.entity;

import org.example.td5_spring_boo.enums.DishTypeEnum;

public record Dish(int id, String name, DishTypeEnum dishType, Double price) {
}
