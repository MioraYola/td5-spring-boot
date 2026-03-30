package org.example.td5_spring_boo.service;

import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    private  final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }
    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public IngredientDTO findById(int id) {
        return ingredientRepository.findById(id);
    }
}
