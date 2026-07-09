package ru.hogwarts.school.quest.dto;

public record CheckTaskFotThisQuestDto(
        boolean hasRelatedEntities,
        long count,
        String message
) {}