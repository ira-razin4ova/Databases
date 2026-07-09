package ru.hogwarts.school.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.hogwarts.school.security.handler.SecurityExceptionHandlers;
import ru.hogwarts.school.security.jwt.JwtAuthenticationFilter;
import ru.hogwarts.school.security.jwt.JwtUtils;
import ru.hogwarts.school.security.service.UserDetailsServiceImpl;

/**
 * Конфигурация безопасности приложения на основе Spring Security.
 *
 * <p>Настраивает цепочку фильтров для stateless REST API с JWT-авторизацией.
 * Ключевые решения:
 * <ul>
 *   <li>CSRF отключен — для REST API с JWT защита от CSRF не требуется,
 *       так как токен передается в заголовке, а не в cookie</li>
 *   <li>Сессии не создаются ({@link SessionCreationPolicy#STATELESS}) —
 *       состояние клиента хранится в JWT, а не на сервере</li>
 *   <li>Публичные эндпоинты: Swagger, auth, users — доступны без токена</li>
 *   <li>Кастомный {@link JwtAuthenticationFilter} встраивается в цепочку
 *       <b>до</b> стандартного {@link UsernamePasswordAuthenticationFilter},
 *       чтобы перехватывать JWT до стандартной обработки</li>
 * </ul>
 *
 * <p>Связанные компоненты:
 * <ul>
 *   <li>{@link JwtAuthenticationFilter} — проверка JWT в каждом запросе</li>
 *   <li>{@link UserDetailsServiceImpl} — загрузка пользователей из БД</li>
 *   <li>{@link JwtUtils} — генерация и валидация токенов</li>
 * </ul>
 *
 * @see JwtAuthenticationFilter
 * @see UserDetailsServiceImpl
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final SecurityExceptionHandlers.UnauthorizedHandler unauthorizedHandler;
    private final SecurityExceptionHandlers.CustomAccessDeniedHandler accessDeniedHandler;

    // 1. Создаем бин нашего кастомного JWT фильтра
    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }
    // 2. Настраиваем провайдер, который связывает базу данных и шифровальщик паролей
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService); // Учим искать по email
        authProvider.setPasswordEncoder(passwordEncoder());     // Учим сверять хэшированные пароли

        return authProvider;
    }

    // 3. Вытаскиваем AuthenticationManager для контроллера логина
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 4. Главный шифровальщик паролей (чтобы в базе не лежали чистые строки)
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                // Отключаем защиту от CSRF атак (для REST API с JWT она не нужна)
                .csrf(AbstractHttpConfigurer::disable)
                // Настраиваем правила для эндпоинтов (урлов)
                .cors(AbstractHttpConfigurer::disable)

                // Разрешаем вход, регистрацию и активацию ВСЕМ без токена
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/api/v1/users/**").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Все остальные запросы в систему требуют железной авторизации!
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                 .exceptionHandling(exception -> exception
                         .authenticationEntryPoint(unauthorizedHandler) // Ловит 401
                         .accessDeniedHandler(accessDeniedHandler)     // Ловит 403
                 )
                // Подключаем наш провайдер авторизации
                .authenticationProvider(authenticationProvider());

        // 🔥 КРИТИЧЕСКИ ВАЖНО: Встраиваем наш JWT фильтр в шеренгу ДО стандартного UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

           return http.build();
    }
}
