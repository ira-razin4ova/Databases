package ru.hogwarts.school.user;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import ru.hogwarts.school.avatar.AvatarMapper;
import ru.hogwarts.school.user.dto.CreateUserDto;
import ru.hogwarts.school.user.dto.PatchUserDto;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.util.DataCodecService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AvatarMapper.class})
public abstract class UserMapper {

    @Autowired
    protected DataCodecService dataCodecService;

    @Autowired
    protected AvatarMapper avatarMapper;

    @Autowired
    protected MessageSource messageSource;

    @Mapping(source = "faculty.name", target = "faculty")
    @Mapping(source = "faculty.id", target = "facultyId")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "studentTicket", target = "numberTicket")
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "statusLocalized", ignore = true)
    public abstract UserDto toDto(User entity);

    @AfterMapping
    protected void decodePhoneNumber(User entity, @MappingTarget UserDto dto) {
        if (entity.getPhoneNumber() != null) {
            dto.setPhoneNumber(dataCodecService.decode(entity.getPhoneNumber()));
        }
    }

    @AfterMapping
    protected void fillStudentId(User entity, @MappingTarget UserDto dto) {
        if (dto.getAvatar() != null && entity.getId() != null) {
            dto.getAvatar().setStudentId(entity.getId());
        }
    }

    @AfterMapping
    protected void localizeFields(User entity, @MappingTarget UserDto dto) {
        java.util.Locale locale = org.springframework.context.i18n.LocaleContextHolder.getLocale();

        if (entity.getStatus() != null) {
            String localized = messageSource.getMessage(
                    "user.status." + entity.getStatus().name(),
                    null,
                    locale
            );
            dto.setStatusLocalized(localized);
        }

        if (entity.getGender() != null) {
            String localized = messageSource.getMessage(
                    "gender." + entity.getGender().name(),
                    null,
                    locale
            );
            dto.setGenderLocalized(localized);
        }
        if (entity.getRole() != null) {
            String localized = messageSource.getMessage(
                    "user.role." + entity.getRole().name(),
                    null,
                    locale
            );
            dto.setRoleLocalized(localized);
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "studentTicket", ignore = true)
    public abstract User toEntity(CreateUserDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract User updateEntityFromPatchDto(PatchUserDto dto, @MappingTarget User entity);

    public abstract List<UserDto> toDtoList(List<User> users);
}
