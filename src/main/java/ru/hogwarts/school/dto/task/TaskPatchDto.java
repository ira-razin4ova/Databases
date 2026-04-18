package ru.hogwarts.school.dto.task;

public record TaskPatchDto(
        Long id,
        Long eventId,
        String title,
        String description,
        Integer award) {
}
