package ru.hogwarts.school.balance.dto.batch;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.hogwarts.school.constant.AppConstants;

import java.util.List;

public record BatchCancelRequestDto(
        @NotEmpty(message = AppConstants.Balance.EMPTY_LIST)
        List<@Valid CancelItem> items
) {
    public record CancelItem(
            @NotNull(message = "ID операции обязателен")
            Long operationId){
    }
}
