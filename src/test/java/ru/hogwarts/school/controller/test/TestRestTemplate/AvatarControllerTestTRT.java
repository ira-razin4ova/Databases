package ru.hogwarts.school.controller.test.TestRestTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import ru.hogwarts.school.avatar.AvatarController;
import ru.hogwarts.school.avatar.dto.AvatarDto;
import ru.hogwarts.school.avatar.AvatarMapper;
import ru.hogwarts.school.avatar.Avatar;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.user.enums.Status;
import ru.hogwarts.school.avatar.AvatarRepository;
import ru.hogwarts.school.faculty.FacultyRepository;
import ru.hogwarts.school.user.UserRepository;

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
    private UserRepository userRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
private AvatarMapper avatarMapper;

    @Autowired
    private TestRestTemplate testRestTemplate;

    // Вспомогательный методы
    private Faculty createFaculty() {
        return facultyRepository.save(new Faculty(null, "Test", "Test"));
    }

    private User createTestStudent(Faculty faculty) {
        return userRepository.save(new User(null, "Temp Student", "Test Student", 20, faculty, Status.ACTIVE));
    }

    private Avatar createTestAvatar(User user) {
        Avatar avatar = new Avatar();
        avatar.setStudent(user);
        avatar.setFilePath("test/path/" + user.getId() + ".png");
        avatar.setMediaType(MediaType.IMAGE_PNG_VALUE);
        avatar.setFileSize(100L);
        avatar.setData(new byte[]{1, 2, 3});
        avatar.setPreview(new byte[]{1, 2, 3});
        avatar.setFilePathPreview("test/path/" + user.getId() + "preview.png");
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
        User testUser = createTestStudent(testFaculty);
        Avatar testAvatar = createTestAvatar(testUser);
        Long id = testAvatar.getId();
        System.out.println(id);

        ResponseEntity<byte[]> response = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/avatars/" + id + "/data",
                byte[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(testAvatar.getData());
        avatarRepository.delete(testAvatar);
        userRepository.delete(testUser);
        facultyRepository.delete(testFaculty);
    }

    @Test
    void avatarGetByIdPreview() {
        Faculty testFaculty = createFaculty();
        User testUser = createTestStudent(testFaculty);
        Avatar testAvatar = createTestAvatar(testUser);
        Long id = testAvatar.getId();
        System.out.println(id);

        ResponseEntity<byte[]> response = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/avatars/" + id + "/preview",
                byte[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(testAvatar.getPreview());
        avatarRepository.delete(testAvatar);
        userRepository.delete(testUser);
        facultyRepository.delete(testFaculty);
    }

    @Test
    void avatarStudentId() {
        Faculty testFaculty = createFaculty();
        User testUser = createTestStudent(testFaculty);
        Avatar testAvatar = createTestAvatar(testUser);
        Long id = testUser.getId();

        ResponseEntity<AvatarDto> response = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/avatars/student/" + id,
                AvatarDto.class
        );
        System.out.println("ТЕЛО ОШИБКИ: " + response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testAvatar.getId());
        assertThat(response.getBody().getFilePath()).isEqualTo(testAvatar.getFilePath());

        avatarRepository.delete(testAvatar);
        userRepository.delete(testUser);
        facultyRepository.delete(testFaculty);
    }

    @Test
    void shouldUploadAvatar() throws IOException {
        User testUser = createTestStudent(createFaculty());
        byte[] fakeFile = generateRealImageBytes();

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

     ByteArrayResource contentsAsResource = new ByteArrayResource(fakeFile) {
      @Override
    public String getFilename() {
         return "test-avatar.png";
      }
    };

     body.add("avatar", contentsAsResource);

        ResponseEntity<String> response = testRestTemplate.postForEntity("http://localhost:" + port + "/avatars/" + testUser.getId() + "/upload",
                new HttpEntity<>(body, new HttpHeaders()), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

}
