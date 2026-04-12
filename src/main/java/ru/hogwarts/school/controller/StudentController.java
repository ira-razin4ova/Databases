package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.product.ProductDTO;
import ru.hogwarts.school.dto.student.StudentDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.ProductService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@Validated //Включает проверку всех аннотаций @Positive, @Min, @Max в этом классе
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    private final ProductService productService;

    public StudentController(StudentService studentService,
                             ProductService productService) {
        this.studentService = studentService;
        this.productService = productService;
    }

    @GetMapping("{id}")
    public Student getStudent(@PathVariable @Positive Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping
    public Student createStudent(@RequestBody @Valid Student student) {
        return studentService.creteStudent(student);
    }

    @PutMapping("{id}")
    public Student updateStudent(@PathVariable @Positive Long id,
                                 @RequestBody Student student) {
        student.setId(id);
        return studentService.editStudent(student);
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

    @GetMapping("/student/{id}/faculty")
    public ResponseEntity<Faculty> getFacultyByStudentId(@PathVariable @Positive Long id) {
        Faculty faculty = studentService.getFacultyByStudentId(id);
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<String> exportCsv() {
        String data = studentService.exportStudentToCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "students.csv");
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.ok().headers(headers).body(data);
    }
    @GetMapping("/dto/{id}")
    public ResponseEntity<StudentDTO> getStudentByIdDTO(@PathVariable Long id) {
        StudentDTO studentDTO = studentService.getByIdDTO(id);

        return ResponseEntity.ok(studentDTO);
    }
}
