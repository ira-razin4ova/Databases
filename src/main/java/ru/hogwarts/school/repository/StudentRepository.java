package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.dto.student.StudentDTO;
import ru.hogwarts.school.model.Student;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<StudentDTO> findByAge(int age);

    List<StudentDTO> findByAgeBetween(int from, int to);

    List<StudentDTO> findByFaculty_Id(Long id); // послу findBy должно быть имя поля в базе по которому мы будем искать, иначе приложение не запускается

    @Query (value = "SELECT COUNT (*) from student",nativeQuery = true)
    Long getStudentCount ();

    @Query (value = "SELECT AVG (age) FROM student",nativeQuery = true)
    Double getStudentAgeAVG ();

    @Query (value = "SELECT * FROM student ORDER BY id desc LIMIT 5", nativeQuery = true)
    List <Student> getStudentLimitFive ();
}
