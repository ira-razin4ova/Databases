package ru.hogwarts.school.dto.student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hogwarts.school.constant.StudentStatus;
import ru.hogwarts.school.dto.avatar.AvatarDto;

@Setter
@Getter
@NoArgsConstructor
public class StudentDto {

    private Long id;
    private int age;
    private String firstName;
    private String lastName;
    private String faculty;
    private AvatarDto avatar;
    private StudentStatus studentStatus;
    private String phoneNumber;
    private String numberTicket;
    private Integer course;


    public StudentDto(Long id, int age, String firstName, String lastName, String faculty, AvatarDto avatar, StudentStatus studentStatus, String numberPhone, String numberTicket,
                      Integer course) {
        this.id = id;
        this.age = age;
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty = faculty;
        this.avatar = avatar;
        this.studentStatus = studentStatus;
        this.phoneNumber = numberPhone;
        this.numberTicket = numberTicket;
        this.course = course;

    }
}