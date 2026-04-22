package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.PatchStudentDto;
import ru.hogwarts.school.dto.student.StudentDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@Validated //Включает проверку всех аннотаций @Positive, @Min, @Max в этом классе
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    private final FacultyService facultyService;

    public StudentController(StudentService studentService,
                             FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public Student getStudent(@PathVariable @Positive Long id) {
        return studentService.getStudentOrThrow(id);
    }

    @PostMapping
    public ResponseEntity <StudentDto> createStudent(@RequestBody @Valid CreateStudentDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentService.createStudent(dto));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<StudentDto> patchStudent(
            @PathVariable Long id,
            @Valid @RequestBody PatchStudentDto dto)
    {
        return ResponseEntity.ok(studentService.patchStudent(id,dto));
    }

    @PutMapping("{id}")
    public Student updateStudent(@PathVariable @Positive Long id,
                                 @RequestBody Student student) {
        student.setId(id);
        return studentService.editStudent(student);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable @Positive Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Студент с id " + id + " успешно удалена");
    }

    @GetMapping
    public ResponseEntity <List<StudentDto>> getStudentsByAge(@RequestParam int age) {
        return ResponseEntity.ok(studentService.findByAge(age));
    }

    @GetMapping("/age")
    public ResponseEntity <List<StudentDto>> getFindByAgeBetween(@RequestParam int from,
                                                                 @RequestParam int to) {
        return ResponseEntity.ok(studentService.findByAgeBetween(from, to));
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
    public ResponseEntity<String> getStudentByIdDTO(@PathVariable Long id) {
        studentService.getByIdDTO(id);
        return ResponseEntity.ok("Студент с id " + id + " успешно удалена");
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
    public ResponseEntity  <List <StudentDto>> getStudentLimit () {
        return ResponseEntity.ok(studentService.getStudentLimitFiveSortedDesc());
    }
}
