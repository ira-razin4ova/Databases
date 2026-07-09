package ru.hogwarts.school.balance.dto.batch;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record BatchCreateFacultyRequest(
        @NotNull(message = "Сумма начисления не может быть пустой")
        @Positive(message = "Сумма начисления должна быть строго больше нуля")
        BigDecimal amount,

        @NotNull(message = "ID задачи не может быть пустым")
        @Positive(message = "ID задачи должен быть больше нуля")
        Long taskId,

        @NotNull(message = "ID факультета не может быть пустым")
        @Positive(message = "ID факультета должен быть больше нуля")
        Long sourceFacultyId,

        @NotEmpty List<Long> facultyIds
        ) {
}
