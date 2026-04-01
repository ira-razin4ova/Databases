package ru.hogwarts.school.dto;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

public class StudentDTO {

    private Long id;
    private int age;
    private String name;
    private String faculty;

    public StudentDTO (Long id, int age, String name, String faculty) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.faculty = faculty;
    }

    public Long getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setName(String name) {
        this.name = name;
    }
}
