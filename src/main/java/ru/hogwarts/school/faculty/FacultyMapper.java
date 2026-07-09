package ru.hogwarts.school.faculty;

import org.mapstruct.*;
import ru.hogwarts.school.faculty.dto.CreateFacultyDto;
import ru.hogwarts.school.faculty.dto.FacultyDto;
import ru.hogwarts.school.faculty.dto.PatchFacultyDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    FacultyDto toDto (Faculty entity);

    @Mapping(target = "id", ignore = true)
    Faculty toEntity (CreateFacultyDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromPatchDto(PatchFacultyDto dto, @MappingTarget Faculty entity);

    List <FacultyDto> toDtoList (List <Faculty> faculties);

}
