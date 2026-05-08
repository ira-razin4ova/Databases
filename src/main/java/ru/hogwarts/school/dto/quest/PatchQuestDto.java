package ru.hogwarts.school.dto.quest;


import ru.hogwarts.school.dto.task.PatchTaskDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для частичного обновления ивента.
 * Если поле равно null, оно не будет обновлено в базе.
 */
public record PatchQuestDto(
        String title,
        LocalDateTime dareStart,
        LocalDateTime dareEnd,
        Integer targetCourse,
        String description,
        Boolean archive,
        List<PatchTaskDto> tasks
) {}
