package ru.hogwarts.school.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.StudentDTO;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.DataCodecService;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class StudentMapper {

    @Autowired
    protected DataCodecService dataCodecService;

    @Mapping(source = "faculty.name", target = "faculty")
    @Mapping(source = "avatar.id", target = "avatarId")
    @Mapping(source = "avatar.filePathPreview", target = "avatarPreviewPath")
    @Mapping(source = "studentTicket", target = "numberTicket")
    @Mapping(target = "numberPhone", ignore = true)
    public abstract StudentDTO toDto(Student entity);

    @AfterMapping
    protected void decodePhoneNumber(Student entity, @MappingTarget StudentDTO dto) {
        if (entity.getPhoneNumber() != null) {
            dto.setPhoneNumber(dataCodecService.decode(entity.getPhoneNumber()));
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    public abstract Student toEntity(CreateStudentDto dto);

    public abstract List<StudentDTO> toDtoList(List<Student> students);
}
