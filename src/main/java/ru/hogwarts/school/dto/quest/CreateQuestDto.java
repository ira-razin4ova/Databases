package ru.hogwarts.school.dto.quest;


import ru.hogwarts.school.dto.task.CreateTaskDto;

import java.time.LocalDateTime;

import java.util.List;

public record CreateQuestDto(

        String title,
        String description,
        LocalDateTime dateStart,
        LocalDateTime dateEnd,
        Boolean archive,
        List <CreateTaskDto> tasks,
        Integer targetCourse)
{ }
