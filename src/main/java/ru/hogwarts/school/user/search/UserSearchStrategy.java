package ru.hogwarts.school.user.search;

import ru.hogwarts.school.user.dto.UserDto;

import java.util.List;

public interface UserSearchStrategy {
    boolean isApplicable(String searchInput);
    List<UserDto> search(String searchInput);
}
