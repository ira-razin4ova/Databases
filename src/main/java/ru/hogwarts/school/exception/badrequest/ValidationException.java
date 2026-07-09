package ru.hogwarts.school.exception.badrequest;

public class ValidationException extends BadRequestException {
    public ValidationException(String message) {
        super(message);
    }
}
