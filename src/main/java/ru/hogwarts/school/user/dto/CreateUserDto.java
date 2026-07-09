package ru.hogwarts.school.user.dto;
import lombok.*;
import ru.hogwarts.school.user.enums.Gender;
import ru.hogwarts.school.user.enums.Status;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateUserDto {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private Long idFaculty;
    private Status status;
    private String phoneNumber;
    private String email;
}