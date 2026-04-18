package ru.hogwarts.school.dto.event;

public record DeleteWarningDTO(
        boolean hasRelatedEntities,
        long count,
        String message
) {}
