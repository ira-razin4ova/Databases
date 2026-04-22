package ru.hogwarts.school.controller.test.TestRestTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.StudentDto;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.constant.StudentStatus;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.DataCodecService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTestTRT {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentController studentController;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private DataCodecService dataCodecService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoaded() {
        assertThat(studentController).isNotNull();
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
        CreateStudentDto cDto1 = new CreateStudentDto(23, 1L, "Артём", "Смирнов", "79536160678", StudentStatus.ACTIVE, "123-456");

        ResponseEntity<StudentDto> response = testRestTemplate.postForEntity("http://localhost:" + port + "/students", cDto1, StudentDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

@Test
    void postStudent() {
    CreateStudentDto cDto1 = new CreateStudentDto(23, 1L, "Артём", "Смирнов", "79536160678", StudentStatus.ACTIVE, "123-456");
    HttpEntity<CreateStudentDto> entity = new HttpEntity<>(cDto1);
    ResponseEntity<StudentDto> response = testRestTemplate.exchange("http://localhost:" + port + "/students",
            HttpMethod.POST,
            entity,
            StudentDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isNotNull();
}
    @Test
    void putStudent() {
        Faculty testNewFaculty = facultyRepository.save(new Faculty(null, "test", "test"));
        Student newStudent = studentRepository.save(new Student(null, "Sergei","Leonov", 20, testNewFaculty, StudentStatus.ACTIVE));
        newStudent.setFirstName("setName");
        newStudent.setAge(21);

        HttpEntity<Student> entity = new HttpEntity<>(newStudent);
        ResponseEntity<Student> response = testRestTemplate.exchange("http://localhost:" + port + "/students/" +
                        entity.getBody().getId(),
                HttpMethod.PUT,
                entity,
                Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getFirstName()).isEqualTo("setName");
        assertThat(response.getBody().getAge()).isEqualTo(21);
    }

    @Test
    void deleteStudent() {
        Faculty testNewFaculty = facultyRepository.save(new Faculty(null, "test", "test"));
        Student newStudent = studentRepository.save(new Student(null, "Sergei", "Leonov",20, testNewFaculty, StudentStatus.ACTIVE));
        Long id = newStudent.getId();
        System.out.println("student id" + id);

        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:" + port + "/students/" + id,
                HttpMethod.DELETE,
                null,
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studentRepository.findById(id)).isEmpty();
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
