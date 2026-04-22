package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.faculty.CreateFacultyDto;
import ru.hogwarts.school.dto.faculty.FacultyDto;
import ru.hogwarts.school.dto.faculty.PatchFacultyDto;
import ru.hogwarts.school.dto.student.StudentDTO;
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
    public ResponseEntity <FacultyDto> createFaculty(@RequestBody @Valid CreateFacultyDto dto) {
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(facultyService.createFaculty(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FacultyDto> patchFaculty(
            @PathVariable Long id,
            @Valid @RequestBody PatchFacultyDto dto)
    {
        return ResponseEntity.ok(facultyService.patchFaculty(id,dto));
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
    public ResponseEntity <List<FacultyDto>>  findByFields(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String color) {
        return ResponseEntity.ok(facultyService.searchNameOrColor(name, color));
    }

    @GetMapping
    public List<FacultyDto> getFindByNameOrColor(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String color) {
        return facultyService.findByNameOrColor(name, color);
    }

    @GetMapping("{id}/faculty")
    public List<StudentDTO> getFindStudentByIdFaculty(@PathVariable @Positive Long id) {
        return facultyService.studentsFacultyById(id);
    }
}
