package ru.hogwarts.school.controller.test.TestRestTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.user.enums.Status;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.user.UserController;
import ru.hogwarts.school.user.dto.CreateUserDto;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.user.UserMapper;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.faculty.FacultyRepository;
import ru.hogwarts.school.user.UserRepository;
import ru.hogwarts.school.util.DataCodecService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTestTRT {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DataCodecService dataCodecService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoaded() {
        assertThat(userController).isNotNull();
    }

    @Test
    void getStudent() {
        assertThat(this.testRestTemplate.getForEntity("http://localhost:" + port + "/students/1", String.class)).isNotNull();
    }

    @Test
    void getStudentNotValidId() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("http://localhost:" + port + "/students/-100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //assertThat(response.getBody()).contains("Ошибка валидации:");
    }

    @Test
    void postStudent1() {
        CreateUserDto cDto1 = new CreateUserDto(23, 1L, "Артём", "Смирнов", "79536160678", Status.ACTIVE, "123-456");

        ResponseEntity<UserDto> response = testRestTemplate.postForEntity("http://localhost:" + port + "/students", cDto1, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

@Test
    void postStudent() {
    CreateUserDto cDto1 = new CreateUserDto(23, 1L, "Артём", "Смирнов", "79536160678", Status.ACTIVE, "123-456");
    HttpEntity<CreateUserDto> entity = new HttpEntity<>(cDto1);
    ResponseEntity<UserDto> response = testRestTemplate.exchange("http://localhost:" + port + "/students",
            HttpMethod.POST,
            entity,
            UserDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isNotNull();
}
    @Test
    void putStudent() {
        Faculty testNewFaculty = facultyRepository.save(new Faculty(null, "test1", "test1"));
        User newUser = userRepository.save(new User(null, "Sergei","Leonov", 20, testNewFaculty, Status.ACTIVE));
        newUser.setFirstName("setName");
        newUser.setAge(21);

        HttpEntity<User> entity = new HttpEntity<>(newUser);
        ResponseEntity<User> response = testRestTemplate.exchange("http://localhost:" + port + "/students/" +
                        entity.getBody().getId(),
                HttpMethod.PUT,
                entity,
                User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getFirstName()).isEqualTo("setName");
        assertThat(response.getBody().getAge()).isEqualTo(21);
    }

    @Test
    void deleteStudent() {
        Faculty testNewFaculty = facultyRepository.save(new Faculty(null, "test", "test"));
        User newUser = userRepository.save(new User(null, "Sergei", "Leonov",20, testNewFaculty, Status.ACTIVE));
        Long id = newUser.getId();
        System.out.println("student id" + id);

        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:" + port + "/students/" + id,
                HttpMethod.DELETE,
                null,
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findById(id)).isEmpty();
    }

    @Test
    void getStudentsCsv() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("http://localhost:" + port + "/students/export/csv", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).contains("text/plain");
        String header = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().toString()).containsIgnoringCase(MediaType.TEXT_PLAIN_VALUE);
        assertThat(header).contains("filename=\"students.csv\"");
    }
}
