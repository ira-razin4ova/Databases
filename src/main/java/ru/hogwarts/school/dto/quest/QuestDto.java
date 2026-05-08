package ru.hogwarts.school.dto.quest;


import java.time.LocalDateTime;
import java.util.List;

public record QuestDto(

        Long id,
        String title,
        String description,
        LocalDateTime dateStart,
        LocalDateTime dateEnd,
        Boolean archive,
        List<String> tasksTitle,
        Integer targetCourse)
{ }
