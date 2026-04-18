package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.product.ProductDTO;
import ru.hogwarts.school.dto.student.CreateStudentDto;
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

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public Student getStudent(@PathVariable @Positive Long id) {
        return studentService.getStudentOrThrow(id);
    }

    @PostMapping
    public ResponseEntity <StudentDTO> createStudent(@RequestBody @Valid CreateStudentDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentService.createStudent(dto));
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
        return ResponseEntity.ok(studentService.getFacultyByStudentId(id));
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
        return ResponseEntity.ok(studentService.getByIdDTO(id));
    }

    @GetMapping ("/count")
    public ResponseEntity <Long> getStudentCount () {
        return ResponseEntity.ok(studentService.getStudentCount());
    }

    @GetMapping ("/age-avg")
    public ResponseEntity <Double> getStudentAgeAvg () {
        return ResponseEntity.ok(studentService.getStudentAgeAvg());
    }

    @GetMapping ("/limit")
    public ResponseEntity  <List <StudentDTO>> getStudentLimit () {
        return ResponseEntity.ok(studentService.getStudentLimitFiveSortedDesc());
    }
}
