package ru.hogwarts.school;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.exception.FacultyNotFound;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentStatus;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacultyServiceTest {

    private List<Student> studentsTest;
    private List<Faculty> facultyTest;

    @BeforeEach
    void setUp() {
        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        Faculty faculty2 = new Faculty(2L, "Физика", "Синий");
        Faculty faculty3 = new Faculty(3L, "Биология", "Зеленый");
        Faculty faculty4 = new Faculty(4L, "Математика", "Белый");

        facultyTest = new ArrayList<>(List.of(faculty1, faculty2, faculty3, faculty4));

        Student student1 = new Student(1L, "Артём", 23, faculty1, StudentStatus.ACTIVE);
        Student student2 = new Student(2L, "Мария", 20, faculty1, StudentStatus.ACTIVE);
        Student student3 = new Student(3L, "Марат", 18, faculty2, StudentStatus.ACTIVE);
        Student student4 = new Student(4L, "Софья", 18, faculty3, StudentStatus.ACTIVE);
        Student student5 = new Student(5L, "Михаил", 19, null, StudentStatus.ACTIVE);

        studentsTest = new ArrayList<>(List.of(student1, student2, student3, student4, student5));
    }

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyService facultyService;

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void createFaculty(int ints) {
        Faculty test = facultyTest.get(ints);

        when(facultyRepository.save(any(Faculty.class))).thenAnswer(i -> i.getArgument(0));
        Faculty result = facultyService.createFaculty(test);

        assertNotNull(result);
        assertEquals(test.getId(), result.getId());
        assertEquals(test.getName(), result.getName());
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void facultyByIdSuccess(int ints) {
        Faculty testfaculty = facultyTest.get(ints);
        Long id = testfaculty.getId();

        when(facultyRepository.findById(id)).thenReturn(Optional.of(testfaculty));

        Faculty result = facultyService.facultySearchId(id);

        assertNotNull(result);
        assertEquals(testfaculty.getId(), result.getId());
        assertEquals(testfaculty.getName(), result.getName());
        verify(facultyRepository, times(1)).findById(id);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void facultyByIdNotFound(int ints) {
        Faculty testfaculty = facultyTest.get(ints);
        Long id = testfaculty.getId();

        when(facultyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(FacultyNotFound.class, () -> {
            facultyService.facultySearchId(id);
        });
    }

    @ParameterizedTest
    @NullSource
    void facultyByIdNull(Long argument) {
        assertThrows(IllegalArgumentException.class, () -> {
            facultyService.facultySearchId(argument);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void editFacultySuccess(int ints) {
        Faculty testFaculty = facultyTest.get(ints);
        Long id = testFaculty.getId();
        String newName = "nawName";
        String newColor = "newColor";

        when(facultyRepository.existsById(id)).thenReturn(true);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(testFaculty);

        testFaculty.setName(newName);
        testFaculty.setColor(newColor);

        Faculty result = facultyService.editFaculty(testFaculty);

        assertNotNull(result);
        assertEquals(testFaculty.getName(), result.getName());
        assertEquals(testFaculty.getColor(), result.getColor());
        verify(facultyRepository, times(1)).save(testFaculty);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void editFacultyNotFound(int ints) {
        Faculty testFaculty = facultyTest.get(ints);
        Long id = testFaculty.getId();

        when(facultyRepository.existsById(id)).thenReturn(false);

        assertThrows(FacultyNotFound.class, () -> {
            facultyService.editFaculty(testFaculty);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void deleteFacultySuccess (int ints) {
        Faculty testFaculty = facultyTest.get(ints);
        Long id = testFaculty.getId();

        when(facultyRepository.findById(id)).thenReturn(Optional.of(testFaculty));

        facultyService.deleteFaculty(id);

        verify(facultyRepository, times(1)).delete(testFaculty);
    }
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void deleteFacultyNotFound (int ints) {
        Faculty testFaculty = facultyTest.get(ints);
        Long id = testFaculty.getId();

        when(facultyRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(FacultyNotFound.class, () -> {
            facultyService.deleteFaculty(id);
        });
    }

    @ParameterizedTest
    @ValueSource (longs = {1L, 2L, 3L})
    void studentSearchIdFaculty (Long facultyId) {
        List <Student> expectedStudent = studentsTest.stream()
                .filter(s -> s.getFaculty() != null && s.getFaculty().getId().equals(facultyId))
                .toList();
        when(studentRepository.findByFaculty_Id(facultyId)).thenReturn(expectedStudent);

        List<Student> result = studentRepository.findByFaculty_Id(facultyId);

        assertNotNull(result);
        assertEquals(expectedStudent.size(), result.size());
        assertTrue(result.stream().allMatch(s -> s.getFaculty() != null && s.getFaculty().getId().equals(facultyId)));
        verify(studentRepository, times(1)).findByFaculty_Id(facultyId);
    }
}
