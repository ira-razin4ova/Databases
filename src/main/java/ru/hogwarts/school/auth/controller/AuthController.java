package ru.hogwarts.school.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.auth.dto.*;
import ru.hogwarts.school.auth.service.AccountActivationService;
import ru.hogwarts.school.constant.AuthConstants;
import ru.hogwarts.school.exception.authention.InvalidActivationTokenException;
import ru.hogwarts.school.exception.notfound.EntityNotFoundException;
import ru.hogwarts.school.security.config.SecurityConfig;
import ru.hogwarts.school.security.jwt.JwtUtils;
import ru.hogwarts.school.security.service.AppUserDetails;
import ru.hogwarts.school.security.service.UserDetailsServiceImpl;


/**
 * REST-контроллер для аутентификации и управления аккаунтами пользователей.
 *
 * <p>Предоставляет эндпоинты для:
 * <ul>
 *   <li>Инициирования активации аккаунта (отправка ссылки на email)</li>
 *   <li>Подтверждения активации (установка пароля после перехода по ссылке)</li>
 *   <li>Классической аутентификации (логин по email + пароль)</li>
 * </ul>
 *
 * <p>Все эндпоинты являются публичными (не требуют JWT), так как настроены
 * в {@link SecurityConfig} через
 * {@code requestMatchers("/api/v1/auth/**").permitAll()}.
 *
 * <p>Архитектура:
 * <ul>
 *   <li>Контроллер принимает DTO, валидирует их через {@code @Valid}</li>
 *   <li>Делегирует бизнес-логику в {@link AccountActivationService}</li>
 *   <li>Для логина использует стандартный {@link AuthenticationManager} от Spring Security</li>
 *   <li>Генерацию JWT выполняет {@link JwtUtils}</li>
 * </ul>
 *
 * @see AccountActivationService
 * @see JwtUtils
 * @see SecurityConfig
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AccountActivationService activationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Инициирует процесс активации аккаунта: генерирует токен и отправляет
     * ссылку на email студента.
     *
     * <p>Endpoint: {@code POST /api/v1/auth/register}
     *
     * <p>Flow:
     * <ol>
     *   <li>Принимает email из {@link RegisterRequest}</li>
     *   <li>Валидирует email через {@code @Valid}</li>
     *   <li>Делегирует в {@link AccountActivationService#initiateActivation(String)}</li>
     *   <li>Возвращает сообщение об успешной отправке ссылки</li>
     * </ol>
     *
     * <p><b>Важно:</b> этот эндпоинт не создает нового пользователя.
     * Пользователь должен уже существовать в БД (добавлен деканатом).
     * Если email не найден — {@link EntityNotFoundException}.
     *
     * @param request DTO с email студента
     * @return {@link AuthMessageResponse} с сообщением об отправке ссылки
     * @throws EntityNotFoundException если пользователь с таким email не найден
     * @throws MethodArgumentNotValidException если email не прошел валидацию
     */
    @PostMapping("/register")
    public ResponseEntity<AuthMessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        activationService.initiateActivation(request.email());
        return response();
    }

    /**
     * Подтверждает активацию аккаунта: проверяет токен, устанавливает пароль,
     * меняет статус на ACTIVE и возвращает JWT для немедленного входа.
     *
     * <p>Endpoint: {@code POST /api/v1/auth/activate}
     *
     * <p>Flow:
     * <ol>
     *   <li>Принимает токен и новый пароль из {@link ActivationConfirmDto}</li>
     *   <li>Делегирует в {@link AccountActivationService#confirmActivation(String, String)}</li>
     *   <li>Сервис проверяет токен (Redis → fallback в БД)</li>
     *   <li>Хэширует пароль через {@link org.springframework.security.crypto.password.PasswordEncoder}</li>
     *   <li>Меняет статус пользователя на ACTIVE</li>
     *   <li>Генерирует JWT через {@link JwtUtils}</li>
     *   <li>Возвращает JWT в {@link JwtResponse}</li>
     * </ol>
     *
     * @param dto DTO с токеном активации и новым паролем
     * @return {@link JwtResponse} с JWT токеном для входа в систему
     * @throws InvalidActivationTokenException если токен недействителен, просрочен или уже использован
     * @throws MethodArgumentNotValidException если DTO не прошел валидацию
     */
    @PostMapping("/activate")
    public ResponseEntity<JwtResponse> confirmActivation(@Valid @RequestBody ActivationConfirmDto dto) {
        JwtResponse response = activationService.confirmActivation(dto.token(), dto.password());
        return ResponseEntity.ok(response);
    }

    /**
     * Аутентифицирует пользователя по email и паролю, возвращает JWT.
     *
     * <p>Endpoint: {@code POST /api/v1/auth/login}
     *
     * <p>Flow:
     * <ol>
     *   <li>Принимает email и пароль из {@link LoginRequest}</li>
     *   <li>Создает {@link UsernamePasswordAuthenticationToken}</li>
     *   <li>Делегирует аутентификацию в {@link AuthenticationManager}</li>
     *   <li>Spring Security вызывает {@link UserDetailsServiceImpl#loadUserByUsername(String)}</li>
     *   <li>Проверяет пароль через {@link org.springframework.security.crypto.password.PasswordEncoder}</li>
     *   <li>Если успешно — генерирует JWT через {@link JwtUtils#generateJwtToken(Authentication)}</li>
     *   <li>Возвращает JWT и информацию о пользователе в {@link JwtResponse}</li>
     * </ol>
     *
     * @param loginRequest DTO с email и паролем
     * @return {@link JwtResponse} с JWT токеном и данными пользователя
     * @throws BadCredentialsException если email или пароль неверны
     * @throws DisabledException если аккаунт не активирован (статус != ACTIVE)
     * @throws MethodArgumentNotValidException если DTO не прошел валидацию
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        String jwt = jwtUtils.generateJwtToken(authentication);
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(JwtResponse.from(jwt, userDetails));
    }

    /**
     * Формирует стандартный ответ об успешной отправке ссылки активации.
     *
     * @return {@link AuthMessageResponse} с сообщением из {@link AuthConstants.Success#ACTIVATION_LINK_SENT}
     */
    private ResponseEntity<AuthMessageResponse> response() {
        return ResponseEntity.ok(new AuthMessageResponse(AuthConstants.Success.ACTIVATION_LINK_SENT));
    }
}
