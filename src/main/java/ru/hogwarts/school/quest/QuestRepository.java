package ru.hogwarts.school.quest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    @Query ("SELECT e FROM Quest e LEFT JOIN FETCH e.tasks")
    List<Quest> findAllWithTasks();

    /**
     * Находит событие по ID и сразу подгружает все связанные задачи.
     * Оптимально для экранов редактирования (Full DTO), так как исключает
     * дополнительные обращения к базе данных при обращении к списку задач.
     * * @param id идентификатор события
     * @return Optional с заполненным событием или empty, если не найдено.
     */
    @Query("SELECT e FROM Quest e LEFT JOIN FETCH e.tasks WHERE e.id = :id")
    Optional<Quest> findByIdWithTasks(@Param("id") Long id);


    @Query("SELECT DISTINCT q FROM Quest q " +
            "LEFT JOIN FETCH q.tasks " +
            "JOIN User s ON (q.targetCourse = s.course OR q.targetCourse IS NULL) " +
            "WHERE s.id = :studentId " +
            "AND (q.targetCourse = s.course OR q.targetCourse IS NULL) " +
            "AND (q.dateStart <= CURRENT_TIMESTAMP OR q.dateStart IS NULL) " +
            "AND (q.dateEnd >= CURRENT_TIMESTAMP OR q.dateEnd IS NULL) " +
            "AND q.archive = false")
    List<Quest> findActiveQuestsForStudent(@Param("studentId") Long studentId);

}