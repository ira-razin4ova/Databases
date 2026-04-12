package ru.hogwarts.school.controller.test.TestRestTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTestTRT {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoaded() {
        assertThat(facultyController).isNotNull();
    }

    @Test
    void gatFaculty() {
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/faculty/2", String.class))
                .isNotEmpty();
    }

    @Test
    void gatFacultyNotValidId() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/-10",
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Ошибка валидации:");
    }

    @Test
    void postFaculty() {
        Faculty newFaculty = new Faculty(null, "TestName", "TestColor");
        ResponseEntity<Faculty> response = testRestTemplate.postForEntity("http://localhost:" + port + "/faculty", newFaculty, Faculty.class);
        Long id = response.getBody().getId();
        assertThat(response.getBody().getId()).isNotNull();
        assertEquals(response.getBody().getId(), id);
        assertThat(facultyRepository.existsById(id)).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void putFaculty() {

        Faculty newFaculty = facultyRepository.save(new Faculty(null, "TestName", "TestColor"));
        newFaculty.setName("NewName");
        Long id = newFaculty.getId();

        HttpEntity<Faculty> entity = new HttpEntity<>(newFaculty);
        ResponseEntity<Faculty> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/faculty/" + id,
                HttpMethod.PUT,
                entity,
                Faculty.class
        );

        assertThat(response.getBody().getName()).isEqualTo("NewName");
    }

    @Test
    void deleteFaculty() {
        Faculty newFaculty = facultyRepository.save(new Faculty(null, "TestName", "TestColor"));
        Long id = newFaculty.getId();

        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/faculty/" + id,
                HttpMethod.DELETE,
                null,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyRepository.existsById(id)).isFalse();

    }
}
