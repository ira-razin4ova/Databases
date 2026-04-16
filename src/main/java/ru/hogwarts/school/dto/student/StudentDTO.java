package ru.hogwarts.school.dto.student;

import lombok.Setter;
import ru.hogwarts.school.constant.StudentStatus;
@Setter
public class StudentDTO {

    private Long id;
    private int age;
    private String firstName;
    private String lastName;
    private String faculty;
    private Long avatarId;
    private String avatarPreviewPath;
    private StudentStatus studentStatus;
    private String phoneNumber;
    private String numberTicket;

    public StudentDTO(Long id, int age, String firstName, String lastName, String faculty, Long avatarId, String avatarPreviewPath, StudentStatus studentStatus, String numberPhone, String numberTicket) {
        this.id = id;
        this.age = age;
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty = faculty;
        this.avatarId = avatarId;
        this.avatarPreviewPath = avatarPreviewPath;
        this.studentStatus = studentStatus;
        this.phoneNumber = numberPhone;
        this.numberTicket = numberTicket;

    }

    public int getAge() {
        return age;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public String getAvatarPreviewPath() {
        return avatarPreviewPath;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getFirstName() {
        return firstName;
    }

    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public StudentStatus getStudentStatus() {
        return studentStatus;
    }

    public String getNumberPhone() {
        return phoneNumber;
    }

    public String getNumberTicket() {
        return numberTicket;
    }
}