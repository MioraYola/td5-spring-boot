package org.example.td5_spring_boo.DTO;

import java.util.List;

public record DishDTO(int id,
                      String name,
                      double price,
                      List<IngredientDTO> ingredients) {
}
