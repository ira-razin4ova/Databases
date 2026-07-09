package ru.hogwarts.school.security.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.hogwarts.school.constant.AuthConstants;
import ru.hogwarts.school.security.config.SecurityConfig;
import ru.hogwarts.school.security.service.UserDetailsServiceImpl;

import java.io.IOException;

/**
 * Фильтр JWT-аутентификации, перехватывающий каждый HTTP-запрос.
 *
 * <p>Наследуется от {@link OncePerRequestFilter}, что гарантирует выполнение
 * фильтра ровно один раз за запрос (даже при внутренних форвардах).
 *
 * <p>Алгоритм работы:
 * <ol>
 *   <li>Извлекает токен из заголовка {@code Authorization: Bearer <token>}</li>
 *   <li>Валидирует токен через {@link JwtUtils#validateJwtToken(String)}</li>
 *   <li>Если токен валиден — извлекает ID пользователя</li>
 *   <li>Загружает пользователя из БД через
 *       {@link UserDetailsServiceImpl#loadUserById(Long)}</li>
 *   <li>Устанавливает {@link Authentication} в {@link SecurityContext},
 *       делая пользователя доступным для всех последующих проверок
 *       (например, {@code @PreAuthorize})</li>
 * </ol>
 *
 * <p>Если токен отсутствует или невалиден — фильтр просто пропускает запрос
 * дальше. Проверку наличия прав доступа выполняет Spring Security на основе
 * правил из {@link SecurityConfig}.
 *
 * <p><b>Порядок в цепочке фильтров:</b> встраивается <b>до</b>
 * {@link UsernamePasswordAuthenticationFilter}, чтобы JWT обрабатывался
 * до стандартных механизмов аутентификации.
 *
 * @see JwtUtils
 * @see UserDetailsServiceImpl
 * @see SecurityConfig
 */

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                String userIdStr = jwtUtils.getUserIdFromJwtToken(jwt);
                Long userId = Long.parseLong(userIdStr);

                UserDetails userDetails = userDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (SignatureException e) {
            log.error(AuthConstants.Logs.JWT_SIGNATURE_INVALID, e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error(AuthConstants.Logs.JWT_EXPIRED, e.getMessage());
        } catch (NumberFormatException e) {
            log.error(AuthConstants.Logs.INVALID_USER_ID, e.getMessage());
        } catch (Exception e) {
            log.error(AuthConstants.Logs.UNEXPECTED_AUTH_ERROR, e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
