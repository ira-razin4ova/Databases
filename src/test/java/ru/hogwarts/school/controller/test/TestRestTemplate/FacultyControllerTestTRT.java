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
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.faculty.FacultyController;
import ru.hogwarts.school.faculty.dto.CreateFacultyDto;
import ru.hogwarts.school.faculty.dto.FacultyDto;
import ru.hogwarts.school.faculty.FacultyRepository;
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/faculties/2", String.class))
                .isNotEmpty();
    }

    @Test
    void gatFacultyNotValidId() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/faculties/-10",
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("должно быть больше 0");
    }

    @Test
    void postFaculty() {

        CreateFacultyDto newFaculty = new CreateFacultyDto("TestName", "TestColor");

        ResponseEntity<FacultyDto> response = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/faculties",
                newFaculty,
                FacultyDto.class
        );

        FacultyDto createdFaculty = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdFaculty).isNotNull();
        assertThat(createdFaculty.id()).isNotNull();

        assertThat(facultyRepository.existsById(createdFaculty.id())).isTrue();
    }

    @Test
    void putFaculty() {

        Faculty newFaculty = facultyRepository.save(new Faculty(null, "TestName", "TestColor"));
        newFaculty.setName("NewName");
        Long id = newFaculty.getId();

        HttpEntity<Faculty> entity = new HttpEntity<>(newFaculty);
        ResponseEntity<Faculty> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/faculties/" + id,
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
                "http://localhost:" + port + "/faculties/" + id,
                HttpMethod.DELETE,
                null,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyRepository.existsById(id)).isFalse();

    }
}
