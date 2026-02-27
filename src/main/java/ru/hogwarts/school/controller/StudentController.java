package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    @GetMapping ("{id}")
    public Student getStudent (@PathVariable Long id) {
        return studentService.studentSearch(id);
    }

    @PostMapping
    public Student createStudent (@RequestBody Student student) {
        return studentService.creteStudent(student);
    }

    @PutMapping ("{id}")
    public Student updateStudent (@PathVariable Long id,
                                @RequestBody Student student) {
        student.setId(id);
        return studentService.updateStudent(student);
    }

    @DeleteMapping ("{id}")
    public Student deleteStudent (@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }

    @GetMapping
    public List <Student> getStudentsByAge (@RequestParam int age) {
        return studentService.findByAge(age);
    }
}
