package ru.hogwarts.school.user.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.user.UserMapper;
import ru.hogwarts.school.user.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserTicketSearchStrategy implements UserSearchStrategy {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public boolean isApplicable(String searchInput) {
        return searchInput.matches("\\d{3}-\\d{3}");
    }

    @Override
    public List<UserDto> search(String searchInput) {
        var student = userRepository.findByStudentTicket(searchInput);
        return student.stream()
                .map(userMapper::toDto)
                .toList();
    }
}
