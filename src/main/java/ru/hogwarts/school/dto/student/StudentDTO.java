package ru.hogwarts.school.dto.student;

import ru.hogwarts.school.constant.StudentStatus;

public class StudentDTO {

    private Long id;
    private int age;
    private String firstName;
    private String lastName;
    private String faculty;
    private Long avatarId;
    private String avatarPreviewPath;
    private StudentStatus studentStatus;

    public StudentDTO(Long id,
                      int age,
                      String firstName,
                      String lastName,
                      String faculty,
                      Long avatarId,
                      String avatarPreviewPath,
                      StudentStatus studentStatus) {
        this.id = id;
        this.age = age;
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty = faculty;
        this.avatarId = avatarId;
        this.avatarPreviewPath = avatarPreviewPath;
        this.studentStatus = studentStatus;
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
}