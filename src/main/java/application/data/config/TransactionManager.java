package application.data.config;

import java.sql.Connection;
import java.sql.SQLException;

public final class TransactionManager {

    @FunctionalInterface
    public interface TransactionalOperation<R> {
        R run() throws SQLException;
    }

    private static final ThreadLocal<Connection> ACTIVE_CONNECTION = new ThreadLocal<>();

    private TransactionManager() {
    }

    public static Connection connectionForOperation() throws SQLException {
        Connection active = ACTIVE_CONNECTION.get();

        if (active != null) {
            return active;
        }
        return DatabaseConnection.getInstance().getConnection();
    }

    public static void release(Connection connection) throws SQLException {
        if (connection != null && connection != ACTIVE_CONNECTION.get()) {
            connection.close();
        }
    }

    public static boolean isTransactionActive() {
        return ACTIVE_CONNECTION.get() != null;
    }

    public static <R> R runInTransaction(TransactionalOperation<R> operation) throws SQLException {
        if (isTransactionActive()) {
            return operation.run();
        }

        Connection connection = DatabaseConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        ACTIVE_CONNECTION.set(connection);

        try {
            R result = operation.run();
            connection.commit();
            return result;

        } catch (RuntimeException | SQLException failure) {
            rollbackQuietly(connection);
            throw failure;

        } finally {
            ACTIVE_CONNECTION.remove();
            closeQuietly(connection);
        }
    }

    private static void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            ACTIVE_CONNECTION.remove();
        }
    }

    private static void closeQuietly(Connection connection) {
        try {
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException ignored) {
            ACTIVE_CONNECTION.remove();
        }
    }
}
