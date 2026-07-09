package ru.hogwarts.school.exception.operation;

import org.springframework.http.HttpStatus;

public class OperationConflictException extends BalanceOperationException {
    public OperationConflictException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
