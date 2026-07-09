package ru.hogwarts.school.quest.dto;

import ru.hogwarts.school.task.dto.TaskDto;

import java.time.LocalDate;
import java.util.List;

public record QuestFullDto(
        Long id,
        String title,
        LocalDate dateStart,
        LocalDate dateEnd,
        String description,
        Integer targetCourse,
        String faculty,
        Boolean archive,
        List<TaskDto> tasks
) {}
