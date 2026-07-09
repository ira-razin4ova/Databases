package ru.hogwarts.school.exception;

import ru.hogwarts.school.constant.AppConstants;
import ru.hogwarts.school.constant.AuthConstants;

import java.time.LocalDateTime;

/**
 * DTO для структурированного ответа об ошибке, возвращаемого клиенту.
 *
 * <p>Используется глобальным обработчиком ошибок ({@code @RestControllerAdvice})
 * для формирования единообразных ответов при возникновении исключений.
 *
 * <p>Структура ответа:
 * <ul>
 *   <li><b>code</b> — машинно-читаемый код ошибки (например, "INVALID_TOKEN",
 *       "INSUFFICIENT_FUNDS"). Используется фронтендом для логики обработки</li>
 *   <li><b>message</b> — человекочитаемое сообщение для пользователя
 *       (берется из {@link AppConstants} или {@link AuthConstants})</li>
 *   <li><b>timestamp</b> — время возникновения ошибки (для логирования и отладки)</li>
 * </ul>
 *
 * <p>Пример JSON-ответа:
 * <pre>
 * {
 *   "code": "INVALID_ACTIVATION_TOKEN",
 *   "message": "Ссылка для активации недействительна, либо её срок действия (24 часа) истек",
 *   "timestamp": "2026-07-10T14:30:45.123"
 * }
 * </pre>
 *
 * <p>Используется в:
 * <ul>
 *   <li>Глобальном обработчике ошибок ({@code @RestControllerAdvice})</li>
 *   <li>Всех эндпоинтах, которые могут выбрасывать исключения</li>
 * </ul>
 *
 * @see AppConstants
 * @see AuthConstants
 */
public class SchoolError {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    /**
     * Создает объект ошибки с указанным кодом и сообщением.
     * Timestamp устанавливается автоматически на момент создания.
     *
     * @param code    машинно-читаемый код ошибки (например, "INVALID_TOKEN")
     * @param message человекочитаемое сообщение для пользователя
     */
    public SchoolError(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

