package ru.hogwarts.school.user.dto;

import lombok.*;
import ru.hogwarts.school.user.enums.Gender;
import ru.hogwarts.school.user.enums.Status;
import ru.hogwarts.school.avatar.dto.AvatarDto;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private LocalDate birthDate;
    private Gender gender;
    private String genderLocalized;
    private String firstName;
    private String lastName;
    private Long facultyId;
    private String faculty;
    private AvatarDto avatar;
    private Status status;
    private String phoneNumber;
    private String numberTicket;
    private Integer course;
    private String statusLocalized;
    private String roleLocalized;

}