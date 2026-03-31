package org.example.td5_spring_boo.controller;

import org.example.td5_spring_boo.DTO.DishCreateDTO;
import org.example.td5_spring_boo.DTO.DishDTO;
import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.entity.Dish;
import org.example.td5_spring_boo.repository.DishRepository;
import org.example.td5_spring_boo.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
public class DishController {
    private final DishService dishService;
    private final DishRepository dishRepository;

    public DishController(DishService dishService, DishRepository dishRepository) {

        this.dishService = dishService;
        this.dishRepository= dishRepository;
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
    @PostMapping("/dishes")
    public ResponseEntity<?> createDishes(@RequestBody List<DishCreateDTO> dishesDto) {
        try {
            List<Dish> savedDishes =dishRepository.saveAll (dishesDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDishes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }
}
