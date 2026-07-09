package ru.hogwarts.school.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
import java.util.stream.Collectors;


/**
 * Глобальный обработчик ошибок приложения.
 *
 * <p>Перехватывает все исключения и возвращает структурированные ответы
 * через {@link SchoolError} с правильными HTTP-статусами.
 *
 * <p>Обрабатываемые исключения:
 * <ul>
 *   <li>{@link AuthException} и наследники — ошибки авторизации (400, 403, 409)</li>
 *   <li>{@link BadCredentialsException} — неверные учетные данные (401)</li>
 *   <li>{@link BadRequestException} — невалидные запросы (400)</li>
 *   <li>{@link NotFoundException} — сущности не найдены (404)</li>
 *   <li>{@link BalanceOperationException} — ошибки операций с балансом (409, 422)</li>
 *   <li>{@link MethodArgumentNotValidException} — ошибки валидации DTO (400)</li>
 *   <li>{@link MethodArgumentTypeMismatchException} — неверный тип параметра (400)</li>
 *   <li>{@link ConstraintViolationException} — нарушения ограничений (400)</li>
 *   <li>{@link BindException} — ошибки привязки query-параметров (400)</li>
 *   <li>{@link HttpMessageNotReadableException} — невалидное тело запроса (400)</li>
 *   <li>{@link HttpRequestMethodNotSupportedException} — неподдерживаемый HTTP-метод (400)</li>
 *   <li>{@link MissingServletRequestParameterException} — отсутствует обязательный параметр (400)</li>
 *   <li>{@link IOException} — ошибки работы с файлами (500)</li>
 *   <li>{@link DataAccessException} — ошибки БД/Redis (500)</li>
 *   <li>{@link Exception} — все остальные исключения (500)</li>
 * </ul>
 *
 * <p>Все ошибки логируются с указанием HTTP-метода, URL и сообщения.
 *
 * @see SchoolError
 * @see AppConstants
 * @see AuthConstants
 */

@RestControllerAdvice
@Slf4j
public class SchoolControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(SchoolControllerAdvice.class);

    @ExceptionHandler (AuthException.class)
    public ResponseEntity <SchoolError>handleAuthException(AuthException ex, HttpServletRequest request) {
        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                ex.getStatus().value(),
                ex.getStatus().name(),
                method,
                url,
                ex.getMessage());

        SchoolError error = new SchoolError(
                ex.getStatus().name(),
                ex.getMessage()
        );
        return ResponseEntity
                .status(ex.getStatus())
                .body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<SchoolError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {


        SchoolError error = new SchoolError(
                HttpStatus.UNAUTHORIZED.name(),
                AuthConstants.Errors.BAD_CREDENTIALS
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<SchoolError> handleBadRequest(BadRequestException ex, HttpServletRequest request) {

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage()
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<SchoolError> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {

        SchoolError error = new SchoolError(
                HttpStatus.NOT_FOUND.name(),
                ex.getMessage()
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler (BalanceOperationException.class)
    public ResponseEntity <SchoolError> handlerBalanceOperationException (BalanceOperationException ex, HttpServletRequest request) {

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                ex.getStatus().value(),
                ex.getStatus().name(),
                method,
                url,
                ex.getMessage());

        SchoolError error = new SchoolError(
                ex.getStatus().name(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SchoolError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        FieldError fieldError = ex.getBindingResult().getFieldError();

        String message = (fieldError != null)
                ? String.format(AppConstants.Validation.VALIDATION_FIELD_ERROR_FORMAT,
                fieldError.getField(), fieldError.getDefaultMessage())
                : AppConstants.Validation.VALIDATION_ERROR;

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                message
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<SchoolError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

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
        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<SchoolError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {

        String message = ex.getConstraintViolations()
                .iterator()
                .next()
                .getMessage();

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                message
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity.badRequest().body(error);

    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<SchoolError> handleIOException(IOException e, HttpServletRequest request) {
        logger.error("IO Exception occurred: ", e);

        SchoolError error = new SchoolError(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                AppConstants.SystemErrors.FILE_ERROR
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                method,
                url,
                error.getMessage());


        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);

    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<SchoolError> handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
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
        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                method,
                url,
                errorResponse.getMessage());


        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<SchoolError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                AppConstants.Validation.MESSAGE_NOT_READABLE
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<SchoolError> handleMissingParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = String.format(
                AppConstants.Validation.MISSING_PARAMETER_FORMAT,
                ex.getParameterName()
        );

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                message
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(BindException.class)
    public ResponseEntity<SchoolError> handleBindException(BindException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format(AppConstants.Validation.VALIDATION_FIELD_ERROR_FORMAT,
                        error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                message
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<SchoolError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String supportedMethods = (ex.getSupportedHttpMethods() != null)
                ? ex.getSupportedHttpMethods().stream()
                .map(HttpMethod::name)
                .collect(Collectors.joining(", "))
                : AppConstants.Validation.UNKNOWN_TYPE;

        String message = String.format(
                AppConstants.Validation.METHOD_NOT_SUPPORTED_FORMAT,
                ex.getMethod(),
                supportedMethods
        );

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                message
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SchoolError> handleAllExceptions(Exception ex, HttpServletRequest request) {

        logger.error("Unexpected error occurred: ", ex);

        SchoolError error = new SchoolError(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                AppConstants.SystemErrors.INTERNAL_ERROR
        );

        String method = request.getMethod();
        String url = request.getRequestURL().toString();

        log.warn("Status: [{} {}] [{}] | [{}] -> {}",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                method,
                url,
                error.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

}