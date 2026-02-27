package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }
    @GetMapping("{id}")
    public Faculty getFaculty(@PathVariable Long id) {
        return facultyService.facultySearchId(id);
    }

    @PostMapping
    public Faculty createFaculty (@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }
    @PutMapping("{id}")
    public Faculty editFaculty(@PathVariable Long id,
                               @RequestBody Faculty faculty) {
        faculty.setId(id);
        return facultyService.editFaculty(faculty);
    }

    @DeleteMapping ("{id}")
    public Faculty deleteFaculty (@PathVariable Long id) {
        return facultyService.deleteFaculty(id);
    }

    @GetMapping
    public List<Faculty> getStudentsByAge (@RequestParam String color) {
        return facultyService.findByColor(color);
    }
}
