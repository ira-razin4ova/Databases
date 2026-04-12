package ru.hogwarts.school.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<SchoolError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String message = String.format(
                "Invalid value '%s' for parameter '%s'",
                ex.getValue(),
                ex.getName()
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