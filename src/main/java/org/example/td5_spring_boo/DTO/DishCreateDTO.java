package org.example.td5_spring_boo.DTO;

import org.example.td5_spring_boo.enums.DishTypeEnum;

public record DishCreateDTO(String name, DishTypeEnum dishType, Double price) {
}
