package ru.hogwarts.school.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class SchoolControllerAdvice {

    @ExceptionHandler(FacultyNotFound.class)
    public ResponseEntity<SchoolError> handleFacultyNotFound(FacultyNotFound e) {

        SchoolError error = new SchoolError(
                HttpStatus.NOT_FOUND.name(),
                e.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(StudentNotFound.class)
    public ResponseEntity<SchoolError> handleStudentNotFound(StudentNotFound e) {

        SchoolError error = new SchoolError(
                HttpStatus.NOT_FOUND.name(),
                e.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(IllegalAccessError.class)
    public ResponseEntity<SchoolError> handleIllegalAccessError(IllegalAccessError e) {

        SchoolError error = new SchoolError(
                HttpStatus.BAD_REQUEST.name(),
                e.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Ошибка валидации: " + ex.getMessage());
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        // Мы можем вывести детали в консоль (лог), чтобы программист (ты) знала, что случилось
        e.printStackTrace();

        // А пользователю отдаем вежливое общее сообщение
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла ошибка при обработке файла на сервере. Обратитесь к администратору.");
    }

    @ExceptionHandler (AvatarException.class)
    public ResponseEntity<SchoolError> handleAvatarException(AvatarException e) {
        SchoolError error= new SchoolError(HttpStatus.NOT_FOUND.name(),
                e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}