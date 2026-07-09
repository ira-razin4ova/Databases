package ru.hogwarts.school.security.service;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.hogwarts.school.auth.dto.JwtResponse;
import ru.hogwarts.school.auth.service.AccountActivationService;
import ru.hogwarts.school.security.jwt.JwtUtils;

import java.util.Collection;
import java.util.List;

/**
 * Обертка над сущностью {@link ru.hogwarts.school.user.User}, адаптирующая её
 * для Spring Security через интерфейс {@link UserDetails}.
 *
 * <p>Наследуется от стандартного
 * {@link org.springframework.security.core.userdetails.User}, чтобы не
 * реализовывать вручную методы {@code isEnabled()}, {@code isAccountNonExpired()}
 * и т.д.
 *
 * <p>Ключевые особенности:
 * <ul>
 *   <li><b>Логин (username):</b> используется {@code email}, а не отдельное
 *       поле username. Это соответствует бизнес-логике — студенты входят
 *       по почте</li>
 *   <li><b>Дополнительное поле {@code id}:</b> необходимо для генерации JWT
 *       через {@link JwtUtils}, чтобы зашивать ID пользователя в токен</li>
 *   <li><b>Статус ACTIVE:</b> флаг {@code enabled} в Spring Security
 *       устанавливается на основе статуса пользователя. Неактивные пользователи
 *       не смогут пройти аутентификацию</li>
 * </ul>
 *
 * <p>Создается через фабричный метод {@link #build(ru.hogwarts.school.user.User)},
 * который автоматически формирует список ролей ({@link GrantedAuthority})
 * на основе поля {@code role} сущности User.
 *
 * <p><b>Используется в:</b>
 * <ul>
 *   <li>{@link UserDetailsServiceImpl} — при загрузке пользователя из БД</li>
 *   <li>{@link JwtUtils} — при генерации JWT (извлекает ID и роли)</li>
 *   <li>AuthController — после успешного логина и активации</li>
 *   <li>{@link AccountActivationService} — после активации аккаунта</li>
 *   <li>{@link JwtResponse} — для формирования ответа клиенту</li>
 * </ul>
 *
 * @see UserDetailsServiceImpl
 * @see JwtUtils
 * @see AccountActivationService
 * @see JwtResponse
 */
@Getter
public class AppUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long id;

    /**
     * Конструктор, адаптирующий сущность User для Spring Security.
     *
     * <p>Передаёт в родительский класс:
     * <ul>
     *   <li>{@code email} как username (логин)</li>
     *   <li>{@code password} (хэш пароля)</li>
     *   <li>{@code enabled} = true, если статус пользователя "ACTIVE"</li>
     *   <li>{@code accountNonExpired}, {@code credentialsNonExpired},
     *       {@code accountNonLocked} = true (всегда активны)</li>
     *   <li>{@code authorities} — список ролей</li>
     * </ul>
     *
     * <p>Дополнительно сохраняет {@code id} пользователя для использования
     * в {@link JwtUtils#generateJwtToken}.
     *
     * @param user        сущность пользователя из БД
     * @param authorities список ролей (например, ["ROLE_STUDENT"])
     */
    public AppUserDetails(ru.hogwarts.school.user.User user, Collection<? extends GrantedAuthority> authorities) {
        // Отдаем Спрингу email в качестве логина, хэш пароля и проверяем, что юзер ACTIVE
        super(user.getEmail(),
                user.getPassword(),
                "ACTIVE".equals(user.getStatus()),
                true, true, true,
                authorities);

        // Клеем кармашек и кладем туда ID для генератора токенов
        this.id = user.getId();
    }

    /**
     * Фабричный метод для создания {@link AppUserDetails} из сущности User.
     *
     * <p>Автоматически формирует список ролей на основе поля {@code role}
     * сущности User. Роль преобразуется в {@link SimpleGrantedAuthority}
     * с префиксом {@code ROLE_} (требование Spring Security).
     *
     * <p>Пример: если {@code user.getRole() == Role.CURATOR}, то создаётся
     * authority {@code "ROLE_CURATOR"}.
     *
     * @param user сущность пользователя из БД
     * @return новый экземпляр {@link AppUserDetails}
     */
    public static AppUserDetails build(ru.hogwarts.school.user.User user) {
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        return new AppUserDetails(user, authorities);
    }
}
