package ru.hogwarts.school.faculty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.faculty.dto.CreateFacultyDto;
import ru.hogwarts.school.faculty.dto.FacultyDto;
import ru.hogwarts.school.faculty.dto.PatchFacultyDto;
import ru.hogwarts.school.user.dto.UserDto;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyDto> getByIdFaculty(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(facultyService.getById(id));
    }

    @PostMapping
    public ResponseEntity<FacultyDto> createFaculty(@RequestBody @Valid CreateFacultyDto dto) {
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(facultyService.createFaculty(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FacultyDto> updateFaculty(
            @PathVariable Long id,
            @Valid @RequestBody PatchFacultyDto dto) {
        return ResponseEntity.ok(facultyService.updateFaculty(id, dto));
    }

    @PutMapping("{id}")
    public Faculty editFaculty(@PathVariable @Positive Long id,
                               @RequestBody Faculty faculty) {
        return facultyService.editFaculty(faculty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable @Positive Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FacultyDto>> getFindByNameOrColor(@RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) String color) {
        return ResponseEntity.ok(facultyService.findByNameOrColor(name, color));
    }

    @GetMapping("{id}/faculty")
    public ResponseEntity<List<UserDto>> getFindStudentByIdFaculty(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(facultyService.studentsFacultyById(id));
    }
}
