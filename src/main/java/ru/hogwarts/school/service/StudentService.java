package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFound;
import ru.hogwarts.school.exception.StudentNotFound;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
@Transactional
@Service
// @RequiredArgsConstructor Lombok Автоматически создаст конструктор для всех final полей
public class StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Student creteStudent(Student student) {

        if (student.getFaculty() == null || student.getFaculty().getId() == null) {
            throw new FacultyNotFound("Не указан факультет или указан неверно!");
        }
        Faculty faculty = facultyRepository
                .findById(student.getFaculty().getId())
                .orElseThrow(() ->
                        new FacultyNotFound(
                                "Факультет с id " + student.getFaculty().getId() + " не найден"
                        )
                );

        student.setFaculty(faculty);

        return studentRepository.save(student);
    }


    public Student studentSearch(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("У студента не установлен ID");
        }

        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFound(
                        "Пользователь с таким id  не найден"
                ));
    }

    public Student updateStudent(Student student) {
        if (!studentRepository.existsById(student.getId())) {
            throw new StudentNotFound("ID не найден, изменения не возможно!");
        }
        student.setId(student.getId());
        return studentRepository.save(student);
    }

    public Student deleteStudent(Long id) {
        Student studentToDelete = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFound("Невозможно удалить: студент с ID не найден! Или был удален ранее!"));

        studentRepository.delete(studentToDelete);
        return studentToDelete;
    }

    public List<Student> findByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public List<Student> findByAgeBetween(int from, int to) {
        return studentRepository.findByAgeBetween(from, to);
    }

    public Faculty getFacultyByStudentId(Student student) {
        if (student == null) {
            throw new StudentNotFound("Студент не существует!");
        }
        Long facultyId = (student.getFaculty() != null) ? student.getFaculty().getId() : null;

        if (facultyId == null) {
            throw new FacultyNotFound("У данного студента отсутствует информация о факультете!");
        }
        return facultyRepository.findById(facultyId)
                .orElseThrow(() -> new FacultyNotFound("Уточните информацию данный факультет отсутствует или указан неверно!"));
    }
     public String exportStudentToCsv () {
        List <Student> students = studentRepository.findAll();
        StringBuilder csv = new StringBuilder("№,Id,Name,Age,FacultyId,FName,FColor\n");
        for (Student student : students) {
            int count = 1;
            csv.append(count++).append(",")
                    .append(student.getId()).append(",")
                    .append(student.getName()).append(",")
                    .append(student.getAge()).append(",");

            if (student.getFaculty() != null) {
                csv.append(student.getFaculty() .getId()).append(",")
                        .append(student.getFaculty().getName()).append(",")
                        .append(student.getFaculty() .getColor());
            } else {
                csv.append("-,-,-");
            }
            csv.append("\n");

        }
        return csv.toString();
     }

}
