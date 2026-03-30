package org.example.td5_spring_boo.controller;

import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.service.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/ingredients")
    public List<IngredientDTO> getIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable int id) {
        IngredientDTO ingredient = ingredientService.findById(id);
        if (ingredient == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Ingredient.id=" + ingredient.id() + " is not found");
        }
        return ResponseEntity.ok(ingredient);
    }

}
