package ru.hogwarts.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
//@ExceptionHandler(IllegalArgumentException.class)
//public ResponseEntity<ExamError> handleIllegalArgumentException(IllegalArgumentException e) {
//    ExamError error = new ExamError("BAD_REQUEST", e.getMessage());
//
//    return ResponseEntity
//            .status(HttpStatus.BAD_REQUEST)
//            .body(error);
//}