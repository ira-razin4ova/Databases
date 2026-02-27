package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentNotFound;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor Lombok Автоматически создаст конструктор для всех final полей
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService (StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student creteStudent(Student student) {
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

    public Student updateStudent (Student student) {
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
}
