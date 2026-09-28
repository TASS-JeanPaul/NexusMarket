package application.services;

import application.domain.exceptions.BusinessRuleException;
import application.domain.exceptions.DataAccessException;
import application.domain.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseService {

    @FunctionalInterface
    protected interface DatabaseOperation<R> {
        R run() throws SQLException;
    }

    protected <R> R execute(DatabaseOperation<R> operation) {
        try {
            return operation.run();
        } catch (SQLException exception) {
            throw new DataAccessException("The operation could not be completed in the database", exception);
        }
    }

    protected <E> E findOrFail(Optional<E> candidate, String entityName, Long id) {
        return candidate.orElseThrow(() ->
                new ResourceNotFoundException(entityName + " with identifier " + id + " does not exist"));
    }

    protected void require(boolean condition, String message) {
        if (!condition) {
            throw new BusinessRuleException(message);
        }
    }

    protected void requireText(String value, String fieldName) {
        require(value != null && !value.isBlank(), fieldName + " is required");
    }

    protected void requireIdentifier(Long value, String fieldName) {
        require(value != null, fieldName + " is required");
    }

    protected void requirePositive(int value, String fieldName) {
        require(value > 0, fieldName + " must be greater than zero");
    }

    protected void requireNotNegative(int value, String fieldName) {
        require(value >= 0, fieldName + " cannot be negative");
    }

    protected void requirePositiveAmount(BigDecimal value, String fieldName) {
        require(value != null && value.compareTo(BigDecimal.ZERO) > 0, fieldName + " must be greater than zero");
    }

    protected String generateDocumentNumber(String prefix) {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String sequence = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + date + "-" + sequence;
    }
}
