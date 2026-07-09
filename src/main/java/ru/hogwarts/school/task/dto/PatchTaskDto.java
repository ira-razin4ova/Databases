package ru.hogwarts.school.task.dto;

import java.math.BigDecimal;

public record PatchTaskDto(
        Long id,
        String title,
        BigDecimal award,
        Long questId,
        Boolean archive) {
}
