package ru.hogwarts.school.quest.dto;

import java.time.LocalDate;
import java.util.List;

public record QuestDto(

        Long id,
        String title,
        String description,
        LocalDate dateStart,
        LocalDate dateEnd,
        Boolean archive,
        List<String> tasksTitle,
        Integer targetCourse)
{ }
