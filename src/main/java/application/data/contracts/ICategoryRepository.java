package application.data.contracts;

import application.data.entities.Category;

import java.sql.SQLException;
import java.util.Optional;

public interface ICategoryRepository extends IRepository<Category> {
    Optional<Category> findByName(String name) throws SQLException;
}
