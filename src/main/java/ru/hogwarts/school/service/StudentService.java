package ru.hogwarts.school.service;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.constant.AppConstants;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.PatchStudentDto;
import ru.hogwarts.school.dto.student.StudentDto;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.exception.ValidationException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;


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
                    return new EntityNotFoundException("Факультет", id);
                });
    }

    public Student getStudentOrThrow(Long id) {
        logger.debug("Was invoked method for find Student with id = {} ", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("There is not student with id = {}", id);
                    return new EntityNotFoundException("Студент", id);
                });
    }

    public StudentDto getByIdDTO(Long id) {
        Student student = getStudentOrThrow(id);
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentDto updateStudent(Long id, PatchStudentDto dto) {
        logger.info("Was invoked method for update Student id = {}, PatchStudentDto = {}", id, dto);

        Student student = getStudentOrThrow(id);

        studentMapper.updateEntityFromPatchDto(dto, student);

        if (dto.phoneNumber() != null) {
            codecPhone(student, dto.phoneNumber());
        }
        if (dto.facultyId() != null) {
            updateFacultyRelationship(student, dto.facultyId());
        }

        logger.info("Student with id {} successfully updated and saved", id);
        return studentMapper.toDto(student);
    }

    private void updateFacultyRelationship(Student studentIdFaculty, Long facultyId) {
        if (facultyId != null) {
            Faculty newFaculty = getFacultyOrThrow(facultyId);
            studentIdFaculty.setFaculty(newFaculty);
        }
    }

    private void codecPhone(Student student, String phoneNumber) {
        student.setPhoneNumber(dataCodecService.encodePhone(phoneNumber));
    }

    public Student editStudent(Student student) {
        if (!studentRepository.existsById(student.getId())) {
            throw new EntityNotFoundException("Студент", student.getId());
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
        List<Student> students = studentRepository.findByAge(age);
        return studentMapper.toDtoList(students);
    }

    public List<StudentDto> findByAgeBetween(int from, int to) {
        logger.info("Was invoked method find by age between from = {} - to = {} ", from, to);
        List<Student> students = studentRepository.findByAgeBetween(from, to);
        return studentMapper.toDtoList(students);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        Student student = getStudentOrThrow(studentId);
        logger.info("Was invoked method det faculty student id = {} ", studentId);

        if (student.getFaculty() == null) {
            logger.warn("student faculty null id = {}", studentId);
            throw new ValidationException(AppConstants.ExceptionMessages.INVALID_FACULTY_DATA);
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

    public List<String> studentsSteamSorted(String sortedLetter) {
        return studentRepository.findAll().stream()
                .map(Student::getFirstName)
                .map(String::toUpperCase)
                .filter(name -> name.startsWith(sortedLetter.toUpperCase()))
                .sorted()
                .toList();
    }

    public Double getAverageAge() {
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

    public String getLongestFacultyName() {
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("Факультеты не найдены");
    }

    public long getSumOptimization() {
        return LongStream.rangeClosed(1, 1000000)
                .parallel()
                .sum();
    }

    public List<StudentDto> getAllStudentsSortedByName() {
        return studentRepository.findAll().stream()
                .sorted((s1, s2) -> s1.getFirstName().compareTo(s2.getFirstName()))
                .map(studentMapper::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void studentThread () {
        List <StudentDto> forThread = getAllStudentsSortedByName();
        for (StudentDto s : forThread) {
            System.out.println(s.getFirstName() );
        }
        int countForThread  = forThread.size() /3;

        Thread thread1 = new Thread(() -> {
        for (int i = 0; i < countForThread; i++) {

            System.out.println("Thread №1 " + forThread.get(i).getFirstName());
        }
        });

        Thread thread2 = new Thread (() -> {
            for (int i = countForThread; i < countForThread * 2; i++) {

                System.out.println("Thread №2 " + forThread.get(i).getFirstName());
            }
        });

        Thread thread3 = new Thread(() -> {
            for (int i = countForThread * 2; i < countForThread * 3; i++) {

                System.out.println("Thread №3 " + forThread.get(i).getFirstName());
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
    }

    @SneakyThrows
    public synchronized void studentThreadSynchronizedJoin () {
        List <StudentDto> forThread = getAllStudentsSortedByName();
        for (StudentDto s : forThread) {
            System.out.println(s.getFirstName() );
        }
        int countForThread  = forThread.size() /3;

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < countForThread; i++) {

                System.out.println("Thread №1 " + forThread.get(i).getFirstName());
                try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        });

        Thread thread2 = new Thread (() -> {
            for (int i = countForThread; i < countForThread * 2; i++) {

                System.out.println("Thread №2 " + forThread.get(i).getFirstName());
                try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        });

        Thread thread3 = new Thread(() -> {
            for (int i = countForThread * 2; i < countForThread * 3; i++) {

                System.out.println("Thread №3 " + forThread.get(i).getFirstName());
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread1.start();
        thread1.join();

        thread2.start();
        thread2.join();

        thread3.start();
        thread3.join();
    }

    public synchronized void studentThreadSynchronizedObject () {
        List <StudentDto> forThread = getAllStudentsSortedByName();

        for (StudentDto s : forThread) {
            System.out.println(s.getFirstName() );
        }

        int countForThread  = forThread.size() /3;

        Object lock = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                for (int i = 0; i < countForThread; i++) {

                    System.out.println("Thread №1 " + forThread.get(i).getFirstName());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread thread2 = new Thread (() -> {
            synchronized (lock) {
                for (int i = countForThread; i < countForThread * 2; i++) {

                    System.out.println("Thread №2 " + forThread.get(i).getFirstName());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread thread3 = new Thread(() -> {
            synchronized (lock) {
                for (int i = countForThread * 2; i < countForThread * 3; i++) {

                    System.out.println("Thread №3 " + forThread.get(i).getFirstName());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();

    }
}
