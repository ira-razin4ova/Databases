package ru.hogwarts.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.model.Avatar;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvatarMapper {

    AvatarDto toDto(Avatar avatar);

    @Mapping(source = "student.id", target = "studentId")

    List<AvatarDto> toDtoList(List<Avatar> avatars);

}
