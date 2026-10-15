package application.data.repositories;

import application.data.config.DatabaseConnection;
import application.data.contracts.IRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T> implements IRepository<T> {
    protected abstract String getTableName();

    protected abstract T mapRow(ResultSet row) throws SQLException;

    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<T> findAll() throws SQLException {
        String sql = "SELECT * FROM " + getTableName();
        List<T> list = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapRow(resultSet));
            }
        }
        return list;
    }

    @Override
    public Optional<T> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    protected List<T> queryList(String sql, Object... parameters) throws SQLException {
        List<T> list = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapRow(resultSet));
                }
            }
        }
        return list;
    }

    protected Optional<T> queryOne(String sql, Object... parameters) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    protected Long insertAndGetId(String sql, Object... parameters) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, parameters);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong("id");
                }
            }
        }
        throw new SQLException("The insertion into " + getTableName() + " did not return a generated identifier");
    }

    protected boolean executeUpdate(String sql, Object... parameters) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);
            return statement.executeUpdate() > 0;
        }
    }

    protected LocalDateTime toDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }

    protected Long toIdentifier(ResultSet row, String column) throws SQLException {
        long value = row.getLong(column);
        return row.wasNull() ? null : value;
    }

    private void bind(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int index = 0; index < parameters.length; index++) {
            statement.setObject(index + 1, parameters[index]);
        }
    }
}
