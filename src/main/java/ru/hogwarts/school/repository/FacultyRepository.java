package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

@Repository
public interface FacultyRepository extends JpaRepository <Faculty, Long> {

    @Query(value = "SELECT * FROM faculty WHERE " +
            "LOWER(name COLLATE \"ru_RU.UTF-8\") = LOWER(:name COLLATE \"ru_RU.UTF-8\") OR " +
            "LOWER(color COLLATE \"ru_RU.UTF-8\") = LOWER(:color COLLATE \"ru_RU.UTF-8\")",
            nativeQuery = true)
    List<Faculty> findByRussianNameOrColor(@Param("name") String name, @Param("color") String color);

    List<Faculty> findByNameIgnoreCaseOrColorIgnoreCase (String name, String color);
}