package ru.hogwarts.school.service.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.constant.StudentStatus;
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.StudentDTO;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.DataCodecService;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    private List<StudentDTO> studentDtosTest;
    private List<Student> studentsTest;
    private List<CreateStudentDto> createDtosTest;
    private List<Faculty> facultyTest;

    @BeforeEach
    void setUp() {

        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        Faculty faculty2 = new Faculty(2L, "Физика", "Синий");
        facultyTest = new ArrayList<>(List.of(faculty1,
                faculty2));
        Student student1 = new Student(1L, "Артём", "Смирнов", 23, faculty1, StudentStatus.ACTIVE);
        Student student2 = new Student(2L, "Мария","Леонова", 20, faculty1, StudentStatus.ACTIVE);
        Student student3 = new Student(3L, "Марат", "Измалков",18, faculty1, StudentStatus.ACTIVE);
        Student student4 = new Student(4L, "Софья", "Афонина", 18, faculty1, StudentStatus.ACTIVE);
        Student student5 = new Student(5L, "Михаил", "Бачурин", 19, null, StudentStatus.ACTIVE);
        studentsTest = new ArrayList<>(List.of(student1, student2, student3, student4, student5));
        AvatarDto avatarDto = new AvatarDto(null, "fail.path", "path.preview", student1.getId());
// 1. Создаем StudentDTO (то, что маппер отдаст в конце)
        StudentDTO sDto1 = new StudentDTO(1L, 23, "Артём", "Смирнов", "Химия", avatarDto, StudentStatus.ACTIVE, "79536160678", "123-456");
        StudentDTO sDto2 = new StudentDTO(2L, 20, "Мария", "Леонова", "Химия", avatarDto, StudentStatus.ACTIVE, "79536160679", "123-457");
        StudentDTO sDto3 = new StudentDTO(3L, 18, "Марат", "Измалков", "Химия", avatarDto, StudentStatus.ACTIVE, "79536160680", "123-458");
        StudentDTO sDto4 = new StudentDTO(4L, 18, "Софья", "Афонина", "Химия", avatarDto, StudentStatus.ACTIVE, "79536160681", "123-459");
        StudentDTO sDto5 = new StudentDTO(5L, 19, "Михаил", "Бачурин", null, avatarDto, StudentStatus.ACTIVE, "79536160682", "123-460");

        studentDtosTest = new ArrayList<>(List.of(sDto1, sDto2, sDto3, sDto4, sDto5));

// 2. Создаем CreateStudentDto (то, что придет в метод createStudent)
        CreateStudentDto cDto1 = new CreateStudentDto(23, 1L, "Артём", "Смирнов", "79536160678", StudentStatus.ACTIVE, "123-456");
        CreateStudentDto cDto2 = new CreateStudentDto(20, 1L, "Мария", "Леонова", "79536160679", StudentStatus.ACTIVE, "123-457");
        CreateStudentDto cDto3 = new CreateStudentDto(18, 1L, "Марат", "Измалков", "79536160680", StudentStatus.ACTIVE, "123-458");
        CreateStudentDto cDto4 = new CreateStudentDto(18, 1L, "Софья", "Афонина", "79536160681", StudentStatus.ACTIVE, "123-459");
        CreateStudentDto cDto5 = new CreateStudentDto(19, null, "Михаил", "Бачурин", "79536160682", StudentStatus.ACTIVE, "123-460");

        createDtosTest = new ArrayList<>(List.of(cDto1, cDto2, cDto3, cDto4, cDto5));
    }

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private DataCodecService dataCodecService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private CreateStudentDto createStudentDto;

    @InjectMocks
    private StudentService studentService;

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void createStudent(int index) {
        CreateStudentDto inputDto = createDtosTest.get(index);
        Student mappedEntity = studentsTest.get(index);
        Faculty faculty = mappedEntity.getFaculty();
        StudentDTO expectedResult = studentDtosTest.get(index);

        when(studentMapper.toEntity(inputDto)).thenReturn(mappedEntity);
        when(dataCodecService.encodePhone(anyString())).thenReturn("ENCODED_123");
        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        // when(studentRepository.save(any(Student.class))).thenReturn(testStudent); // просто возвращает
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));// проверяем правильность создания перед отправкой в базу
        when(studentMapper.toDto(any(Student.class))).thenReturn(expectedResult);
        StudentDTO result = studentService.createStudent(inputDto);

        assertNotNull(result);
        assertEquals(expectedResult.getFirstName(), result.getFirstName());
        assertEquals(expectedResult.getFaculty(), result.getFaculty());
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(dataCodecService).encodePhone(inputDto.getPhoneNumber());
    }

    @Test
    void resolveFacultyOtThrowStudentFacultyNull() {
        CreateStudentDto dto = createDtosTest.get(0);
        when(facultyRepository.findById(dto.getIdFaculty())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                studentService.getFacultyOrThrow(dto.getIdFaculty()));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void resolveFacultyOtThrowStudentFacultyNotFound() {
        Long id = 10L;
        when(facultyRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                studentService.getFacultyOrThrow(id));
    }

    @DisplayName("Поиск студента успешны")
    @Test
    void studentIdSuccess() {
        Long searchId = 1L;
        Student expectedStudent = studentsTest.get(0);
        when(studentRepository.findById(searchId)).thenReturn(Optional.of(expectedStudent));
        Student result = studentService.getStudentOrThrow(searchId);
        assertEquals(searchId, result.getId(), "ID вернувшегося студента должен быть равен запрошенному");
        verify(studentRepository).findById(searchId);
    }

    @ParameterizedTest
    @ValueSource(longs = {6L, 7L, 8L, 9L, 10L})
    void studentNotFound(Long longs) {
        when(studentRepository.findById(longs)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                studentService.getStudentOrThrow(longs));
        verify(studentRepository, times(1)).findById(longs);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void updateStudent(int index) {
        Student testStudent = studentsTest.get(index);
        Faculty testFaculty = facultyTest.get(1);
        String newName = "NewName";

        when(studentRepository.existsById(testStudent.getId())).thenReturn(true);

        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        testStudent.setFirstName(newName);
        testStudent.setFaculty(testFaculty);
        Student result = studentService.editStudent(testStudent);
        assertNotNull(result);
        assertEquals(newName, result.getFirstName());
        assertEquals(testFaculty.getId(), result.getFaculty().getId());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void studentNotFoundForUpdater(int index) {
        Student testStudent = studentsTest.get(index);
        when(studentRepository.existsById(testStudent.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                studentService.editStudent(testStudent));
        verify(studentRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void studentDeleteNotFound(Long longs) {
        when(studentRepository.findById(longs)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                studentService.deleteStudent(longs));
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

        when(studentRepository.findById(testStudent.getId())).thenReturn(Optional.of(testStudent));

        Faculty result = studentService.getFacultyByStudentId(testStudent.getId());
        assertNotNull(result);
        assertEquals(testStudent.getFaculty().getId(), result.getId());
        assertEquals(testStudent.getFaculty().getName(), result.getName());

    }

    @Test
    void findFacultyByStudentIdStudentFacultyIdNull() {
        Student testStudent = studentsTest.get(4);

        assertThrows(NotFoundException.class, () ->
                studentService.getFacultyByStudentId(testStudent.getId()));
    }

    @Test
    void facultyNotFoundInRepository() {
        Student testStudent = studentsTest.get(0);
        Long facultyId = testStudent.getFaculty().getId();
        when(studentRepository.findById(facultyId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                studentService.getFacultyByStudentId(testStudent.getId()));
    }

    @Test
    @DisplayName("Тест на экспорт студентов в CSV")
    void testExportToCsv() {
        when(studentRepository.findAll()).thenReturn(studentsTest);

        String csv = studentService.exportStudentToCsv(); // так как наш файл это строка

        assertNotNull(csv);
        assertTrue(csv.contains(studentsTest.get(0).getFirstName()));

        verify(studentRepository).findAll();
    }
}
