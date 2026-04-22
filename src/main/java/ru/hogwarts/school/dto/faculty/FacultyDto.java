package ru.hogwarts.school.dto.faculty;

import java.math.BigDecimal;

public record FacultyDto(
        Long id,
        String name,
        String color,
        BigDecimal balance) {
}