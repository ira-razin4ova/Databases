package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.dto.task.CreateTaskDto;
import ru.hogwarts.school.dto.task.TaskDto;
import ru.hogwarts.school.dto.task.TaskPatchDto;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.TaskMapper;
import ru.hogwarts.school.model.Event;
import ru.hogwarts.school.model.Task;
import ru.hogwarts.school.repository.EventRepository;
import ru.hogwarts.school.repository.TaskRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    private final EventRepository eventRepository;

    private final TaskMapper taskMapper;

    public Task getTaskOrThrow (Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task " + id + "not found"));
    }
    public Event getEventOrThrow (Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Task " + id + "not found"));
    }

    @Transactional
    public TaskDto createTask (CreateTaskDto dto) {
        Task task = taskMapper.toEntity(dto);
        linkTasksToEvent(task);
        return taskMapper.toDto(task);
    }

    private void linkTasksToEvent(Task task) {
        if (task.getEvent() == null) {
            throw new BadRequestException("ID ивента не может быть пустым");
        }
        Event event = getEventOrThrow(task.getEvent().getId());
        task.setEvent(event);
    }

    @Transactional
    public TaskDto patchTask (Long id, TaskPatchDto dto) {
        Task task = getTaskOrThrow(id);

        taskMapper.updateEntityFromPatchDto(dto, task);
        updateEventRelationship(task, dto.eventId());
        return taskMapper.toDto(task);
    }

    private void updateEventRelationship(Task task, Long eventId) {
        if (eventId != null) {
            Event newEvent = getEventOrThrow(eventId);
            task.setEvent(newEvent);
        }
    }

    @Transactional
    public void deleteTask (Long id) {
        Task deleteTask = getTaskOrThrow(id);
        taskRepository.delete(deleteTask);
    }
}
