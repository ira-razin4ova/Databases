package ru.hogwarts.school.balance.dto.batch;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.hogwarts.school.constant.AppConstants;

import java.math.BigDecimal;
import java.util.List;

public record BatchConfirmRequestDto(
        @NotEmpty(message = AppConstants.Balance.EMPTY_LIST)
        List<@Valid PatchItem> items
) {
    public record PatchItem(
            @NotNull(message =  "ID операции обязателен")
            Long operationId,

            @Positive(message = "Сумма должна быть больше нуля")
            BigDecimal amount,

            @Positive(message = "ID задачи должен быть больше нуля")
            Long taskId
    )  {}
}
