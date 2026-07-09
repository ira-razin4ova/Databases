package ru.hogwarts.school.avatar;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.hogwarts.school.avatar.dto.AvatarDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvatarMapper {

    @Mapping(source = "student.id", target = "studentId")
    AvatarDto toDto(Avatar avatar);
    
    List<AvatarDto> toDtoList(List<Avatar> avatars);



}
