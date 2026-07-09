package ru.hogwarts.school.constant;

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
