package ru.hogwarts.school.dto.task;

import jakarta.validation.constraints.NotNull;

public record CreateTaskDto(
        String title,
        Integer award,
        @NotNull Long eventId,
        Boolean archive) {
}
