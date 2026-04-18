package ru.hogwarts.school.dto.event;


import ru.hogwarts.school.dto.task.TaskDto;

import java.time.LocalDateTime;

import java.util.List;

public record CreateEventDto(

        String title,
        String description,
        LocalDateTime dateStart,
        LocalDateTime dateEnd,
        Boolean archive,
        List<TaskDto> tasks,
        Integer targetCourse)
{ }
