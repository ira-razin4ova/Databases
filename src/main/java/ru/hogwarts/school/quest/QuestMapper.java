package ru.hogwarts.school.quest;

import org.mapstruct.*;
import ru.hogwarts.school.quest.dto.*;
import ru.hogwarts.school.task.TaskMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface QuestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target ="archive", ignore = true )
    Quest toEntity(CreateQuestDto dto);

    @Mapping(target = "tasksTitle", expression = "java(entity.getTasks() != null ? entity.getTasks().stream().map(t -> t.getTitle()).collect(java.util.stream.Collectors.toList()) : null)")
    QuestDto toDto(Quest entity);

    QuestFullDto toDtoFull (Quest entity);

    List<QuestDto> toDtoList(List<Quest> quests);

    List<QuestShortDto> toDtoListShort(List<Quest> eventsShort);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tasks", ignore = true)
    void updateEntityFromDto(PatchQuestDto dto, @MappingTarget Quest entity);

}
