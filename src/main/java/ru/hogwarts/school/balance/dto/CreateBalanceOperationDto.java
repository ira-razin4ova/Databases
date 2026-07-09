package ru.hogwarts.school.balance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateBalanceOperationDto(

        @NotNull(message = "Сумма начисления не может быть пустой")
        @Positive(message = "Сумма начисления должна быть строго больше нуля")
        BigDecimal amount,

        @NotNull(message = "ID задачи не может быть пустым")
        @Positive(message = "ID задачи должен быть больше нуля")
        Long taskId,

        @NotNull(message = "ID студента не может быть пустым")
        @Positive(message = "ID студента должен быть больше нуля")
        Long userId,

        @NotNull(message = "ID факультета не может быть пустым")
        @Positive(message = "ID факультета должен быть больше нуля")
        Long sourceFacultyId,


        @NotNull(message = "Тип операции не может быть пустым")
        String operationType) {
}