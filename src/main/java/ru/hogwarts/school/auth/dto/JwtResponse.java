package ru.hogwarts.school.auth.dto;

import ru.hogwarts.school.auth.controller.AuthController;
import ru.hogwarts.school.security.service.AppUserDetails;

import java.util.List;

/**
 * DTO для возврата JWT и данных пользователя после успешной аутентификации.
 *
 * <p>Используется в эндпоинтах:
 * <ul>
 *   <li>{@code POST /api/v1/auth/activate} — после активации аккаунта</li>
 *   <li>{@code POST /api/v1/auth/login} — после входа по email/паролю</li>
 * </ul>
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>token</b> — JWT для доступа к защищенным эндпоинтам</li>
 *   <li><b>id</b> — ID пользователя (для идентификации на фронтенде)</li>
 *   <li><b>email</b> — email пользователя (используется как username)</li>
 *   <li><b>roles</b> — список ролей пользователя (например, ["ROLE_STUDENT", "ROLE_CURATOR"])</li>
 * </ul>
 *
 * <p>Создается через фабричный метод {@link #from(String, AppUserDetails)},
 * который извлекает данные из {@link AppUserDetails}.
 *
 * @param token JWT токен
 * @param id    ID пользователя
 * @param email email пользователя
 * @param roles список ролей
 * @see AuthController#authenticateUser(LoginRequest)
 * @see AuthController#confirmActivation(ActivationConfirmDto)
 */
public record JwtResponse(
        String token,
        Long id,
        String email,
        List<String> roles
) {
    /**
     * Фабричный метод для создания {@link JwtResponse} из {@link AppUserDetails}.
     *
     * <p>Извлекает ID, email (username) и роли из {@link AppUserDetails},
     * добавляет JWT токен.
     *
     * @param token       сгенерированный JWT
     * @param userDetails данные пользователя из Spring Security
     * @return новый экземпляр {@link JwtResponse}
     */
    public static JwtResponse from(String token, AppUserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList();

        return new JwtResponse(
                token,
                userDetails.getId(),
                userDetails.getUsername(), // это наш email
                roles
        );
    }
}
