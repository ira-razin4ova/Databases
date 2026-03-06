package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Student;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int from, int to);

    List<Student> findByFaculty_Id(Long id); // послу findBy должно быть имя поля в базе по которому мы будем искать, иначе приложение не запускается
}
