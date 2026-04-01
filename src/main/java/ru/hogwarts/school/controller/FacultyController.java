package ru.hogwarts.school.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("{id}")
    public Faculty getFaculty(@PathVariable @Positive Long id) {
        return facultyService.facultySearchId(id);
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping("{id}")
    public Faculty editFaculty(@PathVariable @Positive Long id,
                               @RequestBody Faculty faculty) {
        faculty.setId(id);
        return facultyService.editFaculty(faculty);
    }

    @DeleteMapping("{id}")
    public Faculty deleteFaculty(@PathVariable @Positive Long id) {
        return facultyService.deleteFaculty(id);
    }

    @GetMapping("/search")
    public List<Faculty> findByFields(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String color) {
        return facultyService.search(name, color);
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
