package ru.hogwarts.school.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class SchoolControllerAdvice {

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

    @ExceptionHandler(IllegalAccessError.class)
    public ResponseEntity<SchoolError> handleIllegalAccessError(IllegalAccessError e) {

        SchoolError error = new SchoolError(HttpStatus.BAD_REQUEST.name(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity <SchoolError> handleMethodArgumentNotValidException (MethodArgumentNotValidException ex) {

        FieldError fieldError = ex.getBindingResult().getFieldError();

        String message = (fieldError != null)
                ? String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage())
                : "Ошибка валидации";

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
                : "unknown";

        String message = String.format(
                "Параметр '%s' имеет неверное значение '%s'. Ожидаемый тип: %s",
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
    public ResponseEntity<String> handleIOException(IOException e) {
        e.printStackTrace(); // для себя выводим лог в консоль

        // А пользователю отдаем вежливое общее сообщение
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка при обработке файла на сервере. Обратитесь к администратору.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SchoolError> handleAllExceptions(Exception ex) {

        //log.error("Unexpected error", ex);

        SchoolError error = new SchoolError(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Something went wrong"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

}