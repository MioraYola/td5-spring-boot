package org.example.td5_spring_boo.repository;

import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.DTO.StockValueDTO;
import org.example.td5_spring_boo.enums.CategoryEnum;
import org.example.td5_spring_boo.enums.UnitTypeEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<IngredientDTO> findAll() {
        String sql = "SELECT id, name, price, category FROM ingredient ORDER BY id";
        List<IngredientDTO> ingredients = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ingredients.add(new IngredientDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category").toLowerCase()),
                        rs.getDouble("price")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ingredients;
    }

    public IngredientDTO findById(int id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new IngredientDTO(
                            rs.getInt("id"),
                            rs.getString("name"),
                            CategoryEnum.valueOf(rs.getString("category").toLowerCase()),
                            rs.getDouble("price")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public StockValueDTO getStockValueAt(int ingredientId, Instant at, UnitTypeEnum unit) {
        String sql = """
            SELECT SUM(quantity) AS total_quantity
            FROM stockMovement
            WHERE id_ingredient = ?
              AND creation_datetime <= ?
              AND unit = ?
        """;

        Double quantity = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ingredientId);
            stmt.setTimestamp(2, Timestamp.from(at));
            stmt.setString(3, unit.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    quantity = rs.getDouble("total_quantity");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (quantity == null) {
            return null; // pas de mouvement avant 'at' ou ingrédient non trouvé
        }

        return new StockValueDTO(unit, quantity);
    }
}