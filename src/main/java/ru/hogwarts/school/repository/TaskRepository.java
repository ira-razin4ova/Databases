package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}