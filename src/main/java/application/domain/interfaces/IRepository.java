package application.domain.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    List<T> findAll() throws SQLException;

    Optional<T> findById(Long id) throws SQLException;

    T save(T entity) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean delete(Long id) throws SQLException;
}
