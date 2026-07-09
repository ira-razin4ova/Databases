package ru.hogwarts.school.balance.dto.batch;

import java.math.BigDecimal;

public record BatchOperationResultDto(
        int updatedCount,
        BigDecimal totalAmount
) {}
