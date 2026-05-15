package ru.hogwarts.school.exception;

import ru.hogwarts.school.constant.AppConstants;

public class EntityNotFoundException extends NotFoundException {
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format(AppConstants.ExceptionMessages.ENTITY_NOT_FOUND_FORMAT, entityName, id));
    }

    public EntityNotFoundException(String childName, Long childId, String parentName, Long parentId) {
        super(String.format(
                AppConstants.ExceptionMessages.ENTITY_NOT_FOUND_IN_PARENT_FORMAT,
                childName, childId, parentName, parentId
        ));
    }
    public EntityNotFoundException(String entityNamePlural, int expected, int actual) {
        super(String.format(
                AppConstants.ExceptionMessages.SEVERAL_ENTITIES_NOT_FOUND_FORMAT,
                entityNamePlural, expected, actual
        ));
    }
}
