package ru.hogwarts.school.exception;

public class FacultyNotFound extends RuntimeException {
    public FacultyNotFound(String message) {
        super(message);
    }
}
