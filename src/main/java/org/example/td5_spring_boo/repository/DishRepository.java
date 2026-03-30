package org.example.td5_spring_boo.repository;

import org.example.td5_spring_boo.DTO.DishDTO;
import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.enums.CategoryEnum;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DishRepository {
    private final JdbcTemplate jdbcTemplate;
    private final IngredientRepository ingredientRepository;

    public DishRepository(IngredientRepository ingredientRepository, JdbcTemplate jdbcTemplate) {
        this.ingredientRepository = ingredientRepository;
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<DishDTO> findAllDishes() {
        String sql = "SELECT id, name, price FROM dish ORDER BY id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int dishId = rs.getInt("id");
            String name = rs.getString("name");
            double price = rs.getDouble("price");

            // Récupérer les ingrédients du plat
            List<IngredientDTO> ingredients = jdbcTemplate.query("""
                    SELECT i.id, i.name, i.price, i.category
                    FROM dishIngredient di
                    JOIN ingredient i ON di.id_ingredient = i.id
                    WHERE di.id_dish = ?
                    """, (irs, iRow) -> new IngredientDTO(
                    irs.getInt("id"),
                    irs.getString("name"),
                    CategoryEnum.valueOf(irs.getString("category").toLowerCase()),
                    irs.getDouble("price")
            ), dishId);

            return new DishDTO(dishId, name, price, ingredients);
        });
    }
}
