package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "avatar")
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_path_preview")
    private String filePathPreview;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "media_type")
    private String mediaType;

    @Lob
    @Column(name = "data", columnDefinition = "oid")
    private byte[] data;

    @Lob
    @Column(name = "preview_avatar", columnDefinition = "oid")
    private byte[] preview;

    @OneToOne
    @JsonIgnoreProperties("student")
    @JoinColumn(name = "student_id")
    private Student student;

    public Avatar(String filePath, String filePathPreview, long fileSize, String mediaType, byte[] data, byte[] preview, Student student) {
        this.filePath = filePath;
        this.filePathPreview = filePathPreview;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
        this.data = data;
        this.preview = preview;
        this.student = student;
    }

    public Avatar() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avatar avatar = (Avatar) o;
        return fileSize == avatar.fileSize
                && Objects.equals(id, avatar.id)
                && Objects.equals(filePath, avatar.filePath)
                && Objects.equals(mediaType, avatar.mediaType)
                && Arrays.equals(data, avatar.data)
                && Objects.equals(student, avatar.student);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, filePath, mediaType, fileSize, student);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "data=" + Arrays.toString(data) +
                ", id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
                ", student=" + student +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFilePathPreview() {
        return filePathPreview;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public byte[] getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public byte[] getPreview() {
        return preview;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFilePathPreview(String filePathPreview) {
        this.filePathPreview = filePathPreview;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }
}
