package ru.hogwarts.school.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.auth.controller.AuthController;
import ru.hogwarts.school.auth.dto.JwtResponse;
import ru.hogwarts.school.constant.AuthConstants;
import ru.hogwarts.school.exception.authention.InvalidActivationTokenException;
import ru.hogwarts.school.exception.authention.InvalidUserStatusException;
import ru.hogwarts.school.exception.authention.UserAlreadyActivatedException;
import ru.hogwarts.school.exception.notfound.EntityNotFoundException;
import ru.hogwarts.school.auth.entity.ActivationToken;
import ru.hogwarts.school.auth.repository.ActivationTokenRepository;
import ru.hogwarts.school.security.jwt.JwtUtils;
import ru.hogwarts.school.security.service.AppUserDetails;
import ru.hogwarts.school.user.User;
import ru.hogwarts.school.user.UserRepository;
import ru.hogwarts.school.user.enums.Status;
import ru.hogwarts.school.util.EmailService;

import java.util.Optional;
import java.util.UUID;

/**
 * Сервис аутентификации и онбординга пользователей.
 *
 * <p>Реализует кастомный сценарий первичной активации аккаунта
 * (без классической регистрации):
 * <ol>
 *   <li>Деканат добавляет студента в БД со статусом {@code PENDING}
 *       и пустым паролем</li>
 *   <li>Студент вводит email → сервис генерирует UUID-токен активации</li>
 *   <li>Токен сохраняется в Redis (TTL 24 часа) или в PostgreSQL (fallback)</li>
 *   <li>На email отправляется ссылка с токеном</li>
 *   <li>Студент переходит по ссылке, вводит новый пароль →
 *       аккаунт активируется, выдается JWT</li>
 * </ol>
 *
 * <p>Ключевое архитектурное решение — <b>двухуровневое хранилище токенов</b>:
 * <ul>
 *   <li><b>Redis (основной):</b> быстрый in-memory доступ, автоматическое
 *       удаление по TTL, атомарные операции</li>
 *   <li><b>PostgreSQL (fallback):</b> работает при недоступности Redis,
 *       гарантирует сохранность данных</li>
 * </ul>
 * При записи сначала пытаемся сохранить в Redis, при
 * {@link org.springframework.dao.DataAccessException} переключаемся на БД.
 * При чтении — аналогичная логика: сначала Redis, потом БД.
 *
 * @see ActivationTokenRepository
 * @see EmailService
 * @see JwtUtils
 * @see AuthController
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountActivationService {

    private final UserRepository userRepository;
    private final ActivationTokenRepository redisTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Инициирует процесс активации аккаунта: генерирует токен, сохраняет его
     * и отправляет письмо со ссылкой на email студента.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Ищет пользователя по email. Если не найден —
     *       {@link EntityNotFoundException}</li>
     *   <li>Генерирует случайный UUID-токен</li>
     *   <li>Пытается сохранить в Redis через {@link ActivationTokenRepository}.
     *       При сбое — сохраняет в поле {@code activationToken} сущности User</li>
     *   <li>Формирует ссылку активации и отправляет письмо через
     *       {@link EmailService}</li>
     * </ol>
     *
     * @param email адрес электронной почты студента
     * @throws EntityNotFoundException если пользователь с таким email не найден
     */

    @Transactional
    public void initiateActivation(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(email));

        checkUser(user);

        String token = UUID.randomUUID().toString();

        try {
            saveTokenToRedis(token, email);
        } catch
        (DataAccessException e) {
            log.warn("REDIS not connection! Сохраняю токен в PostgreSQL для: {}", email);
            saveTokenToDb(token, user);
        }

        String activationLink = "http://localhost:8080/api/v1/auth/activate?token=" + token;
        emailService.sendActivationEmail(user.getEmail(), user.getFirstName(), activationLink);
    }

    private void checkUser(User user) {
        if (user.getPassword() != null) {
            throw new UserAlreadyActivatedException(user.getEmail());
        }

        if (user.getStatus().equals(Status.DISMISSED) || user.getStatus().equals(Status.GRADUATED)) {
            throw new InvalidUserStatusException(user.getStatus().name());
        }
    }

    private void saveTokenToRedis(String token, String email) {
        ActivationToken redisToken = new ActivationToken(token, email);
        redisTokenRepository.save(redisToken);
        log.info("Токен успешно сохранен в Redis для: {}", email);
    }

    private void saveTokenToDb(String token, User user) {
        user.setActivationToken(token);
        userRepository.save(user);
    }

    /**
     * Подтверждает активацию аккаунта: проверяет токен, устанавливает пароль,
     * меняет статус на ACTIVE и выдает JWT.
     *
     * <p>После успешной активации токен удаляется из хранилища (Redis или БД),
     * что делает его одноразовым.
     *
     * @param token       токен активации из ссылки в письме
     * @param rawPassword новый пароль, введенный студентом (в открытом виде;
     *                    будет захэширован через {@link PasswordEncoder})
     * @return сгенерированный JWT для немедленного входа в систему
     * @throws InvalidActivationTokenException если токен недействителен,
     *                                         просрочен или уже использован
     */

    @Transactional
    public JwtResponse confirmActivation(String token, String rawPassword) {
        User user = obtainUserByToken(token);

        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        log.info("Аккаунт {} успешно активирован!", user.getEmail());

        AppUserDetails userDetails = AppUserDetails.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String jwtToken = jwtUtils.generateJwtToken(authentication);

        return JwtResponse.from(jwtToken, userDetails);
    }

    /**
     * Ищет пользователя по токену активации, используя двухуровневое хранилище.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Пытается найти токен в Redis. Если найден — удаляет его из Redis
     *       и возвращает пользователя по email из токена</li>
     *   <li>Если Redis недоступен или токена там нет — ищет в БД по полю
     *       {@code activationToken} сущности User. После нахождения обнуляет
     *       поле и сохраняет изменения</li>
     * </ol>
     *
     * @param token токен активации
     * @return сущность пользователя, связанная с токеном
     * @throws InvalidActivationTokenException если токен не найден ни в одном
     *                                         из хранилищ
     */

    private User obtainUserByToken(String token) {
        try {
            Optional<ActivationToken> redisToken = redisTokenRepository.findById(token);
            if (redisToken.isPresent()) {
                User user = userRepository.findByEmail(redisToken.get().getEmail())
                        .orElseThrow(() -> new InvalidActivationTokenException(
                                AuthConstants.Errors.INVALID_OR_EXPIRED_TOKEN));

                try {
                    redisTokenRepository.deleteById(token);
                } catch (DataAccessException ignored) {
                }
                return user;
            }
        } catch (DataAccessException e) {
            log.warn("Redis недоступен, переключаюсь на бэкап в Postgres");
        }

        return userRepository.findByActivationToken(token)
                .map(user -> {
                    user.setActivationToken(null);
                    return user;
                })
                .orElseThrow(() -> new InvalidActivationTokenException(
                        AuthConstants.Errors.INVALID_OR_EXPIRED_TOKEN
                ));
    }
}
