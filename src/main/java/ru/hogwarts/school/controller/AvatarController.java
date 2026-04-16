package ru.hogwarts.school.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.util.List;

@Validated
@RestController
@RequestMapping("/avatars")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }
    @PostMapping(value = "/{studentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AvatarDto> uploadAvatar(
            @PathVariable @NotNull Long studentId,
            @RequestParam MultipartFile avatar) throws IOException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(avatarService.uploadAvatar(studentId, avatar));
    }

    @GetMapping ("student/{id}")
    public ResponseEntity <AvatarDto> findByIdStudent (@PathVariable @Positive Long id) {
        return ResponseEntity.ok(avatarService.findAvatarIdStudent(id));
    }

    @GetMapping("/{id}/data")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable @Positive Long id) {

        Avatar avatar = avatarService.getAvatarOrThrow(id);

        return ResponseEntity
                .ok()
                .header("Content-Type", avatar.getMediaType())
                .body(avatar.getData());
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> getPreview(@PathVariable @Positive Long id) {

        Avatar avatar = avatarService.getAvatarOrThrow(id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(avatar.getPreview());
    }

    @GetMapping("/{id}/path-file")
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable @Positive Long id) throws IOException {

        Avatar avatar = avatarService.getAvatarOrThrow(id);

        byte[] data = avatarService.getAvatarDataFromFile(id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(data);
    }

    @GetMapping ("/avatar-paging")
    public ResponseEntity <List<AvatarDto>> getAvatarPaging (@RequestParam ("page") Integer pageNumber, @RequestParam ("size") Integer pageSize) {
        return ResponseEntity.ok(avatarService.getAvatarPagingSorting(pageNumber, pageSize));
    }

}
