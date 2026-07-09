package ru.hogwarts.school.security.jwt;



import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.constant.AuthConstants;
import ru.hogwarts.school.security.config.SecurityConfig;
import ru.hogwarts.school.security.service.AppUserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Утилита для работы с JWT (JSON Web Token).
 *
 * <p>Отвечает за три ключевые операции:
 * <ul>
 *   <li><b>Генерация токена</b> — создает JWT с ID пользователя и ролями,
 *       подписывает секретным ключом (алгоритм HS256)</li>
 *   <li><b>Валидация токена</b> — проверяет подпись, срок действия, формат.
 *       Логирует конкретную причину невалидности для отладки</li>
 *   <li><b>Извлечение данных</b> — достаёт ID пользователя из payload токена</li>
 * </ul>
 *
 * <p>Конфигурация берется из application.properties:
 * <ul>
 *   <li>{@code app.jwt.secret} — Base64-кодированный секретный ключ
 *       (должен быть не менее 256 бит для HS256)</li>
 *   <li>{@code app.jwt.expiration-ms} — время жизни токена в миллисекундах</li>
 * </ul>
 *
 * <p><b>Важно:</b> секретный ключ должен храниться в безопасности и не попадать
 * в репозиторий. Для продакшена используйте переменные окружения.
 *
 * @see JwtAuthenticationFilter
 * @see SecurityConfig
 */

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Превращаем нашу строку-секрет в безопасный криптографический ключ SecretKey.
     * Криптографические алгоритмы не умеют работать с обычным текстом (String).
     * Им нужны биты и байты особого формата.
     * Этот метод — переводчик, который превращает строчку из настроек в полноценный криптографический инструмент.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 1. ГЕНЕРАЦИЯ ТОКЕНА
     * Вызывается, когда юзер успешно ввел логин/пароль, а так же активировал свой аккаунт.
     */
    public String generateJwtToken(Authentication authentication) {
        // Достаем спрингового пользователя, которого нам вернул твой UserDetailsServiceImpl
        AppUserDetails userPrincipal = (AppUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(String.valueOf(userPrincipal.getId()))
                .claim("roles", roles)                // Payload: зашиваем роли юзера
                .issuedAt(new Date())                 // Время создания
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Время смерти токена
                .signWith(getSigningKey())            // Подписываем нашим секретным ключом
                .compact();
    }

    /**
     * 2. ПОЛУЧЕНИЕ ID ИЗ ТОКЕНА
     * Вызывается при каждом запросе, чтобы понять, "кто к нам пришел".
     */
    public String getUserIdFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Проверяем подпись
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject(); // Забираем email
    }

    /**
     * 3. ВАЛИДАЦИЯ ТОКЕНА
     * Проверяем, что токен не протух, не изменен хакерами и вообще валиден.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error(AuthConstants.Logs.JWT_MALFORMED, e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error(AuthConstants.Logs.JWT_EXPIRED, e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error(AuthConstants.Logs.JWT_UNSUPPORTED, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error(AuthConstants.Logs.JWT_EMPTY, e.getMessage());
        }
        return false;
    }
}
