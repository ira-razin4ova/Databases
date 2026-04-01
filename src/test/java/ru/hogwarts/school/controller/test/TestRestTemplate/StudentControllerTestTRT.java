package ru.hogwarts.school.controller.test.TestRestTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentStatus;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

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
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoaded() {
        assertThat(studentController).isNotNull();
    }

    @Test
    void getStudent() {
        assertThat(this.testRestTemplate.getForEntity("http://localhost:" + port + "/student/1", String.class)).isNotNull();
    }

    @Test
    void getStudentNotValidId() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("http://localhost:" + port + "/student/-100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Ошибка валидации:");
    }

    @Test
    void postStudent() {
        Faculty testNewFaculty = facultyRepository.save(new Faculty(null, "test", "test"));
        Student newStudent = new Student();
        newStudent.setName("Test Tests");
        newStudent.setAge(25);
        newStudent.setFaculty(testNewFaculty);

        ResponseEntity<Student> response = testRestTemplate.postForEntity("http://localhost:" + port + "/student", newStudent, Student.class);
        Long id = response.getBody().getId();
        System.out.println(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isNotNull();
        assertEquals(response.getBody().getId(), id);
    }

    @Test
    void putStudent() {
        Faculty testNewFaculty = facultyRepository.save(new Faculty(null, "test", "test"));
        Student newStudent = studentRepository.save(new Student(null, "Sergei", 20, testNewFaculty, StudentStatus.ACTIVE));
        newStudent.setName("setName");
        newStudent.setAge(21);

        HttpEntity<Student> entity = new HttpEntity<>(newStudent);
        ResponseEntity<Student> response = testRestTemplate.exchange("http://localhost:" + port + "/student/" +
                        entity.getBody().getId(),
                HttpMethod.PUT,
                entity,
                Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("setName");
        assertThat(response.getBody().getAge()).isEqualTo(21);
    }

    @Test
    void deleteStudent() {
        Faculty testNewFaculty = facultyRepository.save(new Faculty(null, "test", "test"));
        Student newStudent = studentRepository.save(new Student(null, "Sergei", 20, testNewFaculty, StudentStatus.ACTIVE));
        Long id = newStudent.getId();

        ResponseEntity<Student> response = testRestTemplate.exchange("http://localhost:" + port + "/student/" + id,
                HttpMethod.DELETE,
                null,
                Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studentRepository.existsById(id)).isFalse();
    }

    @Test
    void getStudentsCsv() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("http://localhost:" + port + "/student/export/csv", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).contains("text/plain");
        String header = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().toString()).containsIgnoringCase(MediaType.TEXT_PLAIN_VALUE);
        assertThat(header).contains("filename=\"students.csv\"");
    }
}
