package org.example.td5_spring_boo.controller;

import org.example.td5_spring_boo.DTO.DishDTO;
import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/dishes")
    public List<DishDTO> getDishes() {
        return dishService.getAllDishes();
    }

    @PutMapping("/dishes/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable int id,
            @RequestBody(required = false) List<IngredientDTO> ingredients) {

        try {
            dishService.updateDishIngredients(id, ingredients);
            return ResponseEntity.ok("Updated successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity
                        .status(404)
                        .body("Dish.id=" + id + " is not found");
            }

            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
