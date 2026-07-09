package ru.hogwarts.school.auth.repository;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;
import ru.hogwarts.school.auth.entity.ActivationToken;
import ru.hogwarts.school.auth.service.AccountActivationService;

/**
 * Репозиторий для работы с токенами активации в Redis.
 *
 * <p>Наследует {@link CrudRepository}, предоставляя стандартные операции:
 * {@code save()}, {@code findById()}, {@code deleteById()}, {@code count()}.
 *
 * <p>Spring Data Redis автоматически генерирует реализацию на основе
 * аннотации {@link RedisHash} у сущности {@link ActivationToken}.
 *
 * <p><b>Важно:</b> операции с Redis могут выбрасывать
 * {@link org.springframework.dao.DataAccessException}, если сервер недоступен.
 * Вызывающий код должен обрабатывать эти исключения и переключаться
 * на резервное хранилище (PostgreSQL).
 *
 * @see ActivationToken
 * @see AccountActivationService
 */

public interface ActivationTokenRepository extends CrudRepository<ActivationToken, String> {
}
