package ru.hogwarts.school.dto.student;
import lombok.Getter;
import lombok.Setter;
import ru.hogwarts.school.constant.StudentStatus;
@Setter
@Getter
public class CreateStudentDto {

    private String firstName;
    private String lastName;
    private int age;
    private Long idFaculty;
    private StudentStatus studentStatus;
    private String phoneNumber;
    private String studentTicket;

    public CreateStudentDto(int age,
                            Long idFaculty,
                            String firstName,
                            String lastName,
                            String phonNumber,
                            StudentStatus studentStatus,
                            String studentTicket) {
        this.age = age;
        this.idFaculty = idFaculty;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phonNumber;
        this.studentStatus = studentStatus;
        this.studentTicket = studentTicket;
    }
}
