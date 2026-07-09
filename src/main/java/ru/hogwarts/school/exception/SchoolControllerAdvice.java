package ru.hogwarts.school.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.hogwarts.school.constant.AppConstants;
import ru.hogwarts.school.constant.AuthConstants;
import ru.hogwarts.school.exception.authention.AuthException;
import ru.hogwarts.school.exception.badrequest.BadRequestException;
import ru.hogwarts.school.exception.notfound.NotFoundException;
import ru.hogwarts.school.exception.operation.BalanceOperationException;

import java.io.IOException;


@RestControllerAdvice
@Slf4j
public class SchoolControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(SchoolControllerAdvice.class);

    @ExceptionHandler (AuthException.class)
    public ResponseEntity <SchoolError>handleAuthException(AuthException ex) {

        SchoolError error = new SchoolError(
                ex.getStatus().name(),
                ex.getMessage()
        );
        return ResponseEntity
                .status(ex.getStatus())
                .body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<SchoolError> handleBadCredentials(BadCredentialsException ex) {

        SchoolError error = new SchoolError(
                HttpStatus.UNAUTHORIZED.name(),
                AuthConstants.Errors.BAD_CREDENTIALS
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<SchoolError> handleBadRequest(BadRequestException ex) {

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<SchoolError> handleNotFoundException(NotFoundException ex) {

        SchoolError error = new SchoolError(
                HttpStatus.NOT_FOUND.name(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler (BalanceOperationException.class)
    public ResponseEntity <SchoolError> handlerBalanceOperationException (BalanceOperationException ex) {

        SchoolError error = new SchoolError(
                ex.getStatus().name(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SchoolError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        FieldError fieldError = ex.getBindingResult().getFieldError();

        String message = (fieldError != null)
                ? String.format(AppConstants.Validation.VALIDATION_FIELD_ERROR_FORMAT,
                fieldError.getField(), fieldError.getDefaultMessage())
                : AppConstants.Validation.VALIDATION_ERROR;

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                message
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<SchoolError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String requiredType = (ex.getRequiredType() != null)
                ? ex.getRequiredType().getSimpleName()
                : AppConstants.Validation.UNKNOWN_TYPE;

        String message = String.format(
                AppConstants.Validation.TYPE_MISMATCH_FORMAT,
                ex.getName(),
                ex.getValue(),
                requiredType
        );

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                message
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<SchoolError> handleConstraintViolation(ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .iterator()
                .next()
                .getMessage();

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                message
        );

        return ResponseEntity.badRequest().body(error);

    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<SchoolError> handleIOException(IOException e) {
        logger.error("IO Exception occurred: ", e);

        SchoolError error = new SchoolError(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                AppConstants.SystemErrors.FILE_ERROR
        );


        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);

    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<SchoolError> handleDataAccessException(DataAccessException ex) {
        String errorClass = ex.getClass().getName();
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "";
        if (errorClass.contains("Redis") || errorClass.contains("redis") || errorMessage.contains("Redis")) {
            log.error("🚨 Редис не отвечает", ex);
        } else {
            log.error("🚨 Постгрес не отвечает", ex);
        }
        SchoolError errorResponse = new SchoolError(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                AppConstants.SystemErrors.INTERNAL_ERROR_MSG
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SchoolError> handleAllExceptions(Exception ex) {

        logger.error("Unexpected error occurred: ", ex);

        SchoolError error = new SchoolError(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                AppConstants.SystemErrors.INTERNAL_ERROR
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

}
//BindException (Когда ломаются Query-параметры, собранные в объект)
// HttpMessageNotReadableException
// HttpRequestMethodNotSupportedException
// MissingServletRequestParameterException