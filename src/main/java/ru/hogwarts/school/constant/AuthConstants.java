package ru.hogwarts.school.constant;
/**
 * Централизованное хранилище констант для модуля аутентификации и авторизации.
 *
 * <p>Константы разделены по <b>аудиториям</b>, что упрощает поддержку и локализацию:
 * <ul>
 *   <li>{@link Success} — успешные ответы, которые отправляются клиенту (на фронтенд)</li>
 *   <li>{@link Errors} — сообщения об ошибках, которые видит пользователь на экране</li>
 *   <li>{@link Logs} — сообщения для логов сервера (для разработчиков, с плейсхолдерами)</li>
 * </ul>
 *
 * <p>Такое разделение позволяет:
 * <ul>
 *   <li>Менять логи без влияния на пользовательские сообщения</li>
 *   <li>Локализовать UI-тексты независимо от технических логов</li>
 *   <li>Быстро находить нужное сообщение по контексту</li>
 * </ul>
 */

public class AuthConstants {
    private AuthConstants() {}

    /**
     * Успешные ответы, которые мы отправляем КЛИЕНТУ (на фронтенд)
     */
    public static final class Success {
        private Success() {}

        public static final String ACTIVATION_LINK_SENT = "Ссылка для активации успешно отправлена на ваш email.";
    }

    /**
     * Ошибки, которые видит ПОЛЬЗОВАТЕЛЬ на экране
     */
    public static final class Errors {
        private Errors() {}

        public static final String INVALID_OR_EXPIRED_TOKEN = "Ссылка для активации недействительна, либо её срок действия (24 часа) истек";
        public static final String UNAUTHORIZED = "Ошибка аутентификации: токен отсутствует или недействителен";
        public static final String ACCESS_DENIED = "Доступ запрещен: у вас недостаточно прав для выполнения этого действия";
        public static final String BAD_CREDENTIALS = "Неверный email или пароль";
        public static final String USER_ALREADY_ACTIVATED = "Пользователь с email %s уже активирован. Пожалуйста, выполните вход.";
        public static final String INVALID_USER_STATUS = "Действие невозможно: текущий статус пользователя (%s) ограничивает доступ к этой операции.";
    }

    /**
     * То, что пишем в лог сервера (для разработчиков), скрываем детальную информацию об ошибке в целях безопасности.
     */
    public static final class Logs {
        private Logs() {}

        public static final String JWT_MALFORMED = "Некорректный формат JWT токена: {}";
        public static final String JWT_SIGNATURE_INVALID = "Невалидная цифровая подпись токена: {}";
        public static final String JWT_EXPIRED = "Срок действия JWT токена истек: {}";
        public static final String JWT_UNSUPPORTED = "JWT токен не поддерживается: {}";
        public static final String JWT_EMPTY = "Строка JWT пустая или содержит только пробелы: {}";
        public static final String INVALID_USER_ID = "В токене пришел кривой ID пользователя: {}";
        public static final String UNEXPECTED_AUTH_ERROR = "Непредвиденная ошибка при аутентификации: {}";
    }
}
