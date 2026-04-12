package ru.hogwarts.school.dto.avatar;

import lombok.Data;
public class AvatarDto {
    Long id;
    String filePath;
    String filePathPreview;

    public AvatarDto(Long id, String filePath, String filePathPreview) {
        this.id = id;
        this.filePath = filePath;
        this.filePathPreview = filePathPreview;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFilePathPreview() {
        return filePathPreview;
    }

    public Long getId() {
        return id;
    }
}