package ru.hogwarts.school.balance.dto;

import ru.hogwarts.school.faculty.dto.FacultyDto;
import ru.hogwarts.school.user.dto.UserDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalanceOperationDto(
        Long id,
        BigDecimal amount,
        Long taskId,
        UserDto userDto,
        FacultyDto sourceFaculty,
        String operationType,
        LocalDateTime createdAt,
        String description,
        String operationStatus) {
}
