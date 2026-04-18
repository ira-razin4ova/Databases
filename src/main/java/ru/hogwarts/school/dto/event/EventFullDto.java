package ru.hogwarts.school.dto.event;

import ru.hogwarts.school.dto.task.TaskDto;

import java.time.LocalDateTime;
import java.util.List;

public record EventFullDto(
        Long id,
        String title,
        LocalDateTime dateStart,
        LocalDateTime dateEnd,
        String description,
        Integer targetCourse,
        String faculty,
        Boolean archive,
        List<TaskDto> tasks
) {}
