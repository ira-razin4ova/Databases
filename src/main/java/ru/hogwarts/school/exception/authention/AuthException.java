package ru.hogwarts.school.exception.authention;

import org.springframework.http.HttpStatus;

public abstract class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
    public abstract HttpStatus getStatus();
}
