package org.example.td5_spring_boo.controller;

import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.enums.UnitTypeEnum;
import org.example.td5_spring_boo.service.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
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

    @GetMapping("/ingredients/{id}/stock")
    public ResponseEntity<?> getStockValue(
            @PathVariable int id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit
    ) {
        if (at == null || unit == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        Instant atInstant;
        try {
            atInstant = Instant.parse(at); // format ISO-8601 attendu, ex: 2024-01-06T12:00:00Z
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid `at` datetime format. Use ISO-8601, e.g., 2024-01-06T12:00:00Z");
        }

        UnitTypeEnum uniteType;
        try {
            uniteType = UnitTypeEnum.valueOf(unit.toUpperCase());
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid `unit`. Allowed values: PCS, KG, L");
        }

        var stockValue = ingredientService.getStockValue(id, atInstant, uniteType);

        if (stockValue == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Ingredient.id=" + id + " is not found");
        }

        return ResponseEntity.ok(stockValue);
    }

}
