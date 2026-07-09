package ru.hogwarts.school.auth.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.hogwarts.school.auth.repository.ActivationTokenRepository;
import ru.hogwarts.school.auth.service.AccountActivationService;

/**
 * Сущность токена активации, сохраняемая в Redis.
 *
 * <p>Используется для хранения одноразовых токенов, отправляемых студентам
 * на email при первичной активации аккаунта.
 *
 * <p>Ключевые особенности:
 * <ul>
 *   <li>Аннотация {@link RedisHash} указывает Spring Data Redis использовать
 *       хэш с именем {@code activation_tokens}</li>
 *   <li>{@code timeToLive = 86400} (24 часа) — токен автоматически удаляется
 *       из Redis по истечении срока действия. Это избавляет от необходимости
 *       писать фоновые задачи для очистки</li>
 *   <li>В качестве {@link Id} используется сам токен (UUID-строка),
 *       что обеспечивает O(1) доступ при проверке</li>
 * </ul>
 *
 * <p><b>Резервное хранилище:</b> если Redis недоступен, токен сохраняется
 * в поле {@code activationToken} сущности {@link ru.hogwarts.school.user.User}
 * в PostgreSQL. См. {@link AccountActivationService}.
 *
 * @see AccountActivationService
 * @see ActivationTokenRepository
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "activation_tokens", timeToLive = 86400)
public class ActivationToken {

    @Id
    private String token;

    private String email;
}
