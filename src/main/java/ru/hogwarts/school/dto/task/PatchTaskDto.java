package ru.hogwarts.school.dto.task;

public record PatchTaskDto(
        Long id,
        String title,
        String description,
        Integer award,
        Long questId,
        Boolean archive) {
}
