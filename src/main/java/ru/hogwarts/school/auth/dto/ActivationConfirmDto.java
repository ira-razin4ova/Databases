package ru.hogwarts.school.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UUID;
import ru.hogwarts.school.auth.controller.AuthController;
import ru.hogwarts.school.auth.service.AccountActivationService;

/**
 * DTO для подтверждения активации аккаунта.
 *
 * <p>Используется в эндпоинте {@code POST /api/v1/auth/activate}
 * ({@link AuthController#confirmActivation(ActivationConfirmDto)}).
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>token</b> — одноразовый токен активации из ссылки в письме.
 *       Проходит валидацию: не пустой, формат UUID</li>
 *   <li><b>password</b> — новый пароль, который студент хочет установить.
 *       Проходит валидацию: не пустой, минимум 6 символов</li>
 * </ul>
 *
 * <p>После успешной валидации DTO передается в
 * {@link AccountActivationService#confirmActivation(String, String)},
 * который проверяет токен, хэширует пароль и выдает JWT.
 *
 * @param token    токен активации (UUID)
 * @param password новый пароль (минимум 6 символов)
 * @see AuthController#confirmActivation(ActivationConfirmDto)
 */

public record ActivationConfirmDto(
        @NotBlank(message = "Токен активации не должен быть пустым")
        @UUID(message = "Некорректный формат токена активации")
        String token,

        @NotBlank(message = "Пароль не должен быть пустым")
        @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
        String password) {

}
