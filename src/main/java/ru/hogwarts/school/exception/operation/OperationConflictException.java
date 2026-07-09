package ru.hogwarts.school.exception.operation;

import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое при конфликте операций с балансом
 * (например, одновременные попытки покупки одного мерча или race condition).
 *
 * <p>Возвращает HTTP-статус {@code 409 CONFLICT}.
 *
 * @see BalanceOperationException
 */
public class OperationConflictException extends BalanceOperationException {
    public OperationConflictException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}