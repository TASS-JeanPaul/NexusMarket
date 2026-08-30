package application.repositories;

import application.config.ConexionBD;
import application.domain.interfaces.IRepositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Resuelve las operaciones que son iguales para todas las tablas.
 * Cada repositorio concreto solo tiene que decir sobre que tabla trabaja
 * y como convertir una fila en un objeto.
 *
 * Todas las consultas piden su propia conexion y la cierran al terminar,
 * asi la aplicacion puede atender a varias personas al mismo tiempo.
 */
public abstract class RepositorioBase<T> implements IRepositorio<T> {

    protected abstract String getNombreTabla();

    protected abstract T mapear(ResultSet fila) throws SQLException;

    protected Connection getConexion() throws SQLException {
        return ConexionBD.getInstancia().getConexion();
    }

    @Override
    public List<T> listarTodos() throws SQLException {
        String sql = "SELECT * FROM " + getNombreTabla();
        List<T> lista = new ArrayList<>();

        try (Connection conexion = getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                lista.add(mapear(resultado));
            }
        }
        return lista;
    }

    @Override
    public Optional<T> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM " + getNombreTabla() + " WHERE id = ?";

        try (Connection conexion = getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setLong(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(mapear(resultado));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean eliminar(Long id) throws SQLException {
        String sql = "DELETE FROM " + getNombreTabla() + " WHERE id = ?";

        try (Connection conexion = getConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setLong(1, id);
            return sentencia.executeUpdate() > 0;
        }
    }
}
