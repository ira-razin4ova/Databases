package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.PatchStudentDto;
import ru.hogwarts.school.dto.student.StudentDto;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final DataCodecService dataCodecService;
    private final StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository, DataCodecService dataCodecService, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.dataCodecService = dataCodecService;
        this.studentMapper = studentMapper;
    }

    Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Transactional
    public StudentDto createStudent(CreateStudentDto dto) {
        logger.info("Was invoked method for create Student CreateStudentDto = {}", dto);
        Student student = studentMapper.toEntity(dto);
        student.setPhoneNumber(dataCodecService.encodePhone(dto.getPhoneNumber()));
        student.setFaculty(getFacultyOrThrow(dto.getIdFaculty()));
        Student saved = studentRepository.save(student);
        return studentMapper.toDto(saved);
    }

    public Faculty getFacultyOrThrow(Long id) {
        logger.debug("Was invoked method for find Faculty");
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                   logger.warn("There is not faculty with id = {}", id);
                   return new NotFoundException("Факультет с id " + id + " не найден");
                });
    }

    public Student getStudentOrThrow(Long id) {
        logger.debug("Was invoked method for find Student with id = {} ", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                   logger.warn("There is not student with id = {}", id);
                    return new NotFoundException("Пользователь с таким id " + id + " не найден");
                });
    }

    public StudentDto getByIdDTO(Long id) {
        Student student = getStudentOrThrow(id);
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentDto patchStudent(Long id, PatchStudentDto dto) {
        logger.info("Was invoked method for update Student id = {}, PatchStudentDto = {}", id, dto);
        Student student = getStudentOrThrow(id);

        studentMapper.updateEntityFromPatchDto(dto, student);
        updateFacultyRelationship(student, dto.facultyId());
        logger.info("Student with id {} successfully updated and saved", id);
        return studentMapper.toDto(student);
    }

    private void updateFacultyRelationship(Student studentIdFaculty, Long facultyId) {
        if (facultyId != null) {
            Faculty newFaculty = getFacultyOrThrow(facultyId);
            studentIdFaculty.setFaculty(newFaculty);
        }
    }

    public Student editStudent(Student student) {
        if (!studentRepository.existsById(student.getId())) {
            throw new NotFoundException("ID не найден, изменения не возможно!");
        }
        student.setId(student.getId());
        return studentRepository.save(student);
    }
    @Transactional
    public void deleteStudent(Long id) {
        Student studentToDelete = getStudentOrThrow(id);
        logger.info("Was invoked method delete student with id = {}, student = {}", id, studentToDelete);
        studentRepository.delete(studentToDelete);
    }

    public List<StudentDto> findByAge(int age) {
        logger.info("Was invoked method find by age = {} ", age);
        List <Student> students = studentRepository.findByAge(age);
        return studentMapper.toDtoList(students);
    }

    public List<StudentDto> findByAgeBetween(int from, int to) {
        logger.info("Was invoked method find by age between from = {} - to = {} ", from, to);
        List <Student> students = studentRepository.findByAgeBetween(from, to);
        return studentMapper.toDtoList(students);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        Student student = getStudentOrThrow(studentId);
        logger.info("Was invoked method det faculty student id = {} ", studentId);

        if (student.getFaculty() == null) {
            logger.warn("student faculty null id = {}", studentId);
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

    public Long getStudentCount() {
        return studentRepository.getStudentCount();
    }

    public Double getStudentAgeAvg() {
        Double avg = studentRepository.getStudentAgeAVG();
        return (avg != null) ? avg : 0.0;
    }

    public List<StudentDto> getStudentLimitFiveSortedDesc() {
        List<Student> students = studentRepository.getStudentLimitFive();
        return studentMapper.toDtoList(students);
    }
}
