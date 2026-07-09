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
import ru.hogwarts.school.user.enums.Status;
import ru.hogwarts.school.avatar.dto.AvatarDto;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.user.dto.CreateUserDto;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.exception.notfound.EntityNotFoundException;
import ru.hogwarts.school.user.UserMapper;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.faculty.FacultyRepository;
import ru.hogwarts.school.user.UserRepository;
import ru.hogwarts.school.util.DataCodecService;
import ru.hogwarts.school.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private List<UserDto> userDtosTest;
    private List<User> studentsTest;
    private List<CreateUserDto> createDtosTest;
    private List<Faculty> facultyTest;

    @BeforeEach
    void setUp() {

        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        Faculty faculty2 = new Faculty(2L, "Физика", "Синий");
        facultyTest = new ArrayList<>(List.of(faculty1,
                faculty2));
        User user1 = new User(1L, "Артём", "Смирнов", 23, faculty1, Status.ACTIVE);
        User user2 = new User(2L, "Мария","Леонова", 20, faculty1, Status.ACTIVE);
        User user3 = new User(3L, "Марат", "Измалков",18, faculty1, Status.ACTIVE);
        User user4 = new User(4L, "Софья", "Афонина", 18, faculty1, Status.ACTIVE);
        User user5 = new User(5L, "Михаил", "Бачурин", 19, null, Status.ACTIVE);
        studentsTest = new ArrayList<>(List.of(user1, user2, user3, user4, user5));
        AvatarDto avatarDto = new AvatarDto(null, "fail.path", "path.preview", user1.getId());

// 1. Создаем StudentDTO (то, что маппер отдаст в конце)
        UserDto sDto1 = new UserDto(1L, 23, "Артём", "Смирнов", "Химия", avatarDto, Status.ACTIVE, "79536160678", "123-456",1, "Актный");
        UserDto sDto2 = new UserDto(2L, 20, "Мария", "Леонова", "Химия", avatarDto, Status.ACTIVE, "79536160679", "123-457",1, "Актный");
        UserDto sDto3 = new UserDto(3L, 18, "Марат", "Измалков", "Химия", avatarDto, Status.ACTIVE, "79536160680", "123-458",1, "Актный");
        UserDto sDto4 = new UserDto(4L, 18, "Софья", "Афонина", "Химия", avatarDto, Status.ACTIVE, "79536160681", "123-459",1, "Актный");
        UserDto sDto5 = new UserDto(5L, 19, "Михаил", "Бачурин", null, avatarDto, Status.ACTIVE, "79536160682", "123-460",1, "Актный");

        userDtosTest = new ArrayList<>(List.of(sDto1, sDto2, sDto3, sDto4, sDto5));

// 2. Создаем CreateStudentDto (то, что придет в метод createStudent)
        CreateUserDto cDto1 = new CreateUserDto(23, 1L, "Артём", "Смирнов", "79536160678", Status.ACTIVE, "123-456");
        CreateUserDto cDto2 = new CreateUserDto(20, 1L, "Мария", "Леонова", "79536160679", Status.ACTIVE, "123-457");
        CreateUserDto cDto3 = new CreateUserDto(18, 1L, "Марат", "Измалков", "79536160680", Status.ACTIVE, "123-458");
        CreateUserDto cDto4 = new CreateUserDto(18, 1L, "Софья", "Афонина", "79536160681", Status.ACTIVE, "123-459");
        CreateUserDto cDto5 = new CreateUserDto(19, null, "Михаил", "Бачурин", "79536160682", Status.ACTIVE, "123-460");

        createDtosTest = new ArrayList<>(List.of(cDto1, cDto2, cDto3, cDto4, cDto5));
    }

    @Mock
    private UserMapper userMapper;

    @Mock
    private DataCodecService dataCodecService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private CreateUserDto createUserDto;

    @InjectMocks
    private UserService userService;

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void createStudent(int index) {
        CreateUserDto inputDto = createDtosTest.get(index);
        User mappedEntity = studentsTest.get(index);
        Faculty faculty = mappedEntity.getFaculty();
        UserDto expectedResult = userDtosTest.get(index);

        when(userMapper.toEntity(inputDto)).thenReturn(mappedEntity);
        when(dataCodecService.encodePhone(anyString())).thenReturn("ENCODED_123");
        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        // when(studentRepository.save(any(Student.class))).thenReturn(testStudent); // просто возвращает
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));// проверяем правильность создания перед отправкой в базу
        when(userMapper.toDto(any(User.class))).thenReturn(expectedResult);
        UserDto result = userService.createStudent(inputDto);

        assertNotNull(result);
        assertEquals(expectedResult.getFirstName(), result.getFirstName());
        assertEquals(expectedResult.getFaculty(), result.getFaculty());
        verify(userRepository, times(1)).save(any(User.class));
        verify(dataCodecService).encodePhone(inputDto.getPhoneNumber());
    }

    @Test
    void resolveFacultyOtThrowStudentFacultyNull() {
        CreateUserDto dto = createDtosTest.get(0);
        when(facultyRepository.findById(dto.getIdFaculty())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                userService.getFacultyOrThrow(dto.getIdFaculty()));
        verify(userRepository, never()).save(any());
    }

    @Test
    void resolveFacultyOtThrowStudentFacultyNotFound() {
        Long id = 10L;
        when(facultyRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                userService.getFacultyOrThrow(id));
    }

    @DisplayName("Поиск студента успешны")
    @Test
    void studentIdSuccess() {
        Long searchId = 1L;
        User expectedUser = studentsTest.get(0);
        when(userRepository.findById(searchId)).thenReturn(Optional.of(expectedUser));
        User result = userService.getStudentOrThrow(searchId);
        assertEquals(searchId, result.getId(), "ID вернувшегося студента должен быть равен запрошенному");
        verify(userRepository).findById(searchId);
    }

    @ParameterizedTest
    @ValueSource(longs = {6L, 7L, 8L, 9L, 10L})
    void studentNotFound(Long longs) {
        when(userRepository.findById(longs)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                userService.getStudentOrThrow(longs));
        verify(userRepository, times(1)).findById(longs);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void updateStudent(int index) {
        User testUser = studentsTest.get(index);
        Faculty testFaculty = facultyTest.get(1);
        String newName = "NewName";

        when(userRepository.existsById(testUser.getId())).thenReturn(true);

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        testUser.setFirstName(newName);
        testUser.setFaculty(testFaculty);
        User result = userService.editStudent(testUser);
        assertNotNull(result);
        assertEquals(newName, result.getFirstName());
        assertEquals(testFaculty.getId(), result.getFaculty().getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void studentNotFoundForUpdater(int index) {
        User testUser = studentsTest.get(index);
        when(userRepository.existsById(testUser.getId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                userService.editStudent(testUser));
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void studentDeleteNotFound(Long longs) {
        when(userRepository.findById(longs)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.deleteStudent(longs));
        verify(userRepository, never()).deleteById(longs);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void studentDelete(Long longs) {
        User testUser = studentsTest.stream()
                .filter(s -> s.getId().equals(longs))
                .findFirst().get();
        when(userRepository.findById(longs)).thenReturn(Optional.of(testUser));
        userService.deleteStudent(longs);
        verify(userRepository, times(1)).delete(testUser);
    }


    @Test
    void searchStudentByAge() {
        int age = 20;

        List<User> expectedUsers = studentsTest.stream()
                .filter(s -> s.getAge() == age)
                .toList();

        when(userRepository.findByAge(age)).thenReturn(expectedUsers);

        List<UserDto> mockDtos = userDtosTest.stream()
                .filter(d -> d.getAge() == age)
                .toList();

        when(userMapper.toDtoList(expectedUsers)).thenReturn(mockDtos);

        List<UserDto> result = userService.findByAge(age);

        assertEquals(expectedUsers.size(), result.size()); // сколько в тестовом списке и сколько в списке результата
        assertTrue(result.stream().allMatch(s -> s.getAge() == age)); // проверяем содержимое списка результата, с учетом фильтра возраста
        verify(userRepository, times(1)).findByAge(age);
    }

    @Test
    void searchStudentByAgeNull() {
        int age = 10;

        List<User> expectedUsers = studentsTest.stream()
                .filter(s -> s.getAge() == age)
                .toList();

        when(userRepository.findByAge(age)).thenReturn(expectedUsers);

        List<UserDto> result = userService.findByAge(age);
        assertEquals(expectedUsers.size(), result.size());
    }

    @Test
    void searchStudentBetweenByAge() {
        int from = 20;
        int to = 30;

        List<User> expectedUsers = studentsTest.stream()
                .filter(s -> s.getAge() >= from && s.getAge() <= to)
                .toList();

        when(userRepository.findByAgeBetween(from, to)).thenReturn(expectedUsers);

        List<UserDto> expectedDto = userDtosTest.stream()
                .filter(s -> s.getAge() >= from && s.getAge() <= to)
                .toList();

        when(userMapper.toDtoList(expectedUsers)).thenReturn(expectedDto);

        List<UserDto> result = userService.findByAgeBetween(from, to);

        assertEquals(expectedUsers.size(), result.size());
        assertTrue(result.stream().allMatch(s -> s.getAge() >= from && s.getAge() <= to));
        verify(userRepository, times(1)).findByAgeBetween(from, to);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void findFacultyByStudentId(int index) {
        User testUser = studentsTest.get(index);

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        Faculty result = userService.getFacultyByStudentId(testUser.getId());
        assertNotNull(result);
        assertEquals(testUser.getFaculty().getId(), result.getId());
        assertEquals(testUser.getFaculty().getName(), result.getName());

    }

    @Test
    void findFacultyByStudentIdStudentFacultyIdNull() {
        User testUser = studentsTest.get(4);

        assertThrows(EntityNotFoundException.class, () ->
                userService.getFacultyByStudentId(testUser.getId()));
    }

    @Test
    void facultyNotFoundInRepository() {
        User testUser = studentsTest.get(0);
        Long facultyId = testUser.getFaculty().getId();
        when(userRepository.findById(facultyId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                userService.getFacultyByStudentId(testUser.getId()));
    }

    @Test
    @DisplayName("Тест на экспорт студентов в CSV")
    void testExportToCsv() {
        when(userRepository.findAll()).thenReturn(studentsTest);

        String csv = userService.exportStudentToCsv(); // так как наш файл это строка

        assertNotNull(csv);
        assertTrue(csv.contains(studentsTest.get(0).getFirstName()));

        verify(userRepository).findAll();
    }
}
