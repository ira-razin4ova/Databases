package ru.hogwarts.school.quest.dto;
import ru.hogwarts.school.task.dto.CreateTaskDto;

import java.time.LocalDate;

import java.util.List;

public record CreateQuestDto(

        String title,
        String description,
        LocalDate dateStart,
        LocalDate dateEnd,
        List <CreateTaskDto> tasks,
        Integer targetCourse)
{ }
