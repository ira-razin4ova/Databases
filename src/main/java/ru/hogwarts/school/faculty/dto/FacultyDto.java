package ru.hogwarts.school.faculty.dto;

import java.math.BigDecimal;

public record FacultyDto(
        Long id,
        String name,
        String color,
        BigDecimal balance) {
}