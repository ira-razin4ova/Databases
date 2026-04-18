package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.hogwarts.school.constant.StudentStatus;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "age", nullable = false)
    private int age;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    @JsonIgnoreProperties("student")
    private Faculty faculty;

    @OneToOne (mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Avatar avatar;

    @Column (name = "student_status")
    @Enumerated(EnumType.STRING)
    private StudentStatus studentStatus;

    @Column (name = "phone_number")
    private String phoneNumber;

    @Column (name = "course")
    private Integer course;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    @Column (name = "student_tiсket")
    private String studentTicket;
    public Student(Long id, String firstName, String lastName, int age, Faculty faculty, StudentStatus studentStatus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.faculty = faculty;
        this.studentStatus = studentStatus;
    }

    public Student() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) && Objects.equals(firstName, student.firstName) && Objects.equals(lastName, student.lastName) && age == student.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, age);
    }

    @Override
    public String toString() {
        return "Student{" +
                "age=" + age +
                ", id=" + id +
                ", name='" + firstName + '\'' +
                '}';
    }
}
