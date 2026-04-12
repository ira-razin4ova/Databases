package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.student.StudentDTO;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
// @RequiredArgsConstructor Lombok Автоматически создаст конструктор для всех final полей
public class StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.studentMapper = studentMapper;
    }

    public Student creteStudent(Student student) {

        if (student.getFaculty() == null || student.getFaculty().getId() == null) {
            throw new NotFoundException("Не указан факультет или указан неверно!");
        }
        Faculty faculty = facultyRepository
                .findById(student.getFaculty().getId())
                .orElseThrow(() ->
                        new NotFoundException(
                                "Факультет с id " + student.getFaculty().getId() + " не найден"
                        )
                );

        student.setFaculty(faculty);

        return studentRepository.save(student);
    }


    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с таким" + id +  "не найден"
                ));
    }

    public Student editStudent(Student student) {
        if (!studentRepository.existsById(student.getId())) {
            throw new NotFoundException("ID не найден, изменения не возможно!");
        }
        student.setId(student.getId());
        return studentRepository.save(student);
    }

    public Student deleteStudent(Long id) {
        Student studentToDelete  = getStudentById(id);

        studentRepository.delete(studentToDelete);
        return studentToDelete;
    }

    public List<Student> findByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public List<Student> findByAgeBetween(int from, int to) {
        return studentRepository.findByAgeBetween(from, to);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        Student student = getStudentById(studentId);

        if (student.getFaculty() == null) {
            throw new NotFoundException("Уточните информацию данный факультет отсутствует или указан неверно!");
        }
        return student.getFaculty();
    }

    public String exportStudentToCsv() {
        List<Student> students = studentRepository.findAll();
        StringBuilder csv = new StringBuilder("№,Id,firstName,lastName,Age,FacultyId,FName,FColor\n");
        for (Student student : students) {
            int count = 1;
            csv.append(count++).append(",")
                    .append(student.getId()).append(",")
                    .append(student.getFirstName()).append(",")
                    .append(student.getLastName()).append(",")
                    .append(student.getAge()).append(",");

            if (student.getFaculty() != null) {
                csv.append(student.getFaculty().getId()).append(",")
                        .append(student.getFaculty().getName()).append(",")
                        .append(student.getFaculty().getColor());
            } else {
                csv.append("-,-,-");
            }
            csv.append("\n");

        }
        return csv.toString();
    }

    public Map<Long, Student> studentMap(int age) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getAge() >= age)
                .collect(Collectors.toMap(Student::getId, Function.identity(), (oldValue, newValue) -> newValue)); // делать при конфликте ID
    }
    public StudentDTO getByIdDTO(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return studentMapper.mapToDto(student);
    }
}
