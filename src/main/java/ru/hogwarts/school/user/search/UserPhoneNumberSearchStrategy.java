package ru.hogwarts.school.user.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.user.dto.UserDto;
import ru.hogwarts.school.user.UserMapper;
import ru.hogwarts.school.user.UserRepository;
import ru.hogwarts.school.util.DataCodecService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserPhoneNumberSearchStrategy implements UserSearchStrategy {

    private final DataCodecService dataCodecService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public boolean isApplicable(String searchInput) {
return searchInput.matches("\\d+");
    }

    @Override
    public List<UserDto> search(String searchInput) {
        String encodePhone = dataCodecService.encodePhone(searchInput);
        var student = userRepository.findByPhoneNumber(encodePhone);
        return student.stream()
                .map(userMapper::toDto)
                .toList();
    }
}
