package ru.hogwarts.school.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.constant.AppConstants;
import ru.hogwarts.school.security.config.SecurityConfig;
import ru.hogwarts.school.security.jwt.JwtAuthenticationFilter;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.user.UserRepository;

import java.util.List;

/**
 * Реализация {@link UserDetailsService} для загрузки пользователей из БД.
 *
 * <p>Используется Spring Security в двух сценариях:
 * <ul>
 *   <li><b>Стандартная аутентификация</b> (по username/password) — вызывается
 *       метод {@link #loadUserByUsername(String)}</li>
 *   <li><b>JWT-аутентификация</b> — вызывается метод
 *       {@link #loadUserById(Long)} из {@link JwtAuthenticationFilter}</li>
 * </ul>
 *
 * <p>Оба метода возвращают {@link AppUserDetails} — кастомную обертку,
 * содержащую ID пользователя для генерации JWT.
 *
 * <p>Роли формируются из поля {@code role} сущности User с префиксом
 * {@code ROLE_} (например, {@code ROLE_STUDENT}, {@code ROLE_CURATOR}).
 * Это требование Spring Security для работы с аннотациями
 * {@code @PreAuthorize("hasRole('CURATOR')")}.
 *
 * @see AppUserDetails
 * @see JwtAuthenticationFilter
 * @see SecurityConfig
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔍 Spring Security затребовал загрузку пользователя с username/email: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("Пользователь с email {} не найден в базе данных", username);
                    return new UsernameNotFoundException(AppConstants.NotFound.BY_EMAIL);
                });

        // Возвращаем нашу обертку.
        return new AppUserDetails(user, setupAuthorities(user));
    }

    public UserDetails loadUserById(Long id) {
     User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(AppConstants.NotFound.BY_ID));

        return new AppUserDetails(user, setupAuthorities(user));
    }


    private List<SimpleGrantedAuthority> setupAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
