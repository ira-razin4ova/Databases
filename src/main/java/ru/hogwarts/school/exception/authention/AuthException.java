package ru.hogwarts.school.exception.authention;

import org.springframework.http.HttpStatus;

/**
 * Базовый класс для всех исключений, связанных с аутентификацией и авторизацией.
 *
 * <p>Каждое наследник определяет свой HTTP-статус через метод {@link #getStatus()},
 * что позволяет глобальному обработчику ошибок возвращать правильные коды ответов.
 *
 * <p>Наследники:
 * <ul>
 *   <li>{@link InvalidActivationTokenException} — невалидный токен активации (400)</li>
 *   <li>{@link InvalidUserStatusException} — пользователь не может войти из-за статуса (403)</li>
 *   <li>{@link UserAlreadyActivatedException} — повторная попытка активации (409)</li>
 * </ul>
 *
 * @see InvalidActivationTokenException
 * @see InvalidUserStatusException
 * @see UserAlreadyActivatedException
 */

public abstract class AuthException extends RuntimeException {
    public AuthException(String message) {
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
