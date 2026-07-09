package ru.hogwarts.school.user.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.user.UserMapper;
import ru.hogwarts.school.user.UserRepository;

import java.util.List;
@Component
@RequiredArgsConstructor
public class UserLastNameSearchStrategy implements UserSearchStrategy {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public boolean isApplicable(String searchInput) {
        return searchInput.matches("[a-zA-Zа-яА-ЯёЁ\\s-]+");
    }

    @Override
    public List<UserDto> search(String searchInput) {
        var student = userRepository.findByLastNameIgnoreCase(searchInput);
        return student.stream()
                .map(userMapper::toDto)
                .toList();
    }
}
