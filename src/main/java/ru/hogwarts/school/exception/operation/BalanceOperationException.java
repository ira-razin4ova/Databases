package ru.hogwarts.school.exception.operation;

import org.springframework.http.HttpStatus;
/**
 * Базовый класс для исключений, связанных с операциями над балансом пользователя
 * (начисления, списания, покупки мерча).
 *
 * <p>Каждое наследник определяет свой HTTP-статус через метод {@link #getStatus()},
 * что позволяет глобальному обработчику ошибок возвращать правильные коды ответов.
 *
 * <p>Наследники:
 * <ul>
 *   <li>{@link InsufficientFundsException} — недостаточно средств для операции (422)</li>
 *   <li>{@link OperationConflictException} — конфликт при одновременных операциях (409)</li>
 * </ul>
 *
 * @see InsufficientFundsException
 * @see OperationConflictException
 */

public abstract class BalanceOperationException extends RuntimeException {
    public BalanceOperationException(String message) {
        super(message);
    }

    /**
     * Возвращает HTTP-статус для данного типа исключения.
     * Используется глобальным обработчиком ошибок для формирования ответа.
     *
     * @return HTTP-статус
     */

    public abstract HttpStatus getStatus();
}
