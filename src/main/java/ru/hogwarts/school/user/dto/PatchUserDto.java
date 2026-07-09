package ru.hogwarts.school.user.dto;

import ru.hogwarts.school.user.enums.Gender;
import ru.hogwarts.school.user.enums.Status;

import java.time.LocalDate;

public record PatchUserDto(
        String firstName,
        String lastName,
        LocalDate birthDate,
        Gender gender,
        Long facultyId,
        Status status,
        String phoneNumber,
        Integer course,
        String studentTicket
) {
}
