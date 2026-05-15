package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.dto.task.CreateTaskDto;
import ru.hogwarts.school.dto.task.PatchTaskDto;
import ru.hogwarts.school.dto.task.TaskDto;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.exception.ValidationException;
import ru.hogwarts.school.mapper.TaskMapper;
import ru.hogwarts.school.model.Quest;
import ru.hogwarts.school.model.Task;
import ru.hogwarts.school.repository.QuestRepository;
import ru.hogwarts.school.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    private final QuestRepository questRepository;

    private final TaskMapper taskMapper;

    public Task getTaskOrThrow (Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Задача",  id));
    }
    public Quest getQuestOrThrow(Long id) {
        return questRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Квест", id));
    }

    public List<TaskDto> getAllTasks () {
        List <Task> tasks = taskRepository.findAll();
        return taskMapper.toDtoList(tasks);
    }

    public TaskDto getTaskById (Long id) {
        Task task = getTaskOrThrow(id);
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDto createTask (CreateTaskDto dto) {
        Task task = taskMapper.toEntity(dto);
        linkTasksToEvent(task);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    private void linkTasksToEvent(Task task) {
        if (task.getQuest() == null) {
            throw new ValidationException("ID ивента не может быть пустым");
        }
        Quest quest = getQuestOrThrow(task.getQuest().getId());
        task.setQuest(quest);
    }

    @Transactional
    public TaskDto patchTask (Long id, PatchTaskDto dto) {
        Task task = getTaskOrThrow(id);

        taskMapper.updateEntityFromPatchDto(dto, task);
        updateEventRelationship(task, dto.questId());
        return taskMapper.toDto(task);
    }

    private void updateEventRelationship(Task task, Long eventId) {
        if (eventId != null) {
            Quest newQuest = getQuestOrThrow(eventId);
            task.setQuest(newQuest);
        }
    }

    @Transactional
    public void deleteTask (Long id) {
        Task deleteTask = getTaskOrThrow(id);
        taskRepository.delete(deleteTask);
    }
}
