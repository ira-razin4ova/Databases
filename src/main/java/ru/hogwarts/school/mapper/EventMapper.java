package ru.hogwarts.school.mapper;

import org.mapstruct.*;
import ru.hogwarts.school.dto.event.*;
import ru.hogwarts.school.model.Event;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Event toEntity(CreateEventDto dto);

    EventDto toDto(Event entity);

    EventFullDto toDtoFull (Event entity);

    List<EventDto> toDtoList(List<Event> events);

    List<EventShortDto> toDtoListShort(List<Event> eventsShort);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PatchEventDto dto, @MappingTarget Event entity);

}
