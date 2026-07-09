package ru.hogwarts.school.constant;

/**
 * Централизованное хранилище констант приложения.
 *
 * <p>Все текстовые сообщения вынесены сюда, чтобы:
 * <ul>
 *   <li>Избежать дублирования строк в коде</li>
 *   <li>Упростить локализацию в будущем</li>
 *   <li>Облегчить поиск и изменение текстов</li>
 * </ul>
 *
 * <p>Структура:
 * <ul>
 *   <li>{@link Validation} — сообщения об ошибках валидации</li>
 *   <li>{@link Balance} — сообщения, связанные с операциями над балансом</li>
 *   <li>{@link SystemErrors} — общие системные ошибки</li>
 *   <li>{@link NotFound} — шаблоны сообщений "не найдено"</li>
 * </ul>
 *
 * <p>Конструктор приватный — класс не предназначен для инстанцирования.
 */

public final class AppConstants {

    private AppConstants() {
    }

    public static final class Validation {
        public static final String VALIDATION_ERROR = "Ошибка валидации";
        public static final String INVALID_FACULTY_DATA = "Информация о факультете отсутствует или указана неверно";
        public static final String VALIDATION_FIELD_ERROR_FORMAT = "%s: %s";
        public static final String UNKNOWN_TYPE = "unknown";
        public static final String TYPE_MISMATCH_FORMAT = "Параметр '%s' имеет неверное значение '%s'. Ожидаемый тип: %s";
        public static final String PHONE_MUST_START_WITH_79 = "Номер должен начинаться с 79";
        public static final String PHONE_INVALID = "Номер телефона не корректен";
        public static final String BIND_ERROR_FORMAT = "Ошибка привязки параметров: %s";
        public static final String MESSAGE_NOT_READABLE = "Невозможно прочитать тело запроса. Проверьте формат JSON";
        public static final String METHOD_NOT_SUPPORTED_FORMAT = "Метод '%s' не поддерживается для этого URL. Поддерживаемые: %s";
        public static final String MISSING_PARAMETER_FORMAT = "Обязательный параметр '%s' отсутствует в запросе";
    }


    public static final class Balance {
        public static final String DELETE_CONFLICT_FORMAT = "Нельзя удалить операцию со статусом %s";
        public static final String CONFIRM_CONFLICT_FORMAT = "Нельзя подтвердить операцию со статусом %s";
        public static final String CANCEL_CONFLICT_FORMAT = "Нельзя отменить операцию со статусом %s";
        public static final String TYPE_CONFLICT_FORMAT = "Операция имеет неподходящий тип: %s";

        public static final String EMPTY_LIST = "Список операций не может быть пустым";
        public static final String INSUFFICIENT_FUNDS_FORMAT = "Недостаточно средств для совершения операции. Требуется: %s, доступно: %s";
    }

    public static final class SystemErrors {
        public static final String INTERNAL_ERROR = "Something went wrong";
        public static final String INTERNAL_ERROR_MSG = "На сервере произошла ошибка. Пожалуйста, попробуйте позже.";
        public static final String FILE_ERROR = "Ошибка при обработке файла";

    }

    public static final class NotFound {
        public static final String BY_ID = "%s с id %d не найден";
        public static final String BY_EMAIL = "Пользователь с email %s не найден";
        public static final String IN_PARENT = "%s с id %d не найден в объекте %s с id %d";
        public static final String SEVERAL_ENTITIES = "Некоторые %s не найдены. Ожидалось: %d, найдено: %d";
    }
}
