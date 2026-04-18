package ru.hogwarts.school.dto.event;

public record EventShortDto(
        Long id,
        String title,
        String description,
        Boolean archive) {
}
