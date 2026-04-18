package ru.hogwarts.school.mapper;


import org.mapstruct.*;
import ru.hogwarts.school.dto.task.CreateTaskDto;
import ru.hogwarts.school.dto.task.TaskDto;
import ru.hogwarts.school.dto.task.TaskPatchDto;
import ru.hogwarts.school.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "eventId", source = "event.id")
    TaskDto toDto(Task entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    Task toEntity(CreateTaskDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    void updateEntityFromPatchDto(TaskPatchDto dto, @MappingTarget Task entity);

    Task toEntity(TaskPatchDto dto);
}
