package ru.hogwarts.school;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.exception.FacultyNotFound;
import ru.hogwarts.school.exception.StudentNotFound;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentStatus;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    private List<Student> studentsTest;
    private List<Faculty> facultyTest;

    @BeforeEach
    void setUp() {
        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        Faculty faculty2 = new Faculty(2L, "Физика", "Синий");
        facultyTest = new ArrayList<>(List.of(faculty1,
                faculty2));
        Student student1 = new Student(1L, "Артём", 23, faculty1, StudentStatus.ACTIVE);
        Student student2 = new Student(2L, "Мария", 20, faculty1, StudentStatus.ACTIVE);
        Student student3 = new Student(3L, "Марат", 18, faculty1, StudentStatus.ACTIVE);
        Student student4 = new Student(4L, "Софья", 18, faculty1, StudentStatus.ACTIVE);
        Student student5 = new Student(5L, "Михаил", 19, null, StudentStatus.ACTIVE);
        studentsTest = new ArrayList<>(List.of(student1, student2, student3, student4, student5));
    }

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private StudentService studentService;

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void createStudent(int index) {
        Student testStudent = studentsTest.get(index);
        Faculty faculty = testStudent.getFaculty();

        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        // when(studentRepository.save(any(Student.class))).thenReturn(testStudent); // просто возвращает
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));// проверяем правильность создания перед отправкой в базу
        Student result = studentService.creteStudent(testStudent);

        assertNotNull(result);
        assertEquals(testStudent.getName(), result.getName());
        assertEquals(faculty.getName(), result.getFaculty().getName());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void createStudentFacultyNull() {
        Student testStudent = studentsTest.get(4);

        assertThrows(FacultyNotFound.class, () -> {
            studentService.creteStudent(testStudent);
        });
        verify(studentRepository, never()).save(any());
    }

    @Test
    void createStudentFacultyNotFound() {
        Student testStudent = studentsTest.get(0);
        when(facultyRepository.findById(testStudent.getFaculty().getId())).thenReturn(Optional.empty());
        assertThrows(FacultyNotFound.class, () ->
        {
            studentService.creteStudent(testStudent);
        });
    }

    // @DisplayName("Поиск студента успешны")
    @Test
    void studentIdSuccess() {
        Long searchId = 1L;
        Student expectedStudent = studentsTest.get(0);
        when(studentRepository.findById(searchId)).thenReturn(Optional.of(expectedStudent));
        Student result = studentService.studentSearch(searchId);
        assertEquals(searchId, result.getId(), "ID вернувшегося студента должен быть равен запрошенному");
        verify(studentRepository).findById(searchId);
    }

    @ParameterizedTest
    @ValueSource(longs = {6L, 7L, 8L, 9L, 10L})
    void studentNotFound(Long longs) {
        when(studentRepository.findById(longs)).thenReturn(Optional.empty());
        assertThrows(StudentNotFound.class, () -> {
            studentService.studentSearch(longs);
        });
        verify(studentRepository, times(1)).findById(longs);
    }

    @Test
    void studentIdNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            studentService.studentSearch(null);
        });
        verify(studentRepository, never()).findById(null);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void updateStudent(int index) {
        Student testStudent = studentsTest.get(index);
        Faculty testFaculty = facultyTest.get(1);
        String newName = "NewName";

        when(studentRepository.existsById(testStudent.getId())).thenReturn(true);

        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        testStudent.setName(newName);
        testStudent.setFaculty(testFaculty);
        Student result = studentService.updateStudent(testStudent);
        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertEquals(testFaculty.getId(), result.getFaculty().getId());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void studentNotFoundForUpdater(int index) {
        Student testStudent = studentsTest.get(index);
        when(studentRepository.existsById(testStudent.getId())).thenReturn(false);

        assertThrows(StudentNotFound.class, () -> {
            studentService.updateStudent(testStudent);
        });
        verify(studentRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void studentDeleteNotFound(Long longs) {
        when(studentRepository.findById(longs)).thenReturn(Optional.empty());

        assertThrows(StudentNotFound.class, () -> {
            studentService.deleteStudent(longs);
        });
        verify(studentRepository, never()).deleteById(longs);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void studentDelete(Long longs) {
        Student testStudent = studentsTest.stream()
                .filter(s -> s.getId().equals(longs))
                .findFirst().get();
        when(studentRepository.findById(longs)).thenReturn(Optional.of(testStudent));
        studentService.deleteStudent(longs);
        verify(studentRepository, times(1)).delete(testStudent);
    }

    @ParameterizedTest
    @NullSource
    void studentIdNull2(Long argument) {
        assertThrows(IllegalArgumentException.class, () -> {
            studentService.studentSearch(argument);
        });
        verify(studentRepository, never()).findById(argument);
    }

    @Test
    void searchStudentByAge() {
        int age = 20;

        List<Student> expectedStudent = studentsTest.stream()
                .filter(s -> s.getAge() == age)
                .toList();

        when(studentRepository.findByAge(age)).thenReturn(expectedStudent);

        List<Student> result = studentService.findByAge(age);

        assertEquals(expectedStudent.size(), result.size()); // сколько в тестовом списке и сколько в списке результата
        assertTrue(result.stream().allMatch(s -> s.getAge() == age)); // проверяем содержимое списка результата, с учетом фильтра возраста
        verify(studentRepository, times(1)).findByAge(age);
    }

    @Test
    void searchStudentByAgeNull() {
        int age = 10;

        List<Student> expectedStudent = studentsTest.stream()
                .filter(s -> s.getAge() == age)
                .toList();

        when(studentRepository.findByAge(age)).thenReturn(expectedStudent);

        List<Student> result = studentService.findByAge(age);
        assertEquals(expectedStudent.size(), result.size());
    }

    @Test
    void searchStudentBetweenByAge() {
        int from = 20;
        int to = 30;

        List<Student> expectedStudent = studentsTest.stream()
                .filter(s -> s.getAge() >= from && s.getAge() <= to)
                .toList();

        when(studentRepository.findByAgeBetween(from, to)).thenReturn(expectedStudent);

        List<Student> result = studentService.findByAgeBetween(from, to);

        assertEquals(expectedStudent.size(), result.size());
        assertTrue(result.stream().allMatch(s -> s.getAge() >= from && s.getAge() <= to));
        verify(studentRepository, times(1)).findByAgeBetween(from, to);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void findFacultyByStudentId(int index) {
        Student testStudent = studentsTest.get(index);
        Long facultyId = testStudent.getFaculty().getId();
        Faculty faculty = testStudent.getFaculty();


        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(faculty));

        Faculty result = studentService.getFacultyByStudentId(testStudent);
        assertNotNull(result);
        assertEquals(testStudent.getFaculty().getId(), result.getId());
        assertEquals(testStudent.getFaculty().getName(), result.getName());
        verify(facultyRepository, times(1)).findById(facultyId);

    }

    @ParameterizedTest
    @NullSource
    void findFacultyByStudentIdStudentNull (Student argument) {
        assertThrows(StudentNotFound.class, () -> {
            studentService.getFacultyByStudentId(argument);
        });

    }
    @Test
    void findFacultyByStudentIdStudentFacultyIdNull () {
        Student testStudent = studentsTest.get(4);

        assertThrows(FacultyNotFound.class, () -> {
            studentService.getFacultyByStudentId(testStudent);
        });
    }
    @Test
    void facultyNotFoundInRepository() {
        Student testStudent = studentsTest.get(0);
        Long facultyId = testStudent.getFaculty().getId();
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.empty());
        assertThrows(FacultyNotFound.class, () -> {
            studentService.getFacultyByStudentId(testStudent);
        });
    }
    @Test
    @DisplayName("Тест на экспорт студентов в CSV")
    void testExportToCsv() {
        when(studentRepository.findAll()).thenReturn(studentsTest);

        // 2. Вызываем метод
        String csv = studentService.exportStudentToCsv();

        // 3. Проверяем, что в строке есть хотя бы Артём (или любой из списка)
        assertNotNull(csv);
        assertTrue(csv.contains(studentsTest.get(0).getName()));

        verify(studentRepository).findAll();
    }
}
