package ru.hogwarts.school.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "Поисковый запрос не должен быть пустым")
        String searchInput
) {
    public UserRequest {
        if (searchInput != null) {
            searchInput = searchInput.trim();
        }
    }
}
