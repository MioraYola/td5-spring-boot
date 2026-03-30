package org.example.td5_spring_boo.service;

import org.example.td5_spring_boo.DTO.DishDTO;
import org.example.td5_spring_boo.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<DishDTO> getAllDishes() {
        return dishRepository.findAllDishes();
    }
}
