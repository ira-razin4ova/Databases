package ru.hogwarts.school.mapper;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.student.StudentDTO;
import ru.hogwarts.school.model.Student;

@Component
public class StudentMapper {
    public StudentDTO mapToDto(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getAge(),
                student.getFirstName(),
                student.getLastName(),
                student.getFaculty() != null ? student.getFaculty().getName() : null,
                student.getAvatar() != null ? student.getAvatar().getId() : null,
                student.getAvatar() != null ? student.getAvatar().getFilePathPreview() : null,
                student.getStudentStatus()
        );
    }
}
