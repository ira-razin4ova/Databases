package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.faculty.FacultyDto;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.FacultyMapper;
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
    private final FacultyMapper facultyMapper;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository, FacultyMapper facultyMapper) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.facultyMapper = facultyMapper;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyOrThrow(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Факультет с таким " + id +  " не найден"));
    }

    public FacultyDto getById (Long id) {
        return facultyMapper.toDto(getFacultyOrThrow(id));
    }

    public Faculty editFaculty(Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())) {
            throw new NotFoundException("ID не найден, изменения не возможно!");
        }
        faculty.setId(faculty.getId());
        return facultyRepository.save(faculty);
    }

    @Transactional
    public void deleteFaculty(Long id) {
        if (!facultyRepository.existsById(id)) {
            throw new NotFoundException("Невозможно удалить: факультет с ID " + id + " не найден!");
        }
        facultyRepository.deleteById(id);
    }

    public List<Faculty> searchNameOrColor(String name, String color) {
        String searchName = (name == null) ? "" : name;
        String searchColor = (color == null) ? "" : color;

        return facultyRepository.findByRussianNameOrColor(searchName, searchColor);
    }

    public List<Faculty> findByNameOrColor(String name, String color) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public List<Student> studentsFacultyById(Long facultyId) {
        return studentRepository.findByFaculty_Id(facultyId);
    }

}