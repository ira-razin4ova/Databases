package ru.hogwarts.school.dto.student;

import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hogwarts.school.constant.StudentStatus;
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.model.Avatar;

@Setter
@NoArgsConstructor
public class StudentDTO {

    private Long id;
    private int age;
    private String firstName;
    private String lastName;
    private String faculty;
    private AvatarDto avatar;
    private StudentStatus studentStatus;
    private String phoneNumber;
    private String numberTicket;

    public StudentDTO(Long id, int age, String firstName, String lastName, String faculty, AvatarDto avatar, StudentStatus studentStatus, String numberPhone, String numberTicket) {
        this.id = id;
        this.age = age;
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty = faculty;
        this.avatar = avatar;
        this.studentStatus = studentStatus;
        this.phoneNumber = numberPhone;
        this.numberTicket = numberTicket;

    }

    public int getAge() {
        return age;
    }

    public AvatarDto getAvatarDto() {
        return avatar;
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