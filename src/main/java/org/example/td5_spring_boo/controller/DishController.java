package org.example.td5_spring_boo.controller;

import org.example.td5_spring_boo.DTO.DishDTO;
import org.example.td5_spring_boo.service.DishService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
