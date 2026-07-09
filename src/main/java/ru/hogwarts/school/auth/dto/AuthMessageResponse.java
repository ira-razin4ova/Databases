package ru.hogwarts.school.auth.dto;

import ru.hogwarts.school.auth.controller.AuthController;

/**
 * DTO для возврата текстового сообщения клиенту.
 *
 * <p>Используется в эндпоинте {@code POST /api/v1/auth/register}
 * ({@link AuthController#register(RegisterRequest)})
 * для подтверждения успешной отправки ссылки активации на email.
 *
 * <p>Пример ответа:
 * <pre>
 * {
 *   "message": "Ссылка для активации отправлена на ваш email"
 * }
 * </pre>
 *
 * @param message текстовое сообщение для клиента
 * @see AuthController#register(RegisterRequest)
 */
public record AuthMessageResponse(String message) {
}
