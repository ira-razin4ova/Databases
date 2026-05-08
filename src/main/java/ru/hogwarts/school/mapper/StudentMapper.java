package ru.hogwarts.school.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.PatchStudentDto;
import ru.hogwarts.school.dto.student.StudentDto;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.DataCodecService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AvatarMapper.class})
public abstract class StudentMapper {

    @Autowired
    protected DataCodecService dataCodecService;

    @Autowired
    protected AvatarMapper avatarMapper;

    @Mapping(source = "faculty.name", target = "faculty")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "studentTicket", target = "numberTicket")
    @Mapping(target = "phoneNumber", ignore = true)
    public abstract StudentDto toDto(Student entity);

    @AfterMapping
    protected void decodePhoneNumber(Student entity, @MappingTarget StudentDto dto) {
        if (entity.getPhoneNumber() != null) {
            dto.setPhoneNumber(dataCodecService.decode(entity.getPhoneNumber()));
        }
    }

    @AfterMapping
    protected void fillStudentId(@MappingTarget StudentDto dto, Student entity) {
        if (dto.getAvatar() != null && entity.getId() != null) {
            dto.getAvatar().setStudentId(entity.getId());
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    public abstract Student toEntity(CreateStudentDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Student updateEntityFromPatchDto(PatchStudentDto dto, @MappingTarget Student entity);

    public abstract List<StudentDto> toDtoList(List<Student> students);
}
