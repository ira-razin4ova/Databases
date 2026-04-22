package ru.hogwarts.school.dto.student;

import ru.hogwarts.school.constant.StudentStatus;

public record PatchStudentDto(
        String firstName,
        String lastName,
        Integer age,
        Long facultyId,
        StudentStatus studentStatus
)
{
}
