package org.example.td5_spring_boo.repository;

import org.example.td5_spring_boo.DTO.DishCreateDTO;
import org.example.td5_spring_boo.DTO.DishDTO;
import org.example.td5_spring_boo.DTO.IngredientDTO;
import org.example.td5_spring_boo.entity.Dish;
import org.example.td5_spring_boo.enums.CategoryEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class DishRepository {

    private final DataSource dataSource;
    private final IngredientRepository ingredientRepository;

    public DishRepository(IngredientRepository ingredientRepository, DataSource dataSource) {
        this.ingredientRepository = ingredientRepository;
        this.dataSource = dataSource;
    }

    public List<DishDTO> findAllDishes() {
        String sql = "SELECT id, name, price FROM dish ORDER BY id";
        List<DishDTO> dishes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int dishId = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");

                List<IngredientDTO> ingredients = findIngredientsByDish(conn, dishId);

                dishes.add(new DishDTO(dishId, name, price, ingredients));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dishes;
    }

    private List<IngredientDTO> findIngredientsByDish(Connection conn, int dishId) {
        String sql = """
                SELECT i.id, i.name, i.price, i.category
                FROM dishIngredient di
                JOIN ingredient i ON di.id_ingredient = i.id
                WHERE di.id_dish = ?
                """;

        List<IngredientDTO> ingredients = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dishId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(new IngredientDTO(
                            rs.getInt("id"),
                            rs.getString("name"),
                            CategoryEnum.valueOf(rs.getString("category").toLowerCase()),
                            rs.getDouble("price")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ingredients;
    }

    public boolean existsDishById(int id) {
        String sql = "SELECT COUNT(*) FROM dish WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public List<Integer> findIngredientIdsByDish(int dishId) {
        String sql = "SELECT id_ingredient FROM dishIngredient WHERE id_dish = ?";
        List<Integer> ids = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ids.add(rs.getInt("id_ingredient"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ids;
    }

    public boolean existsIngredientById(int id) {
        String sql = "SELECT COUNT(*) FROM ingredient WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public void addIngredientToDish(int dishId, int ingredientId) {
        String sql = """
                INSERT INTO dishIngredient(id_dish, id_ingredient)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dishId);
            stmt.setInt(2, ingredientId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeIngredientFromDish(int dishId, int ingredientId) {
        String sql = """
                DELETE FROM dishIngredient
                WHERE id_dish = ? AND id_ingredient = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dishId);
            stmt.setInt(2, ingredientId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Dish> saveAll(List<DishCreateDTO> newDishes) throws SQLException {
        List<Dish> savedDishes = new ArrayList<>();
        Set<String> namesSet = new HashSet<>();

        // Vérifie doublons dans la liste
        for (DishCreateDTO dto : newDishes) {
            if (!namesSet.add(dto.name())) {
                throw new IllegalArgumentException("Dish.name=" + dto.name() + " already exists");
            }
        }

        String checkExistSQL = "SELECT COUNT(*) FROM dish WHERE name = ?";
        String insertSQL = """
            INSERT INTO dish(name, dish_type, price)
            VALUES (?, ?::dish_type, ?)
            returning id
        """;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkExistSQL);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                for (DishCreateDTO dto : newDishes) {
                    // Vérifier si le nom existe déjà dans la base
                    checkStmt.setString(1, dto.name());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            throw new IllegalArgumentException("Dish.name=" + dto.name()+ " already exists");
                        }
                    }

                    // Insert
                    insertStmt.setString(1, dto.name());
                    insertStmt.setString(2, dto.dishType().name());
                    insertStmt.setDouble(3, dto.price() != null ? dto.price() : 0.0);

                    try (ResultSet rs = insertStmt.executeQuery()) {
                        if (rs.next()) {
                            Dish dish = new Dish(
                                    rs.getInt("id"),           // id généré
                                    dto.name(),             // nom
                                    dto.dishType(),         // catégorie
                                    dto.price()
                            );
                            savedDishes.add(dish);
                        }
                    }
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }

        return savedDishes;
    }
}