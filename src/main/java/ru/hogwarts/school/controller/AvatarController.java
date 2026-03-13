package ru.hogwarts.school.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }
    @PostMapping(value = "/{studentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long studentId,
                                               @RequestParam MultipartFile avatar) throws IOException {


        avatarService.uploadAvatar(studentId, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping ("/{id}")
    public Avatar findByIdStudent (@PathVariable Long id) {
        return avatarService.findAvatarId(id);
    }

    @GetMapping("/{id}/data")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long id) {

        Avatar avatar = avatarService.getAvatar(id);

        return ResponseEntity
                .ok()
                .header("Content-Type", avatar.getMediaType())
                .body(avatar.getData());
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> getPreview(@PathVariable Long id) {

        Avatar avatar = avatarService.getAvatar(id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(avatar.getPreview());
    }

    @GetMapping("/{id}/path-file")
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long id) throws IOException {

        Avatar avatar = avatarService.getAvatar(id);

        byte[] data = avatarService.getAvatarDataFromFile(id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(data);
    }

}
