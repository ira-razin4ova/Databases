package ru.hogwarts.school.controller.test.TestRestTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import ru.hogwarts.school.controller.AvatarController;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentStatus;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AvatarControllerTestTRT {

    @LocalServerPort
    private int port;

    @Autowired
    private AvatarController avatarController;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    // Вспомогательный методы
    private Faculty createFaculty() {
        return facultyRepository.save(new Faculty(null, "Test", "Test"));
    }

    private Student createTestStudent(Faculty faculty) {
        return studentRepository.save(new Student(null, "Temp Student", 20, faculty, StudentStatus.ACTIVE));
    }

    private Avatar createTestAvatar(Student student) {
        Avatar avatar = new Avatar();
        avatar.setStudent(student);
        avatar.setFilePath("test/path/" + student.getId() + ".png");
        avatar.setMediaType(MediaType.IMAGE_PNG_VALUE);
        avatar.setFileSize(100L);
        avatar.setData(new byte[]{1, 2, 3});
        avatar.setPreview(new byte[]{1, 2, 3});
        return avatarRepository.save(avatar);
    }

    private byte [] generateRealImageBytes () throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_BGR);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write (image, "png", baos);
        return baos.toByteArray();
    }

    @Test
    void contextLoaded() {
        assertThat(avatarController).isNotNull();
    }

    @Test
    void avatarGetByIdData() {
        Faculty testFaculty = createFaculty();
        Student testStudent = createTestStudent(testFaculty);
        Avatar testAvatar = createTestAvatar(testStudent);
        Long id = testAvatar.getId();
        System.out.println(id);

        ResponseEntity<byte[]> response = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/avatar/" + id + "/data",
                byte[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(testAvatar.getData());
        avatarRepository.delete(testAvatar);
        studentRepository.delete(testStudent);
        facultyRepository.delete(testFaculty);
    }

    @Test
    void avatarGetByIdPreview() {
        Faculty testFaculty = createFaculty();
        Student testStudent = createTestStudent(testFaculty);
        Avatar testAvatar = createTestAvatar(testStudent);
        Long id = testAvatar.getId();
        System.out.println(id);

        ResponseEntity<byte[]> response = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/avatar/" + id + "/preview",
                byte[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(testAvatar.getPreview());
        avatarRepository.delete(testAvatar);
        studentRepository.delete(testStudent);
        facultyRepository.delete(testFaculty);
    }

    @Test
    void avatarStudentId() {
        Faculty testFaculty = createFaculty();
        Student testStudent = createTestStudent(testFaculty);
        Avatar testAvatar = createTestAvatar(testStudent);
        Long id = testStudent.getId();

        ResponseEntity<Avatar> response = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/avatar/" + id,
                Avatar.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testAvatar.getId());
        assertThat(response.getBody().getFilePath()).isEqualTo(testAvatar.getFilePath());

        avatarRepository.delete(testAvatar);
        studentRepository.delete(testStudent);
        facultyRepository.delete(testFaculty);
    }

    @Test
    void shouldUploadAvatar() throws IOException {
        Student testStudent = createTestStudent(createFaculty());
        byte[] fakeFile = generateRealImageBytes();

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

//        HttpHeaders partHeaders = new HttpHeaders();
//        partHeaders.setContentType(MediaType.IMAGE_PNG); // делаем фейковые заголовки, будь-то это наш файл
//
//        HttpEntity<byte[]> fileEntity = new HttpEntity<>(fakeFile, partHeaders);

     ByteArrayResource contentsAsResource = new ByteArrayResource(fakeFile) {
      @Override
    public String getFilename() {
         return "test-avatar.png"; // Контроллер увидит это как имя файла
        }
    }; //так же можно сделать с помощью специального класса, он позволит имя файла задать

        body.add("avatar", contentsAsResource);

        ResponseEntity<String> response = testRestTemplate.postForEntity("http://localhost:" + port + "/avatar/" + testStudent.getId() + "/upload",
                new HttpEntity<>(body, new HttpHeaders()), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
