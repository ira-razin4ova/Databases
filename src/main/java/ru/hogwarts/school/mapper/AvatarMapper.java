package ru.hogwarts.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.model.Avatar;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvatarMapper {

    @Mapping(source = "student.id", target = "studentId")
    AvatarDto toDto(Avatar avatar);
    
    List<AvatarDto> toDtoList(List<Avatar> avatars);



}
