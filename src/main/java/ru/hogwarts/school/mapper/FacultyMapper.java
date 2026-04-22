package ru.hogwarts.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.faculty.CreateFacultyDto;
import ru.hogwarts.school.dto.faculty.FacultyDto;
import ru.hogwarts.school.dto.faculty.PatchFacultyDto;
import ru.hogwarts.school.dto.task.TaskPatchDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Task;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    FacultyDto toDto (Faculty entity);

    @Mapping(target = "id", ignore = true)
    Faculty toEntity (CreateFacultyDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromPatchDto(PatchFacultyDto dto, @MappingTarget Faculty entity);

}
