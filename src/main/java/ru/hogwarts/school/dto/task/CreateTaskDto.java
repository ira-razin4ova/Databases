package ru.hogwarts.school.dto.task;

public record CreateTaskDto(
        String title,
        Integer award,
        Long eventId,
        Boolean archive) {
}
