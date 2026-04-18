package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.AvatarMapper;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;
    private final AvatarMapper avatarMapper;

    public AvatarService(StudentService studentService,
                         AvatarRepository avatarRepository,
                         AvatarMapper avatarMapper) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
        this.avatarMapper = avatarMapper;
    }

    private String getExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public AvatarDto uploadAvatar (Long id, MultipartFile file) throws IOException {
        Student student = studentService.getStudentOrThrow(id);
        String contentType = file.getContentType();
        if (contentType == null || !List.of("image/jpeg", "image/png", "image/gif", "image/webp", "image/svg").contains(contentType)) {
            throw new BadRequestException("Это не картинка! Грузи только jpeg, png или gif.");
        }

        Path filePath = Path.of (avatarsDir, id + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }

        byte[] previewBytes = generateImagePreview(filePath);

        Path previewPath = Path.of(avatarsDir,
                id + "_preview." + getExtension(file.getOriginalFilename()));

        Files.deleteIfExists(previewPath);
        Files.write(previewPath, previewBytes);

        Avatar avatar = avatarRepository.findByStudentId(id)
                .orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFilePathPreview(previewPath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());
        avatar.setPreview(previewBytes);

        Avatar savedAvatar = avatarRepository.save(avatar);

        return avatarMapper.toDto(savedAvatar);

    }

    public byte[] generateImagePreview (Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                throw new BadRequestException("Не удалось прочитать картинку!");
            }
            /**
              *Исправляем деление на ноль (через double)
              * Исправляем тип 0 (TYPE_CUSTOM)
             */

            double ratio = (double) image.getWidth() / 100;
            int height = (int) (image.getHeight() / ratio);
            if (height <= 0) height = 1;
            int type = (image.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : image.getType();

            BufferedImage preview = new BufferedImage(100, height, type);
            Graphics2D graphics = preview.createGraphics();

            /**
             * Добавляем сглаживание, чтобы превью не было "пиксельным"
             */

            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    public AvatarDto findAvatarIdStudent (Long id) {
        Avatar avatar = avatarRepository.findByStudentId(id)
                .orElseThrow(() -> new NotFoundException("Аватар для студента с id " + id + " не найден"));
        return avatarMapper.toDto(avatar);
    }
    public Avatar getAvatarOrThrow(Long id) {
        return avatarRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Аватар с id " + id + " не найден"));
    }

    public byte[] getAvatarDataFromFile(Long id) throws IOException {
        Avatar avatar = getAvatarOrThrow(id);
        return Files.readAllBytes(Path.of(avatar.getFilePath()));
    }

    public List <AvatarDto> getAvatarPagingSorting (Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        List <Avatar> avatars = avatarRepository.findAll(pageRequest).getContent();
        return avatarMapper.toDtoList(avatars);
    }
}
