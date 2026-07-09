package ru.hogwarts.school.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.hogwarts.school.auth.controller.AuthController;
import ru.hogwarts.school.security.service.UserDetailsServiceImpl;

/**
 * DTO для запроса аутентификации по email и паролю.
 *
 * <p>Используется в эндпоинте {@code POST /api/v1/auth/login}
 * ({@link AuthController#authenticateUser(LoginRequest)}).
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>email</b> — email пользователя (используется как username).
 *       Проходит валидацию: не пустой, формат email</li>
 *   <li><b>password</b> — пароль в открытом виде.
 *       Проходит валидацию: не пустой, минимум 6 символов</li>
 * </ul>
 *
 * <p>После валидации DTO передается в {@link org.springframework.security.authentication.AuthenticationManager},
 * который вызывает {@link UserDetailsServiceImpl#loadUserByUsername(String)}
 * для загрузки пользователя из БД и проверки пароля.
 *
 * @param email    email пользователя
 * @param password пароль (минимум 6 символов)
 * @see AuthController#authenticateUser(LoginRequest)
 */
public record LoginRequest(
        @NotBlank(message = "Email не должен быть пустым")
        @Email(message = "Некорректный формат email")
        String email,

        @NotBlank(message = "Пароль не должен быть пустым")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password
) {}
