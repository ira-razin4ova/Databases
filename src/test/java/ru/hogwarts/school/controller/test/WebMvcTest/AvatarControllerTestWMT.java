package ru.hogwarts.school.controller.test.WebMvcTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.controller.AvatarController;
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.constant.StudentStatus;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;// импортирует сразу все
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;// импортирует сразу все
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest (AvatarController.class)
public class AvatarControllerTestWMT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private AvatarService avatarService;

    private MockMultipartFile validImage;

    @BeforeEach
    void setUp() throws IOException {

        BufferedImage simpleImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);  // Создаем картинку чтобы ImageIO не падал
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(simpleImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

      validImage = new MockMultipartFile(
                "avatar", "avatar.png", "image/png", imageBytes);

    }

    Faculty facultyTest = new Faculty(1L, "Химия", "Красный");

    Student studentTest = new Student(1L, "Артём", "Смирнов", 23, facultyTest, StudentStatus.ACTIVE);


   @Test
    void uploadAvatar1() throws Exception {

        AvatarDto dto = new AvatarDto(
                1L,
                "path/to/file.png",
                "path/to/preview.png",
                1L
        );

        when(avatarService.uploadAvatar(eq(studentTest.getId()), any(MultipartFile.class)))
                .thenReturn(dto);

        mockMvc.perform(multipart("/avatars/" + studentTest.getId() + "/upload")
                        .file(validImage))
                .andExpect(status().isCreated()); // если ты поменяла на CREATED
    }

    @Test
    void avatarByIdStudent () throws Exception {

        AvatarDto dto = new AvatarDto(
                1L,
                "file.png",
                "preview.png",
                1L
        );

        when(avatarService.findAvatarIdStudent(studentTest.getId())).thenReturn(dto);

        mockMvc.perform(get("/avatars/student/" + 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.filePath").value("file.png"))
                .andExpect(jsonPath("$.filePathPreview").value("preview.png"));
    }

@Test
    void getAvatarFromDb () throws  Exception {

    Avatar newAvatar = new Avatar();
    newAvatar.setId(1L);
    newAvatar.setData(validImage.getBytes());
    newAvatar.setMediaType(validImage.getContentType());

    when(avatarService.getAvatarOrThrow(1L)).thenReturn(newAvatar);

    mockMvc.perform(get("/avatars/" +1L +"/data"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "image/png"))
            .andExpect(content().bytes(validImage.getBytes()));
}

    @Test
    void getAvatarPreview () throws  Exception {

        Avatar newAvatar = new Avatar();
        newAvatar.setId(1L);
        newAvatar.setPreview(validImage.getBytes());
        newAvatar.setMediaType(validImage.getContentType());

        when(avatarService.getAvatarOrThrow(1L)).thenReturn(newAvatar);

        mockMvc.perform(get("/avatars/" +1L +"/preview"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(content().bytes(validImage.getBytes()));
    }
    @Test
    void getAvatarFromFile () throws  Exception {

        Avatar newAvatar = new Avatar();
        newAvatar.setId(1L);
        newAvatar.setData(validImage.getBytes());
        newAvatar.setMediaType(validImage.getContentType());
        newAvatar.setFilePath("src/test/resources/avatars/avatar.png");

        when(avatarService.getAvatarOrThrow(1L)).thenReturn(newAvatar);

        when(avatarService.getAvatarDataFromFile(1L)).thenReturn(newAvatar.getData());

        mockMvc.perform(get("/avatars/" +1L +"/path-file"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(content().bytes(validImage.getBytes()));
        verify(avatarService).getAvatarOrThrow(1L);
        verify(avatarService).getAvatarDataFromFile(1L);
    }

}