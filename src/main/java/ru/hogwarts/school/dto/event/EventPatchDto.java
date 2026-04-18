package ru.hogwarts.school.dto.event;


import ru.hogwarts.school.dto.task.TaskPatchDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для частичного обновления ивента.
 * Если поле равно null, оно не будет обновлено в базе.
 */
public record EventPatchDto(
        String title,
        LocalDateTime dareStart,
        LocalDateTime dareEnd,
        Integer targetCourse,
        String description,
        Boolean archive,
        List<TaskPatchDto> tasks
) {}
