package org.example.td5_spring_boo.repository;

import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.entity.Ingredient;
import org.example.td5_spring_boo.enums.CategoryEnum;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
public class IngredientRepository {
    private final JdbcTemplate jdbcTemplate;

    public IngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<IngredientDTO> findAll() {
        String sql = "SELECT id, name, price, category FROM ingredient ORDER BY id";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new IngredientDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category").toLowerCase()),
                        rs.getDouble("price")
                )
        );
    }

    public IngredientDTO findById(int id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return new IngredientDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category").toLowerCase()),
                        rs.getDouble("price")
                );
            }
            return null;
        }, id);
    }
}
