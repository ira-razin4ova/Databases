package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.dto.event.*;
import ru.hogwarts.school.dto.task.TaskPatchDto;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.EventMapper;
import ru.hogwarts.school.mapper.TaskMapper;
import ru.hogwarts.school.model.Event;
import ru.hogwarts.school.model.Task;
import ru.hogwarts.school.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final TaskMapper taskMapper;

    public Event getEventOrThrow(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event " + id + "not found"));
    }

    public EventDto geByIdEvent(Long id) {
        Event event = getEventOrThrow(id);
        return eventMapper.toDto(event);
    }

    public EventFullDto geByIdEventFull(Long id) {
        Event event = getEventOrThrow(id);
        return eventMapper.toDtoFull(event);
    }

    public List<EventDto> findAllWithTasks() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.toDtoList(events);
    }

    public List<EventShortDto> getByAllEventShort() {
        List<Event> eventsShort = eventRepository.findAll();
        return eventMapper.toDtoListShort(eventsShort);
    }

    @Transactional
    public EventFullDto createEvent(CreateEventDto dto) {
        Event event = eventMapper.toEntity(dto);

       linkTasksToEvent(event);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDtoFull(savedEvent);
    }

    private void linkTasksToEvent(Event event) {
        if (event.getTasks() != null) {
            event.getTasks().forEach(task -> task.setEvent(event));
        }
    }

    @Transactional
    public EventDto patchEvent(Long id, PatchEventDto patchDto) {
        Event event = getEventOrThrow(id);

        eventMapper.updateEntityFromDto(patchDto, event);

        if (patchDto.tasks() != null) {
            synchronizeTasks(event, patchDto.tasks());
        }

        return eventMapper.toDto(eventRepository.save(event));
    }

    /**
     * Синхронизирует список задач ивента с пришедшим DTO.
     * Обновляет существующие задачи или добавляет новые.
     */
    private void synchronizeTasks(Event event, List<TaskPatchDto> taskDtos) {
        for (TaskPatchDto taskDto : taskDtos) {
            if (taskDto.id() != null) {
                updateExistingTask(event, taskDto);
            } else {
                addNewTask(event, taskDto);
            }
        }
    }

    private void updateExistingTask(Event event, TaskPatchDto taskDto) {
        Task existingTask = event.getTasks().stream()
                .filter(t -> t.getId().equals(taskDto.id()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Задача с id " + taskDto.id() + " не найдена в этом ивенте"));

        taskMapper.updateEntityFromPatchDto(taskDto, existingTask);
    }

    private void addNewTask(Event event, TaskPatchDto taskDto) {
        Task newTask = taskMapper.toEntity(taskDto);
        newTask.setEvent(event);
        event.getTasks().add(newTask);
    }

    public DeleteWarningDTO checkBeforeDelete(Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        long taskCount = event.getTasks().size();

        if (taskCount > 0) {
            return new DeleteWarningDTO(true, taskCount, "Удаление ивента повлечет удаление " + taskCount + " задачи");
        }
        return new DeleteWarningDTO(false, 0, "Можно удалять");
    }

    @Transactional
    public void deleteEvent (Long id) {
        Event delereEvent = getEventOrThrow(id);
        eventRepository.delete(delereEvent);
    }
}

