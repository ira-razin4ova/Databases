package ru.hogwarts.school.mapper;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.model.Avatar;

@Component
public class AvatarMapper {

    public AvatarDto toDto(Avatar avatar) {
        return new AvatarDto(
                avatar.getId(),
                avatar.getFilePath(),
                avatar.getFilePathPreview()
        );
    }
}
