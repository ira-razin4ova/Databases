package ru.hogwarts.school.dto.task;

public record TaskDto (
         Long id,
         String title,
         Integer award,
         Boolean archive,
         Long questId)
{ }
