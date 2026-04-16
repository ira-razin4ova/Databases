package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.StudentDTO;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.student.StudentMapper;
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
    private final DataCodecService dataCodecService;
    private final StudentMapper studentMapper;

    private final RestTemplateBuilder restTemplateBuilder;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository, DataCodecService dataCodecService, StudentMapper studentMapper,
                          RestTemplateBuilder restTemplateBuilder) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.dataCodecService = dataCodecService;
        this.studentMapper = studentMapper;
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public StudentDTO createStudent(CreateStudentDto dto) {
        Student student = studentMapper.toEntity(dto);
        student.setPhoneNumber(dataCodecService.encodePhone(dto.getPhoneNumber()));
        student.setFaculty(resolveFacultyOrThrow(dto.getIdFaculty()));
        Student saved = studentRepository.save(student);
        return studentMapper.toDto(saved);
    }

    public Faculty resolveFacultyOrThrow(Long id) {
       return facultyRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Факультет с id " + id + " не найден")
                );
    }


    public Student getStudentOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с таким" + id +  "не найден"
                ));
    }

    public StudentDTO getByIdDTO(Long id) {
        Student student = getStudentOrThrow(id);

        return studentMapper.toDto(student);
    }

    public Student editStudent(Student student) {
        if (!studentRepository.existsById(student.getId())) {
            throw new NotFoundException("ID не найден, изменения не возможно!");
        }
        student.setId(student.getId());
        return studentRepository.save(student);
    }

    public Student deleteStudent(Long id) {
        Student studentToDelete  = getStudentOrThrow(id);

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
        Student student = getStudentOrThrow(studentId);

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

    public Long getStudentCount () {
        return studentRepository.getStudentCount();
    }

    public Double getStudentAgeAvg () {
        Double avg = studentRepository.getStudentAgeAVG();
        return (avg != null) ? avg : 0.0;
    }

    public List <StudentDTO> getStudentLimitFiveSortedDesc () {
        List <Student> students = studentRepository.getStudentLimitFive();
        return studentMapper.toDtoList(students);
    }
}
