package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFound;
import ru.hogwarts.school.exception.StudentNotFound;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    
    private final FacultyRepository facultyRepository;
    
    public FacultyService (FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }
    
    public Faculty createFaculty (Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty facultySearchId (Long id) {
if (id == null) {
    throw new IllegalArgumentException("Введите данные для поиска");
}

        return facultyRepository.findById(id)
                .orElseThrow(()-> new FacultyNotFound ("Факультет с таким id не найден"));
    }

    public Faculty editFaculty (Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())) {
            throw new StudentNotFound("ID не найден, изменения не возможно!");
        }
        faculty.setId(faculty.getId());
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty (Long id) {
        Faculty facultyToDelete = facultyRepository.findById(id)
                .orElseThrow(() -> new StudentNotFound("Невозможно удалить: факультет с ID не найден! Или был удален ранее"));

        facultyRepository.delete(facultyToDelete);
        return facultyToDelete;
    }

    public List<Faculty> findByColor (String color) {
        return facultyRepository.findByColor(color);
    }

}
