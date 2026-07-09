package ru.hogwarts.school.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ru.hogwarts.school.auth.controller.AuthController;
import ru.hogwarts.school.auth.service.AccountActivationService;

/**
 * DTO для запроса инициации активации аккаунта.
 *
 * <p>Используется в эндпоинте {@code POST /api/v1/auth/register}
 * ({@link AuthController#register(RegisterRequest)}).
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>email</b> — email студента, который уже добавлен в систему деканатом.
 *       Проходит валидацию: не пустой, формат email</li>
 * </ul>
 *
 * <p><b>Важно:</b> этот DTO не создает нового пользователя. Пользователь должен
 * уже существовать в БД (добавлен деканатом при зачислении). Если email не найден —
 * {@link org.springframework.dao.EmptyResultDataAccessException}.
 *
 * <p>После валидации email передается в
 * {@link AccountActivationService#initiateActivation(String)},
 * который генерирует токен активации и отправляет ссылку на email.
 *
 * @param email email студента
 * @see AuthController#register(RegisterRequest)
 */
public record RegisterRequest(
        @NotBlank(message = "Email не должен быть пустым")
        @Email(message = "Некорректный формат email")
        String email
) {}
