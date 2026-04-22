package ru.hogwarts.school.dto.avatar;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
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

    public String getFilePath() {
        return filePath;
    }

    public String getFilePathPreview() {
        return filePathPreview;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }
}