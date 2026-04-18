package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.faculty.FacultyDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
@Validated
@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/{id}")
    public ResponseEntity <FacultyDto> getByIdFaculty(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(facultyService.getById(id));
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody @Valid Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping("{id}")
    public Faculty editFaculty(@PathVariable @Positive Long id,
                               @RequestBody Faculty faculty) {
        faculty.setId(id);
        return facultyService.editFaculty(faculty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFaculty(@PathVariable @Positive Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok("Факультет с ID " + id + " успешно удален");
    }

    @GetMapping("/search")
    public List<Faculty> findByFields(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String color) {
        return facultyService.searchNameOrColor(name, color);
    }

    @GetMapping
    public List<Faculty> getFindByNameOrColor(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String color) {
        return facultyService.findByNameOrColor(name, color);
    }

    @GetMapping("{id}/faculty")
    public List<Student> getFindStudentByIdFaculty(@PathVariable @Positive Long id) {
        return facultyService.studentsFacultyById(id);
    }
}
