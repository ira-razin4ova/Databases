package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.dto.quest.*;
import ru.hogwarts.school.dto.task.PatchTaskDto;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.mapper.QuestMapper;
import ru.hogwarts.school.mapper.TaskMapper;
import ru.hogwarts.school.model.Quest;
import ru.hogwarts.school.model.Task;
import ru.hogwarts.school.repository.QuestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestService {

    private final QuestRepository questRepository;

    private final QuestMapper questMapper;

    private final TaskMapper taskMapper;

    public Quest getQuestOrThrow(Long id) {
        return questRepository.findByIdWithTasks(id).
                orElseThrow(() -> new EntityNotFoundException("Quest",  id));
    }

    public QuestDto getByIdQuest(Long id) {
        Quest quest = getQuestOrThrow(id);
        return questMapper.toDto(quest);
    }

    public QuestFullDto geByIdQuestFull(Long id) {
        Quest quest = getQuestOrThrow(id);
        return questMapper.toDtoFull(quest);
    }

    public List<QuestDto> findAllWithTasks() {
        List<Quest> quests = questRepository.findAll();
        return questMapper.toDtoList(quests);
    }

    public List<QuestShortDto> getByAllQuestShort() {
        List<Quest> questList = questRepository.findAll();
        return questMapper.toDtoListShort(questList);
    }

    public List<QuestDto> getByQuestActive (Long studentId) {
        List<Quest> questList = questRepository.findActiveQuestsForStudent(studentId);
        return questMapper.toDtoList(questList);
    }

    @Transactional
    public QuestFullDto createQuest(CreateQuestDto dto) {
        Quest quest = questMapper.toEntity(dto);

        linkTasksToQuest(quest);

        Quest savedQuest = questRepository.save(quest);
        return questMapper.toDtoFull(savedQuest);
    }

    private void linkTasksToQuest(Quest quest) {
        if (quest.getTasks() != null) {
            quest.getTasks().forEach(task -> task.setQuest(quest));
        }
    }

    @Transactional
    public QuestFullDto updateQuest(Long id, PatchQuestDto patchDto) {
        Quest quest = getQuestOrThrow(id);

        questMapper.updateEntityFromDto(patchDto, quest);

        if (patchDto.tasks() != null) {
            synchronizeTasks(quest, patchDto.tasks());
        }

        return questMapper.toDtoFull(questRepository.save(quest));
    }

    /**
     * Синхронизирует список задач ивента с пришедшим DTO.
     * Обновляет существующие задачи или добавляет новые.
     */
    private void synchronizeTasks(Quest quest, List<PatchTaskDto> taskDtos) {
        for (PatchTaskDto taskDto : taskDtos) {
            if (taskDto.id() != null) {
                updateExistingTask(quest, taskDto);
            } else {
                addNewTask(quest, taskDto);
            }
        }
    }

    private void updateExistingTask(Quest quest, PatchTaskDto taskDto) {
        Task existingTask = quest.getTasks().stream()
                .filter(t -> t.getId().equals(taskDto.id()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Задача" , taskDto.id(), "не найдена для квеста", quest.getId()));

        taskMapper.updateEntityFromPatchDto(taskDto, existingTask);
    }

    private void addNewTask(Quest quest, PatchTaskDto taskDto) {
        Task newTask = taskMapper.toEntity(taskDto);
        newTask.setQuest(quest);
        quest.getTasks().add(newTask);
    }

    public CheckTaskFotThisQuestDto checkBeforeDelete(Long id) {
        Quest quest = questRepository.findById(id).orElseThrow();
        long taskCount = quest.getTasks().size();

        if (taskCount > 0) {
            return new CheckTaskFotThisQuestDto(true, taskCount, "Удаление ивента повлечет удаление " + taskCount + " задачи");
        }
        return new CheckTaskFotThisQuestDto(false, 0, "Можно удалять");
    }

    @Transactional
    public void deleteQuest(Long id) {
        Quest delereQuest = getQuestOrThrow(id);
        questRepository.delete(delereQuest);
    }
}

