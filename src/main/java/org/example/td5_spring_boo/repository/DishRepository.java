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

    public boolean existsDishById(int id) {
        String sql = "SELECT COUNT(*) FROM dish WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public List<Integer> findIngredientIdsByDish(int dishId) {
        String sql = "SELECT id_ingredient FROM dishIngredient WHERE id_dish = ?";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("id_ingredient"),
                dishId
        );
    }

    public boolean existsIngredientById(int id) {
        String sql = "SELECT COUNT(*) FROM ingredient WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public void addIngredientToDish(int dishId, int ingredientId) {
        String sql = """
        INSERT INTO dishIngredient(id_dish, id_ingredient)
        VALUES (?, ?)
        ON CONFLICT DO NOTHING
    """;

        jdbcTemplate.update(sql, dishId, ingredientId);
    }

    public void removeIngredientFromDish(int dishId, int ingredientId) {
        String sql = """
        DELETE FROM dishIngredient
        WHERE id_dish = ? AND id_ingredient = ?
    """;

        jdbcTemplate.update(sql, dishId, ingredientId);
    }
}
