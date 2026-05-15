package ru.hogwarts.school.constant;

public final class AppConstants {

    private AppConstants () {}

    public static final class ExceptionMessages {
        public static final String ENTITY_NOT_FOUND_FORMAT = "%s с id %d не найден";
        public static final String ENTITY_NOT_FOUND_IN_PARENT_FORMAT = "%s с id %d не найден в объекте %s с id %d";
        public static final String SEVERAL_ENTITIES_NOT_FOUND_FORMAT = "Некоторые %s не найдены. Ожидалось: %d, найдено: %d";

        public static final String VALIDATION_ERROR = "Ошибка валидации";

        public static final String INTERNAL_SERVER_ERROR = "Something went wrong";

        public static final String FILE_ERROR = "Ошибка при обработке файла";

        public static final String INVALID_FACULTY_DATA = "Информация о факультете отсутствует или указана неверно";

        public static final String VALIDATION_FIELD_ERROR_FORMAT = "%s: %s";

        public static final String TYPE_MISMATCH_FORMAT = "Параметр '%s' имеет неверное значение '%s'. Ожидаемый тип: %s";

        public static final String UNKNOWN_TYPE = "unknown";
    }

}
