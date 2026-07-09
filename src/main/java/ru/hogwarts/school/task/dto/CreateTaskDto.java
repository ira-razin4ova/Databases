package ru.hogwarts.school.task.dto;

import java.math.BigDecimal;

public record CreateTaskDto(
        String title,
        BigDecimal award,
        Long questId) {
}
