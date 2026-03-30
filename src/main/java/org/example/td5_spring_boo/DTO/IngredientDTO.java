package org.example.td5_spring_boo.DTO;

import org.example.td5_spring_boo.enums.CategoryEnum;

public record IngredientDTO(int id,
                            String name,
                            CategoryEnum category,
                            Double price) {
}
