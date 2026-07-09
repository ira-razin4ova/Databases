package ru.hogwarts.school.faculty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.faculty.dto.CreateFacultyDto;
import ru.hogwarts.school.faculty.dto.FacultyDto;
import ru.hogwarts.school.faculty.dto.PatchFacultyDto;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.exception.notfound.EntityNotFoundException;
import ru.hogwarts.school.user.UserMapper;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.user.UserRepository;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final FacultyMapper facultyMapper;
    private final UserMapper userMapper;

    public FacultyService(FacultyRepository facultyRepository, UserRepository userRepository, FacultyMapper facultyMapper,
                          UserMapper userMapper) {
        this.facultyRepository = facultyRepository;
        this.userRepository = userRepository;
        this.facultyMapper = facultyMapper;
        this.userMapper = userMapper;
    }

    Logger logger = LoggerFactory.getLogger(FacultyService.class);

    @Transactional
    public FacultyDto createFaculty(CreateFacultyDto dto) {
        logger.info("Was invoked method for create faculty CreateFacultyDto = {}", dto);
        Faculty faculty = facultyMapper.toEntity(dto);
        Faculty savedFaculty = facultyRepository.save(faculty);
        return facultyMapper.toDto(savedFaculty);
    }

    public Faculty getFacultyOrThrow(Long id) {
        logger.debug("Was invoked method for find Faculty");
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("There is not faculty with id = {}", id);
                    return new EntityNotFoundException("Факультет", id);
                });
    }

    public FacultyDto getById (Long id) {
        return facultyMapper.toDto(getFacultyOrThrow(id));
    }

    @Transactional
    public FacultyDto updateFaculty(Long id, PatchFacultyDto dto) {
        logger.info("Was invoked method for update faculty id = {}, PatchFacultyDto = {}", id, dto);
        Faculty faculty = getFacultyOrThrow(id);

        facultyMapper.updateEntityFromPatchDto(dto, faculty);
        logger.info("Faculty with id {} successfully updated and saved", id);
        return facultyMapper.toDto(faculty);
    }


    public Faculty editFaculty(Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())) {
            throw new EntityNotFoundException("Факультет", faculty.getId());
        }
        faculty.setId(faculty.getId());
        return facultyRepository.save(faculty);
    }

    @Transactional
    public void deleteFaculty(Long id) {
        Faculty facultyToDelete = getFacultyOrThrow(id);
        logger.info("Was invoked method delete faculty with id = {}, faculty = {}", id, facultyToDelete);
        facultyRepository.delete(facultyToDelete);
    }


    public List<FacultyDto> findByNameOrColor(String name, String color) {
        logger.debug("Was invoked method find by name = {} or color = {} ", name, color);
        List <Faculty> faculties = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
        return facultyMapper.toDtoList(faculties);
    }

    public List<UserDto> studentsFacultyById(Long facultyId) {
        logger.debug("Was invoked method student by faculty id = {}", facultyId);
        List <User> users = userRepository.findByFaculty_Id(facultyId);
        logger.debug("Was invoked method student list= {}", users);
        return userMapper.toDtoList(users);
    }

}