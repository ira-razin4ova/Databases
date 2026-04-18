package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query ("SELECT e FROM Event e LEFT JOIN FETCH e.tasks")
    List<Event> findAllWithTasks();

    /**
     * Находит событие по ID и сразу подгружает все связанные задачи.
     * Оптимально для экранов редактирования (Full DTO), так как исключает
     * дополнительные обращения к базе данных при обращении к списку задач.
     * * @param id идентификатор события
     * @return Optional с заполненным событием или empty, если не найдено.
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.tasks WHERE e.id = :id")
    Optional<Event> findByIdWithTasks(@Param("id") Long id);
}