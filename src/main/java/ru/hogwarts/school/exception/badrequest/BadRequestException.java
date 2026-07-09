package ru.hogwarts.school.exception.badrequest;

/**
 * Базовый класс для исключений, связанных с невалидными запросами от клиента.
 *
 * <p>Наследники:
 * <ul>
 *   <li>{@link ValidationException} — ошибка валидации данных</li>
 * </ul>
 *
 * @see ValidationException
 */
public abstract class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
