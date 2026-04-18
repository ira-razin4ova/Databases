package ru.hogwarts.school.mapper;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.faculty.FacultyDto;
import ru.hogwarts.school.model.Faculty;

@Component
public class FacultyMapper {
    public FacultyDto toDto(Faculty faculty) {
        return new FacultyDto(
                faculty.getId(),
                faculty.getName(),
                faculty.getColor()
        );
    }
    public Faculty toEntity(Faculty dto) {
        Faculty faculty = new Faculty();
        faculty.setName(dto.getName());
        faculty.setColor(dto.getColor());
        return faculty;
    }
}
