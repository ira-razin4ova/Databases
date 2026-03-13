package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

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

    public Student(String name, int age, Faculty faculty, StudentStatus studentStatus) {
        this.name = name;
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
        return id == student.id && Objects.equals(name, student.name) && age == student.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

    @Override
    public String toString() {
        return "Student{" +
                "age=" + age +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
