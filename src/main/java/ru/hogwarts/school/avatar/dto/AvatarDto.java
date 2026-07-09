package ru.hogwarts.school.avatar.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AvatarDto {
    private Long id;
    private String filePath;
    private String filePathPreview;
    private Long studentId;

    public AvatarDto(Long id, String filePath, String filePathPreview, Long studentId) {
        this.id = id;
        this.filePath = filePath;
        this.filePathPreview = filePathPreview;
        this.studentId = studentId;
    }
}