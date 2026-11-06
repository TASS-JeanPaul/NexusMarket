package application.data.repositories;

import application.data.config.TransactionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAnalyticsRepository {

    @FunctionalInterface
    protected interface RowMapper<R> {
        R map(ResultSet row) throws SQLException;
    }

    protected <R> List<R> query(String sql, RowMapper<R> mapper, Object... parameters) throws SQLException {
        List<R> rows = new ArrayList<>();
        Connection connection = TransactionManager.connectionForOperation();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < parameters.length; index++) {
                statement.setObject(index + 1, parameters[index]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapper.map(resultSet));
                }
            }
        } finally {
            TransactionManager.release(connection);
        }
        return rows;
    }

    protected LocalDateTime toDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
