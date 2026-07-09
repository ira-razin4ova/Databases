package ru.hogwarts.school.balance.dto.batch;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BatchDeleteRequestDto(@NotEmpty List<DeleteItem> items) {
    public record DeleteItem(Long operationId) {}
}
