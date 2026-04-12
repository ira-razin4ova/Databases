package ru.hogwarts.school.service.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.hogwarts.school.constant.StudentStatus;
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.AvatarMapper;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvatarServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private AvatarRepository avatarRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private AvatarMapper avatarMapper;

    @InjectMocks
    private AvatarService avatarService;

    private MockMultipartFile validImage;
    private MockMultipartFile illegalFile;
    private MockMultipartFile nullTypeFile;
    private Path tempDir;
    private List<Student> studentsTest;
    private List<Faculty> facultyTest;
    private AvatarDto dto;
    private Avatar avatar;

    @BeforeEach
    void setUp() throws IOException {

        ReflectionTestUtils.setField(avatarService, "avatarsDir", "src/test/resources/avatars");  // 1. Настраиваем путь к аватарам (чтобы не был null)

        BufferedImage simpleImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);  // Создаем картинку чтобы ImageIO не падал
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(simpleImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        validImage = new MockMultipartFile(
                "avatar", "avatar.png", "image/png", imageBytes);

        illegalFile = new MockMultipartFile(
                "avatar", "test.txt", "text/plain", "Not an image".getBytes());

        nullTypeFile = new MockMultipartFile("avatar", "avatar.png", null, "some image data".getBytes());

        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        facultyTest = new ArrayList<>(List.of(faculty1));

        Student student1 = new Student(1L, "Артём", "Смирнов", 23, faculty1, StudentStatus.ACTIVE);
        Student student2 = new Student(2L, "Мария","Леонова", 20, faculty1, StudentStatus.ACTIVE);
        Student student3 = new Student(3L, "Марат", "Измалков",18, faculty1, StudentStatus.ACTIVE);
        Student student4 = new Student(4L, "Софья", "Афонина", 18, faculty1, StudentStatus.ACTIVE);
        studentsTest = new ArrayList<>(List.of(student1, student2, student3, student4));
        dto = new AvatarDto(
                1L,
                "1.png",
                "1_preview.png"
        );
        avatar = new Avatar();
        avatar.setId(1L);
        avatar.setFilePath("1.png");
        avatar.setFilePathPreview("1_preview.png");
    }

    @Test
    void generateImagePreviewTest() throws IOException {
        Path testFilePath = Path.of("src/test/resources/avatars/test_unit.png"); // определяем, где будет лежать файл

        Files.createDirectories(testFilePath.getParent()); // создаем директорию если ее нет
        Files.write(testFilePath, validImage.getBytes()); // записываем файл
        byte[] result = avatarService.generateImagePreview(testFilePath);

        assertNotNull(result);
        boolean deleted = Files.deleteIfExists(testFilePath);
        assertTrue(deleted, "Файл должен быть успешно удален (значит, он не заблокирован потоками)");
    }

    @ParameterizedTest
    @CsvSource({
            "10, 10",
            "200, 200",
            "500, 300"
    })
    @DisplayName("Тест генерации превью для разных размеров картинок")
    void generateImagePreviewDifferentSizes(int width, int height) throws IOException {
        BufferedImage simpleImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR); // набор красок
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // буфер потока в оперативке для записи ImageIO
        ImageIO.write(simpleImage, "png", baos); // запись

        Path testFilePath = Path.of("src/test/resources/avatars/test_unit.png");
        Files.createDirectories(testFilePath.getParent());
        Files.write(testFilePath, baos.toByteArray());

        byte[] previewData = avatarService.generateImagePreview(testFilePath);

        assertNotNull(previewData);

        BufferedImage resultImage = ImageIO.read(new ByteArrayInputStream(previewData));
        assertEquals(100, resultImage.getWidth());
        boolean deleted = Files.deleteIfExists(testFilePath);
        assertTrue(deleted, "Файл должен быть успешно удален (значит, он не заблокирован потоками)");
    }

    @Test
    @DisplayName("Ошибка: файл не найден")
    void generateImagePreviewFileNotFound() {
        Path nonExistentPath = Path.of("src/test/resources/avatars/ghost_file.png"); // этого пути не существует

        try {
            Files.deleteIfExists(nonExistentPath); // что бы наверняка, проверим и удалим
        } catch (IOException ignored) {
        } // игнорируем ошибку

        assertThrows(IOException.class, () ->
                avatarService.generateImagePreview(nonExistentPath));

    }

    @Test
    void generateImagePreviewImageNull() throws IOException {

        Path fakeImagePath = Path.of("src/test/resources/avatars/fake_image.png");// файл, который притворяется картинкой, внутри текст
        Files.createDirectories(fakeImagePath.getParent());
        Files.write(fakeImagePath, "Это просто текстовая строка, а не байты картинки".getBytes());
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                avatarService.generateImagePreview(fakeImagePath));

        assertEquals("Не удалось прочитать картинку!", exception.getMessage());  // Проверяем сообщение внутри ошибки
        Files.deleteIfExists(fakeImagePath);
    }

    @Test
    void uploadAvatarFullSuccessTest() throws IOException {

        Student testStudent = studentsTest.get(0);


        when(studentService.getStudentById(1L))
                .thenReturn(testStudent);

        when(avatarRepository.findByStudentId(1L))
                .thenReturn(Optional.empty());

        when(avatarRepository.save(any(Avatar.class)))
                .thenReturn(avatar);

        when(avatarMapper.toDto(any(Avatar.class)))
                .thenReturn(dto);

        AvatarDto result = avatarService.uploadAvatar(testStudent.getId(), validImage);

        assertNotNull(result);

        assertEquals(1L, result.getId()); // или что ты туда кладёшь

        assertTrue(result.getFilePath().contains("1.png"));
        assertTrue(result.getFilePathPreview().contains("1_preview"));

        verify(avatarRepository, times(1)).save(any(Avatar.class));
    }

    @Test
    void uploadAvatarIllegalFileTest() {

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                avatarService.uploadAvatar(1L, illegalFile));
        assertEquals("Это не картинка! Грузи только jpeg, png или gif.", exception.getMessage());
    }

    @Test
    void uploadAvatarNullFileTest() {
        Student testStudent = studentsTest.get(0);

        when(studentService.getStudentById(testStudent.getId())).thenReturn(testStudent);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                avatarService.uploadAvatar(1L, nullTypeFile));

        assertEquals("Это не картинка! Грузи только jpeg, png или gif.", exception.getMessage());
    }

    @Test
    void findAvatarById() {
        Student testStudent = studentsTest.get(0);
        Avatar expectedAvatar = new Avatar();
        expectedAvatar.setId(10L);
        expectedAvatar.setStudent(testStudent);

        when(avatarRepository.findById(10L)).thenReturn(Optional.of(expectedAvatar));

        Avatar result = avatarService.getAvatar(10L);

        assertNotNull(result);
        assertEquals(expectedAvatar.getId(), result.getId());
    }

    @Test
    void findAvatarByIdStudent() {
        Student testStudent = studentsTest.get(0);
        Avatar expectedAvatar = new Avatar();
        expectedAvatar.setId(10L);
        expectedAvatar.setStudent(testStudent);
        testStudent.setAvatar(expectedAvatar);

        when(avatarRepository.findByStudentId(testStudent.getId())).thenReturn(Optional.of(expectedAvatar));

        Avatar result = avatarService.findAvatarIdStudent(testStudent.getId());

        assertNotNull(result);
        assertEquals(expectedAvatar.getId(), result.getId(), "ID аватара должен быть 10");
        assertEquals(testStudent.getId(), result.getStudent().getId(), "Это должен быть аватар нашего студента");
        assertEquals(testStudent.getAvatar().getId(), result.getId());
    }

    @Test
    void findAvatarByIdStudentNull() {
        Student testStudent = studentsTest.get(0);

        when(avatarRepository.findByStudentId(testStudent.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                avatarService.findAvatarIdStudent(testStudent.getId()));

        verify(avatarRepository, times(1)).findByStudentId(testStudent.getId());
    }



}