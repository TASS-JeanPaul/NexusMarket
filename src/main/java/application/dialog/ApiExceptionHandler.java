package application.dialog;

import application.dialog.responses.ErrorResponse;
import application.shared.exceptions.BusinessRuleException;
import application.shared.exceptions.DataAccessException;
import application.shared.exceptions.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException exception) {
        return respond(HttpStatus.BAD_REQUEST, "BUSINESS_RULE_VIOLATED", exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
        return respond(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException exception) {
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "DATA_ACCESS_FAILURE", exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> respond(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), error, message));
    }
}
