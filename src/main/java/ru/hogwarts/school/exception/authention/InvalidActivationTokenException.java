package ru.hogwarts.school.exception.authention;

import org.springframework.http.HttpStatus;

public class InvalidActivationTokenException extends AuthException {
    public InvalidActivationTokenException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
