package ru.hogwarts.school.quest.dto;

public record QuestShortDto(
        Long id,
        String title,
        String description,
        Boolean archive) {
}
