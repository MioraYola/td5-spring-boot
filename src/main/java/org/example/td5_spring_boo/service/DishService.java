package org.example.td5_spring_boo.service;

import org.example.td5_spring_boo.DTO.DishDTO;
import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<DishDTO> getAllDishes() {
        return dishRepository.findAllDishes();
    }

    public void updateDishIngredients(int dishId, List<IngredientDTO> ingredients) {

        if (ingredients == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        // 404 si dish absent
        if (!dishRepository.existsDishById(dishId)) {
            throw new RuntimeException("Dish.id=" + dishId + " is not found");
        }

        // existants
        Set<Integer> existingIds = new HashSet<>(dishRepository.findIngredientIdsByDish(dishId));

        // nouveaux (filtrés)
        Set<Integer> newIds = new HashSet<>();
        for (IngredientDTO ing : ingredients) {
            if (dishRepository.existsIngredientById(ing.id())) {
                newIds.add(ing.id());
            }
        }

        // calcul diff
        Set<Integer> toAdd = new HashSet<>(newIds);
        toAdd.removeAll(existingIds);

        Set<Integer> toRemove = new HashSet<>(existingIds);
        toRemove.removeAll(newIds);

        // appliquer
        for (Integer id : toAdd) {
            dishRepository.addIngredientToDish(dishId, id);
        }

        for (Integer id : toRemove) {
            dishRepository.removeIngredientFromDish(dishId, id);
        }
    }
}
