package ru.hogwarts.school.exception.notfound;

import ru.hogwarts.school.constant.AppConstants;

/**
 * Базовый класс для исключений, связанных с отсутствием сущностей в базе данных.
 *
 * <p>Наследники:
 * <ul>
 *   <li>{@link EntityNotFoundException} — сущность не найдена по ID, email или другим критериям</li>
 * </ul>
 *
 * @see EntityNotFoundException
 */

public class EntityNotFoundException extends NotFoundException {
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format(AppConstants.NotFound.BY_ID, entityName, id));
    }

    public EntityNotFoundException(String childName, Long childId, String parentName, Long parentId) {
        super(String.format(
                AppConstants.NotFound.IN_PARENT,
                childName, childId, parentName, parentId
        ));
    }
    public EntityNotFoundException(String entityNamePlural, int expected, int actual) {
        super(String.format(
                AppConstants.NotFound.SEVERAL_ENTITIES,
                entityNamePlural, expected, actual
        ));
    }

    public EntityNotFoundException(String email) {
        super(String.format(
                AppConstants.NotFound.BY_EMAIL,
                email
        ));
    }
}
