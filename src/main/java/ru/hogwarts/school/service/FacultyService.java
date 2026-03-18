package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFound;
import ru.hogwarts.school.exception.StudentNotFound;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Transactional
@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty facultySearchId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Введите данные для поиска");
        }

        return facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFound("Факультет с таким id не найден"));
    }

    public Faculty editFaculty(Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())) {
            throw new FacultyNotFound("ID не найден, изменения не возможно!");
        }
        faculty.setId(faculty.getId());
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(Long id) {
        Faculty facultyToDelete = facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFound("Невозможно удалить: факультет с ID не найден! Или был удален ранее"));

        facultyRepository.delete(facultyToDelete);
        return facultyToDelete;
    }

    public List<Faculty> search(String name, String color) {
        String searchName = (name == null) ? "" : name;
        String searchColor = (color == null) ? "" : color;

        return facultyRepository.findByRussianNameOrColor(searchName, searchColor);
    }

    public List<Faculty> findByNameOrColor(String name, String color) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public List<Student> studentsFacultyById(Long facultyId) {
        if (facultyId == null) {
            throw new FacultyNotFound("Введите данные факультета для поиска");
        }
        return studentRepository.findByFaculty_Id(facultyId);
    }
}