package ru.hogwarts.school.service.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.user.enums.Status;
import ru.hogwarts.school.avatar.dto.AvatarDto;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.faculty.FacultyMapper;
import ru.hogwarts.school.faculty.dto.CreateFacultyDto;
import ru.hogwarts.school.faculty.dto.FacultyDto;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.exception.notfound.EntityNotFoundException;
import ru.hogwarts.school.user.UserMapper;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.faculty.FacultyRepository;
import ru.hogwarts.school.user.UserRepository;
import ru.hogwarts.school.faculty.FacultyService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacultyServiceTest {

    private List<User> studentsTest;
    private List<UserDto> userDtosTest;
    private List<Faculty> facultyTest;
    private List<FacultyDto> facultyDtoList;
    private List<CreateFacultyDto> createFacultyDtoList;


    @BeforeEach
    void setUp() {
        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        Faculty faculty2 = new Faculty(2L, "Физика", "Синий");
        Faculty faculty3 = new Faculty(3L, "Биология", "Зеленый");
        Faculty faculty4 = new Faculty(4L, "Математика", "Белый");

        facultyTest = new ArrayList<>(List.of(faculty1, faculty2, faculty3, faculty4));

                    facultyDtoList = List.of(
                new FacultyDto(1L, "Химия", "Красный", new BigDecimal("1000.00")),
                new FacultyDto(2L, "Физика", "Синий", new BigDecimal("2500.50")),
                new FacultyDto(3L, "Биология", "Зеленый", new BigDecimal("0.00")),
                new FacultyDto(4L, "Математика", "Белый", new BigDecimal("50000.00"))
        );

        createFacultyDtoList = List.of(
                new CreateFacultyDto("Химия", "Красный"),
                new CreateFacultyDto("Физика", "Синий"),
                new CreateFacultyDto("Биология", "Зеленый"),
                new CreateFacultyDto("Математика", "Белый")
        );

        User user1 = new User(1L, "Артём", "Смирнов", 23, faculty1, Status.ACTIVE);
        User user2 = new User(2L, "Мария", "Леонова", 20, faculty1, Status.ACTIVE);
        User user3 = new User(3L, "Марат", "Измалков", 18, faculty1, Status.ACTIVE);
        User user4 = new User(4L, "Софья", "Афонина", 18, faculty1, Status.ACTIVE);
        User user5 = new User(5L, "Михаил", "Бачурин", 19, faculty1, Status.ACTIVE);

        studentsTest = new ArrayList<>(List.of(user1, user2, user3, user4, user5));

        AvatarDto avatarDto = new AvatarDto(null, "fail.path", "path.preview", user1.getId());

        UserDto sDto1 = new UserDto(1L, 23, "Артём", "Смирнов", "Химия", avatarDto, Status.ACTIVE, "79536160678", "123-456", 1, "Актный");
        UserDto sDto2 = new UserDto(2L, 20, "Мария", "Леонова", "Химия", avatarDto, Status.ACTIVE, "79536160679", "123-457", 1, "Актный");
        UserDto sDto3 = new UserDto(3L, 18, "Марат", "Измалков", "Химия", avatarDto, Status.ACTIVE, "79536160680", "123-458", 1, "Актный");
        UserDto sDto4 = new UserDto(4L, 18, "Софья", "Афонина", "Химия", avatarDto, Status.ACTIVE, "79536160681", "123-459", 1, "Актный");
        UserDto sDto5 = new UserDto(5L, 19, "Михаил", "Бачурин", null, avatarDto, Status.ACTIVE, "79536160682", "123-460", 1, "Актный");

        userDtosTest = new ArrayList<>(List.of(sDto1, sDto2, sDto3, sDto4, sDto5));
    }

    @Mock
    private UserRepository userRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private FacultyMapper facultyMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private FacultyDto facultyDto;

    @Mock
    private CreateFacultyDto createFacultyDto;

    @InjectMocks
    private FacultyService facultyService;

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void createFaculty(int ints) {
        CreateFacultyDto test = createFacultyDtoList.get(ints);
        System.out.println(test);
        Faculty faculty = facultyTest.get(ints);
        System.out.println(faculty);
        when(facultyMapper.toEntity(test)).thenReturn(faculty);

        when(facultyRepository.save(any(Faculty.class))).thenAnswer(i -> i.getArgument(0));

        when(facultyMapper.toDto(any(Faculty.class))).thenReturn(facultyDtoList.get(ints));

        FacultyDto result = facultyService.createFaculty(test);

        assertNotNull(result);
        assertEquals(test.color(), result.color());
        assertEquals(test.name(), result.name());
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void facultyByIdSuccess(int ints) {
        Faculty testfaculty = facultyTest.get(ints);
        Long id = testfaculty.getId();

        when(facultyRepository.findById(id)).thenReturn(Optional.of(testfaculty));

        Faculty result = facultyService.getFacultyOrThrow(id);

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

        assertThrows(EntityNotFoundException.class, () ->
                facultyService.getFacultyOrThrow(id));

    }

    @ParameterizedTest
    @NullSource
    void facultyByIdNull(Long argument) {
        assertThrows(EntityNotFoundException.class, () ->
                facultyService.getFacultyOrThrow(argument));
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

        assertThrows(EntityNotFoundException.class, () ->
                facultyService.editFaculty(testFaculty));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void deleteFacultySuccess(int ints) {
        Faculty testFaculty = facultyTest.get(ints);
        Long id = testFaculty.getId();

        when(facultyRepository.findById(id)).thenReturn(Optional.of(testFaculty));

        facultyService.deleteFaculty(id);

        verify(facultyRepository, times(1)).delete(testFaculty);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void deleteFacultyNotFound(int ints) {
        Faculty testFaculty = facultyTest.get(ints);
        Long id = testFaculty.getId();

        when(facultyRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                facultyService.deleteFaculty(id));
    }

    @ParameterizedTest
    @CsvSource({
            "Биология, null",
            "null, Красный",
            "Биология, Красный"
    })
    void findByNameOrColorTest(String name, String color) {
        // 1. Подготовка: создаем список, который "якобы" нашла база

        List<Faculty> expectedFaculties = facultyTest.stream()
                .filter(f -> f.getName().equals(name) || f.getColor().equals(color))
                .toList();

        when(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color))
                .thenReturn(expectedFaculties);

        List<FacultyDto> expectedDtos = expectedFaculties.stream()
                .map(f -> new FacultyDto(f.getId(), f.getName(), f.getColor(), f.getBalance()))
                .toList();
        when(facultyMapper.toDtoList(expectedFaculties)).thenReturn(expectedDtos);

        List<FacultyDto> result = facultyService.findByNameOrColor(name, color);

        assertNotNull(result);
        assertEquals(expectedFaculties.size(), result.size());
        verify(facultyRepository).findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void studentSearchIdFaculty(Long facultyId) {
        List<User> expectedUsers = studentsTest.stream()
                .filter(s -> s.getFaculty().equals(facultyId))
                .toList();

        when(userRepository.findByFaculty_Id(facultyId)).thenReturn(expectedUsers);

        List<UserDto> result = facultyService.studentsFacultyById(facultyId);

        assertNotNull(result);
        assertEquals(expectedUsers.size(), result.size());
        assertTrue(result.stream().allMatch(s -> s.getFaculty().equals(facultyId)));
        verify(userRepository, times(1)).findByFaculty_Id(facultyId);
    }

    @TestFactory
    @DisplayName("Динамическая проверка всех факультетов на ошибку поиска")
    Stream<DynamicTest> dynamicFacultiesNotFoundTest() {
        // Идем по твоему списку из setUp
        return facultyTest.stream().map(faculty -> {
            // Вытаскиваем данные для удобства
            Long id = faculty.getId();
            String name = faculty.getName();

            // Создаем отдельный минитест для каждого элемента
            return DynamicTest.dynamicTest("Факультет: " + name + " [ID: " + id + "]", () -> {

                // 1. Обучаем мок (имитируем пустоту в базе)
                when(facultyRepository.findById(id)).thenReturn(Optional.empty());

                // 2. Ловим исключение
                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                        facultyService.getFacultyOrThrow(id)
                );

                // 3. Проверяем твой текст ошибки
               assertEquals("Факультет с таким " + id + " не найден", exception.getMessage());

                // Бонус: проверяем, что репозиторий вызывался именно с этим ID
                verify(facultyRepository).findById(id);
            });
        });
    }
}