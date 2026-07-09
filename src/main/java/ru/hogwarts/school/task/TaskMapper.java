package ru.hogwarts.school.task;


import org.mapstruct.*;
import ru.hogwarts.school.task.dto.CreateTaskDto;
import ru.hogwarts.school.task.dto.TaskDto;
import ru.hogwarts.school.task.dto.PatchTaskDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "quest.id", target = "questId")
    TaskDto toDto(Task entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "questId", target = "quest.id")
    @Mapping(target = "archive", ignore = true)
    Task toEntity(CreateTaskDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quest", ignore = true)
    void updateEntityFromPatchDto(PatchTaskDto dto, @MappingTarget Task entity);

    Task toEntity(PatchTaskDto dto);

    List<TaskDto> toDtoList(List<Task> tasks);
}
