package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.quest.PatchQuestDto;
import ru.hogwarts.school.dto.task.CreateTaskDto;
import ru.hogwarts.school.dto.task.PatchTaskDto;
import ru.hogwarts.school.dto.task.TaskDto;
import ru.hogwarts.school.model.Task;
import ru.hogwarts.school.service.TaskService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid CreateTaskDto createTaskDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(createTaskDto));
    }

    @PostMapping ("/{id}")
    public ResponseEntity <TaskDto> updateTask (@PathVariable @Positive Long id,
                               @RequestBody @Valid PatchTaskDto patchTaskDto) {
        return ResponseEntity.ok(taskService.patchTask(id, patchTaskDto));
    }



}
