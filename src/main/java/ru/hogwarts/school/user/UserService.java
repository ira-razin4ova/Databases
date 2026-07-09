package ru.hogwarts.school.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.constant.AppConstants;
import ru.hogwarts.school.user.dto.CreateUserDto;
import ru.hogwarts.school.user.dto.PatchUserDto;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.user.dto.UserRequest;
import ru.hogwarts.school.exception.notfound.EntityNotFoundException;
import ru.hogwarts.school.exception.badrequest.ValidationException;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.util.DataCodecService;
import ru.hogwarts.school.faculty.FacultyRepository;
import ru.hogwarts.school.user.search.UserSearchStrategy;

import java.time.LocalDate;
import java.util.*;


@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final DataCodecService dataCodecService;
    private final UserMapper userMapper;
    private final List<UserSearchStrategy> searchStrategies;


    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Transactional
    public UserDto createUser(CreateUserDto dto) {
        logger.info("Was invoked method for create User CreateUserDto = {}", dto);
        User user = userMapper.toEntity(dto);
        user.setPhoneNumber(dataCodecService.encodePhone(dto.getPhoneNumber()));
        user.setFaculty(getFacultyOrThrow(dto.getIdFaculty()));
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    public Faculty getFacultyOrThrow(Long id) {
        logger.debug("Was invoked method for find Faculty");
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("There is not faculty with id = {}", id);
                    return new EntityNotFoundException("Факультет", id);
                });
    }

    public User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("There is not user with id = {}", id);
                    return new EntityNotFoundException("Студент", id);
                });
    }

    public UserDto getByIdDTO(Long id) {
        User user = getUserOrThrow(id);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, PatchUserDto dto) {
        logger.info("Was invoked method for update User id = {}, PatchUserDto = {}", id, dto);

        User user = getUserOrThrow(id);

        userMapper.updateEntityFromPatchDto(dto, user);

        if (dto.phoneNumber() != null) {
            codecPhone(user, dto.phoneNumber());
        }
        if (dto.facultyId() != null) {
            updateFacultyRelationship(user, dto.facultyId());
        }

        logger.info("User with id {} successfully updated and saved", id);
        return userMapper.toDto(user);
    }

    private void updateFacultyRelationship(User userIdFaculty, Long facultyId) {
        if (facultyId != null) {
            Faculty newFaculty = getFacultyOrThrow(facultyId);
            userIdFaculty.setFaculty(newFaculty);
        }
    }

    private void codecPhone(User user, String phoneNumber) {
        user.setPhoneNumber(dataCodecService.encodePhone(phoneNumber));
    }

    public User editUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new EntityNotFoundException("Студент", user.getId());
        }
        user.setId(user.getId());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User userToDelete = getUserOrThrow(id);
        logger.info("Was invoked method delete user with id = {}, user = {}", id, userToDelete);
        userRepository.delete(userToDelete);
    }

    public List<UserDto> findByAge(LocalDate birthDate) {
        List<User> users = userRepository.findByBirthDate(birthDate);
        return userMapper.toDtoList(users);
    }

    public List<UserDto> findByAgeBetween(LocalDate from, LocalDate to) {
        List<User> users = userRepository.findByBirthDateBetween(from, to);
        return userMapper.toDtoList(users);
    }

    public String exportUserToCsv() {
        List<User> users = userRepository.findAll();
        StringBuilder csv = new StringBuilder("№,Id,firstName,lastName,Age,FacultyId,FName,FColor\n");
        for (User user : users) {
            int count = 1;
            csv.append(count++).append(",")
                    .append(user.getId()).append(",")
                    .append(user.getFirstName()).append(",")
                    .append(user.getLastName()).append(",")
                    .append(user.getBirthDate()).append(",");

            if (user.getFaculty() != null) {
                csv.append(user.getFaculty().getId()).append(",")
                        .append(user.getFaculty().getName()).append(",")
                        .append(user.getFaculty().getColor());
            } else {
                csv.append("-,-,-");
            }
            csv.append("\n");

        }
        return csv.toString();
    }


    public Long getUserCount() {
        return userRepository.getUserCount();
    }

    public List<UserDto> getUserLimitFiveSortedDesc() {
        List<User> users = userRepository.getUserLimitFive();
        return userMapper.toDtoList(users);
    }

    public List<String> usersSteamSorted(String sortedLetter) {
        return userRepository.findAll().stream()
                .map(User::getFirstName)
                .map(String::toUpperCase)
                .filter(name -> name.startsWith(sortedLetter.toUpperCase()))
                .sorted()
                .toList();
    }

    public List<UserDto> searchUsers(UserRequest request) {
        String input = request.searchInput();

        return searchStrategies.stream()
                .filter(strategy -> strategy.isApplicable(input))
                .findFirst()
                .map(strategy -> strategy.search(input))
                .orElseThrow(() -> new ValidationException(AppConstants.Validation.VALIDATION_ERROR + input));
    }
}
