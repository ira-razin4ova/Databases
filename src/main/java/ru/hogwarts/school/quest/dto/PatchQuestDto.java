package ru.hogwarts.school.quest.dto;


import ru.hogwarts.school.task.dto.PatchTaskDto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO для частичного обновления ивента.
 * Если поле равно null, оно не будет обновлено в базе.
 */
public record PatchQuestDto(
        String title,
        LocalDate dareStart,
        LocalDate dareEnd,
        Integer targetCourse,
        String description,
        Boolean archive,
        List<PatchTaskDto> tasks
) {}
