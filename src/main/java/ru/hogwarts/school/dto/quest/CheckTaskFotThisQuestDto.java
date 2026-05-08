package ru.hogwarts.school.dto.quest;

public record CheckTaskFotThisQuestDto(
        boolean hasRelatedEntities,
        long count,
        String message
) {}