package ru.hogwarts.school.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
//@Validated Включает проверку всех аннотаций @Positive, @Min, @Max в этом классе
@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public Student getStudent(@PathVariable @Positive Long id) {
        return studentService.studentSearch(id);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.creteStudent(student);
    }

    @PutMapping("{id}")
    public Student updateStudent(@PathVariable @Positive Long id,
                                 @RequestBody Student student) {
        student.setId(id);
        return studentService.updateStudent(student);
    }

    @DeleteMapping("{id}")
    public Student deleteStudent(@PathVariable @Positive Long id) {
        return studentService.deleteStudent(id);
    }

    @GetMapping
    public List<Student> getStudentsByAge(@RequestParam int age) {
        return studentService.findByAge(age);
    }

    @GetMapping("/age")
    public List<Student> getFindByAgeBetween(@RequestParam int from,
                                             @RequestParam int to) {
        return studentService.findByAgeBetween(from, to);
    }

    @PostMapping("info-faculty")
    public Faculty getFacultyInfo(@RequestBody Student student) {
        return studentService.getFacultyByStudentId(student);
    }

    @GetMapping ("/exprt/csv")
    public ResponseEntity <String> exportCsv (){
        String data = studentService.exportStudentToCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "students.csv");
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.ok().headers(headers).body(data);
    }
}
