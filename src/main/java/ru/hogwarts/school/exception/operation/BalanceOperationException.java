package ru.hogwarts.school.exception.operation;

import org.springframework.http.HttpStatus;

public abstract class BalanceOperationException extends RuntimeException {
    public BalanceOperationException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();
}
