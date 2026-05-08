package ru.hogwarts.school.dto.task;

import jakarta.validation.constraints.NotNull;
import ru.hogwarts.school.model.Quest;

public record CreateTaskDto(
        String title,
        Integer award,
        Boolean archive,
        Quest questId) {
}
