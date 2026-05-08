package ru.hogwarts.school.dto.quest;

public record QuestShortDto(
        Long id,
        String title,
        String description,
        Boolean archive) {
}
