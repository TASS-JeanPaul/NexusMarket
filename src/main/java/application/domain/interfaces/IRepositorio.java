package application.domain.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrato que debe cumplir cualquier repositorio del sistema.
 * La T es el tipo de la entidad con la que trabaja (Usuario, Producto, Pedido...).
 */
public interface IRepositorio<T> {

    List<T> listarTodos() throws SQLException;

    Optional<T> buscarPorId(Long id) throws SQLException;

    T guardar(T entidad) throws SQLException;

    boolean actualizar(T entidad) throws SQLException;

    boolean eliminar(Long id) throws SQLException;
}
