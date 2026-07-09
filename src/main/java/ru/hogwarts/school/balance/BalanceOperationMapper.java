package ru.hogwarts.school.balance;


import org.mapstruct.*;
import ru.hogwarts.school.balance.dto.BalanceOperationDto;
import ru.hogwarts.school.balance.dto.CreateBalanceOperationDto;
import ru.hogwarts.school.balance.dto.batch.BatchConfirmRequestDto;
import ru.hogwarts.school.faculty.FacultyMapper;
import ru.hogwarts.school.user.UserMapper;

import java.util.List;

@Mapper (componentModel =  "spring", uses = {UserMapper.class, FacultyMapper.class})
public interface BalanceOperationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "sourceFaculty", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "creator", ignore = true)
    BalanceOperation toEntity(CreateBalanceOperationDto dto);

    @Mapping(source = "task.id", target = "taskId")
    BalanceOperationDto toDto(BalanceOperation entity);

    List<BalanceOperationDto> toDtoList (List <BalanceOperation> balanceOperationList);

    @BeanMapping (nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "sourceFaculty", ignore = true)
    @Mapping(target = "creator", ignore = true)
    void updateEntityFromPatchDto (BatchConfirmRequestDto operationStudentsDto, @MappingTarget BalanceOperation entity);
}
