package application.data.repositories;

import application.data.contracts.ICategoryRepository;
import application.data.entities.Category;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class CategoryRepository extends BaseRepository<Category> implements ICategoryRepository {

    @Override
    protected String getTableName() {
        return "category";
    }

    @Override
    protected Category mapRow(ResultSet row) throws SQLException {
        Category category = new Category();
        category.setId(row.getLong("id"));
        category.setName(row.getString("name"));
        category.setDescription(row.getString("description"));
        return category;
    }

    @Override
    public Category save(Category entity) throws SQLException {
        String sql = "INSERT INTO category (name, description) VALUES (?, ?)";

        entity.setId(insertAndGetId(sql, entity.getName(), entity.getDescription()));
        return entity;
    }

    @Override
    public boolean update(Category entity) throws SQLException {
        return executeUpdate("UPDATE category SET name = ?, description = ? WHERE id = ?",
                entity.getName(),
                entity.getDescription(),
                entity.getId());
    }

    @Override
    public Optional<Category> findByName(String name) throws SQLException {
        return queryOne("SELECT * FROM category WHERE name = ?", name);
    }
}
