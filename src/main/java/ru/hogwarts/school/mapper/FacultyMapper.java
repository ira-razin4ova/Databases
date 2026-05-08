package ru.hogwarts.school.mapper;

import org.mapstruct.*;
import ru.hogwarts.school.dto.faculty.CreateFacultyDto;
import ru.hogwarts.school.dto.faculty.FacultyDto;
import ru.hogwarts.school.dto.faculty.PatchFacultyDto;
import ru.hogwarts.school.model.Faculty;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    FacultyDto toDto (Faculty entity);

    @Mapping(target = "id", ignore = true)
    Faculty toEntity (CreateFacultyDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromPatchDto(PatchFacultyDto dto, @MappingTarget Faculty entity);

}
