package org.example.td5_spring_boo.controller;

import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.service.IngredientService;
import org.springframework.web.bind.annotation.GetMapping;
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

}
