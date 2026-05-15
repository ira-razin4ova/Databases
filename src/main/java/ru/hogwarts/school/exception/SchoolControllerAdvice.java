package ru.hogwarts.school.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.hogwarts.school.constant.AppConstants;

import java.io.IOException;



@RestControllerAdvice
public class SchoolControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(SchoolControllerAdvice.class);

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

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity <SchoolError> handleMethodArgumentNotValidException (MethodArgumentNotValidException ex) {

        FieldError fieldError = ex.getBindingResult().getFieldError();

        String message = (fieldError != null)
                ? String.format(AppConstants.ExceptionMessages.VALIDATION_FIELD_ERROR_FORMAT,
        fieldError.getField(), fieldError.getDefaultMessage())
            : AppConstants.ExceptionMessages.VALIDATION_ERROR;

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
                : AppConstants.ExceptionMessages.UNKNOWN_TYPE;

        String message = String.format(
                AppConstants.ExceptionMessages.TYPE_MISMATCH_FORMAT,
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
                AppConstants.ExceptionMessages.FILE_ERROR
        );


        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SchoolError> handleAllExceptions(Exception ex) {

        logger.error("Unexpected error occurred: ", ex);

        SchoolError error = new SchoolError(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                AppConstants.ExceptionMessages.INTERNAL_SERVER_ERROR
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

}