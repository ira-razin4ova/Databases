package ru.hogwarts.school.controller.test.WebMvcTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.user.enums.Status;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.user.UserController;
import ru.hogwarts.school.avatar.dto.AvatarDto;
import ru.hogwarts.school.user.dto.CreateUserDto;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.user.UserMapper;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.faculty.FacultyRepository;
import ru.hogwarts.school.util.DataCodecService;
import ru.hogwarts.school.faculty.FacultyService;
import ru.hogwarts.school.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTestWMT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private DataCodecService dataCodecService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private FacultyService facultyService;

    private List<User> studentsTest;
    private List<UserDto> userDtosTest;
    private List<Faculty> facultyTest;

    @BeforeEach
    void setUp() {
        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        Faculty faculty2 = new Faculty(2L, "Физика", "Синий");

        facultyTest = new ArrayList<>(List.of(faculty1, faculty2));

        User user1 = new User(1L, "Артём", "Смирнов", 23, faculty1, Status.ACTIVE);
        User user2 = new User(2L, "Мария", "Зайцева", 20, faculty1, Status.ACTIVE);
        User user3 = new User(3L, "Марат", "Афонин", 18, faculty1, Status.ACTIVE);
        User user4 = new User(4L, "Михаил", "Башаров", 19, null, Status.ACTIVE);
        studentsTest = new ArrayList<>(List.of(user1, user2, user3, user4));

        AvatarDto avatarDto = new AvatarDto(null, "fail.path", "path.preview", user1.getId());

        UserDto sDto1 = new UserDto(1L, 23, "Артём", "Смирнов", "Химия", avatarDto, Status.ACTIVE, "79536160678", "123-456",1, "Актный");
        UserDto sDto2 = new UserDto(2L, 20, "Мария", "Леонова", "Химия", avatarDto, Status.ACTIVE, "79536160679", "123-457",1, "Актный");
        UserDto sDto3 = new UserDto(3L, 18, "Марат", "Измалков", "Химия", avatarDto, Status.ACTIVE, "79536160680", "123-458",1, "Актный");
        UserDto sDto4 = new UserDto(4L, 18, "Софья", "Афонина", "Химия", avatarDto, Status.ACTIVE, "79536160681", "123-459",1, "Актный");
        UserDto sDto5 = new UserDto(5L, 19, "Михаил", "Бачурин", null, avatarDto, Status.ACTIVE, "79536160682", "123-460",1, "Актный");

        userDtosTest = new ArrayList<>(List.of(sDto1, sDto2, sDto3, sDto4, sDto5));
    }

    @Test
    void findByIdStudent() throws Exception {

        User testUser = studentsTest.get(0);

        when(userService.getUserOrThrow(testUser.getId())).thenReturn(testUser);

        mockMvc.perform(get("/students/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.age").value(testUser.getAge()));
    }

    @Test
    void createStudent() throws Exception {

        CreateUserDto cDto1 = new CreateUserDto(23, 1L, "Артём", "Смирнов", "79536160678", Status.ACTIVE, "123-456");
        AvatarDto avatarDto = new AvatarDto(null, "fail.path", "path.preview", 1L);
        UserDto sDto1 = new UserDto(1L, 23, "Артём", "Смирнов", "Химия",avatarDto , Status.ACTIVE, "79536160678", "123-456",1, "Актный");

        when(userService.createUser(any(CreateUserDto.class))).thenReturn(sDto1);

        mockMvc.perform(post("/students")
                        .content(objectMapper.writeValueAsString(cDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void editStudent() throws Exception {

        User testUser = studentsTest.get(0);
        testUser.setFirstName("New Name");
        testUser.setAge(30);

        when(userService.editUser(testUser)).thenReturn(testUser);

        mockMvc.perform(put("/students/" + testUser.getId())
                        .content(objectMapper.writeValueAsString(testUser)
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.age").value(testUser.getAge()));
    }

    @Test
    void deleteStudent() throws Exception {

        User testUser = studentsTest.get(0);

        doNothing().when(userService).deleteUser(testUser.getId());

        mockMvc.perform(delete("/students/" + testUser.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(testUser.getId());
    }

    @Test
    void returnBadRequestWhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/students/-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findStudentAge() throws Exception {

        int age = 20;

        List<UserDto> expectedStudent = userDtosTest.stream()
                .filter(student -> student.getAge() >= age)
                .collect(Collectors.toUnmodifiableList());

        when(userService.findByAge(age)).thenReturn(expectedStudent);

        mockMvc.perform(get("/students")
                        .param("age", String.valueOf(age)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedStudent.size()));
    }

    @Test
    void findStudentBetweenAge() throws Exception {

        int fromAge = 18;
        int toAge = 20;

        List<UserDto> expectedStudent = userDtosTest.stream()
                .filter(student -> student.getAge() >= fromAge && student.getAge() <= toAge)
                .collect(Collectors.toUnmodifiableList());

        when(userService.findByAgeBetween(fromAge, toAge)).thenReturn(expectedStudent);

        mockMvc.perform(get("/students/age")
                        .param("from", String.valueOf(fromAge))
                        .param("to", String.valueOf(toAge)))
                .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(expectedStudent.size()));
    }
    @Test
    void shouldDownloadStudentsCsv() throws Exception {

        String expectedContent = "id,name,age\n1,Student1,20\n";

        when(userService.exportUserToCsv()).thenReturn(expectedContent);

        mockMvc.perform(get("/students/export/csv"))
                .andExpect(status().isOk())

                .andExpect(content().contentType("text/plain")) //тип контента должен быть text/csv
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"students.csv\"")) // заголовки
                .andExpect(result -> {
                    String actualContent = result.getResponse().getContentAsString(); // содержимое
                    assertTrue(actualContent.contains("Student1")); // сравнение содержимого
                });
    }
}
