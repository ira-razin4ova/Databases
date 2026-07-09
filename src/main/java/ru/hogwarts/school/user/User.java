package ru.hogwarts.school.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.hogwarts.school.auth.service.AccountActivationService;
import ru.hogwarts.school.avatar.Avatar;
import ru.hogwarts.school.security.service.AppUserDetails;
import ru.hogwarts.school.security.service.UserDetailsServiceImpl;
import ru.hogwarts.school.user.enums.Gender;
import ru.hogwarts.school.user.enums.Role;
import ru.hogwarts.school.user.enums.Status;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.util.EmailService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Центральная сущность системы — пользователь (студент, староста, куратор или декан).
 *
 * <p>Ключевые особенности бизнес-логики:
 * <ul>
 *   <li><b>Отсутствие самостоятельной регистрации:</b> пользователи не могут
 *       зарегистрироваться сами. Они добавляются в систему деканатом при зачислении
 *       со статусом {@link Status#ACTIVE}. Пользователь существует в системе
 *       с момента зачисления, даже если еще не заходил в приложение</li>
 *   <li><b>Первый вход через email:</b> при первом входе пользователь устанавливает
 *       пароль через одноразовый токен (см. {@code activationToken}). Это не
 *       "активация аккаунта", а именно установка первого пароля</li>
 *   <li><b>Жизненный цикл студента:</b> статус ({@link Status}) отражает текущее
 *       положение студента в университете: зачислен, в академе, отчислен или выпускник.
 *       Это влияет на доступ к системе и видимость данных</li>
 *   <li><b>Ролевая модель (RBAC):</b> роль ({@link Role}) определяет права доступа
 *       и видимость данных. Роли зашиваются в JWT и проверяются через
 *       Spring Security ({@code @PreAuthorize})</li>
 *   <li><b>Изоляция данных по факультету:</b> поле {@link #faculty} определяет,
 *       к какому факультету принадлежит пользователь. Квесты и мерч с
 *       {@code faculty_id = null} доступны всем, иначе — только студентам
 *       соответствующего факультета</li>
 *   <li><b>Внутренняя валюта:</b> поле {@link #balance} хранит баланс студента
 *       в формате {@link BigDecimal} для точных финансовых вычислений.
 *       Используется для покупки мерча в магазине</li>
 * </ul>
 *
 * <p>Связи с другими сущностями:
 * <ul>
 *   <li>{@link Faculty} — факультет, к которому принадлежит пользователь
 *       (связь Many-to-One). Определяет видимость квестов и мерча</li>
 *   <li>{@link Avatar} — аватар пользователя (связь One-to-One с каскадным
 *       удалением). При удалении пользователя аватар удаляется автоматически</li>
 * </ul>
 *
 * <p>Использование в системе авторизации:
 * <ul>
 *   <li>{@link AppUserDetails} — обертка над User для Spring Security,
 *       адаптирует email как username, добавляет ID для JWT</li>
 *   <li>{@link UserDetailsServiceImpl} — загружает
 *       User из БД по email или ID</li>
 *   <li>{@link AccountActivationService} —
 *       устанавливает пароль и меняет статус на ACTIVE при активации</li>
 * </ul>
 *
 * @see Faculty
 * @see Avatar
 * @see Role
 * @see Status
 * @see AppUserDetails
 */

@Setter
@Getter
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
@Table(name = "users")
public class User {

    /**
     * Уникальный идентификатор пользователя.
     * Генерируется базой данных автоматически (IDENTITY).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный идентификатор пользователя.
     * Генерируется базой данных автоматически (IDENTITY).
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Фамилия пользователя.
     * Обязательное поле, используется для отображения в интерфейсе.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Пол пользователя.
     * Хранится как строка (EnumType.STRING) для читаемости в БД.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    /**
         * Дата рождения пользователя.
     * Используется для верификации возраста и персональных предложений.
     */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /**
     * Факультет, к которому принадлежит пользователь.
     *
     * <p>Определяет видимость квестов и мерча:
     * <ul>
     *   <li><b>Факультетские квесты и мерч</b> (созданные куратором факультета X) —
     *       доступны только студентам факультета X. Студенты других факультетов
     *       их не видят</li>
     *   <li><b>Общие квесты и мерч</b> (созданные деканом) — доступны всем студентам
     *       университета без ограничений по факультету</li>
     * </ul>
     *
     * <p>Аннотация {@code @JsonIgnoreProperties("students")} предотвращает
     * циклическую сериализацию при конвертации в JSON (Faculty содержит
     * список students, который содержит Faculty, и т.д.).
     *
     * @see Faculty
     */
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    @JsonIgnoreProperties("students")
    private Faculty faculty;

    /**
     * Аватар пользователя.
     *
     * <p>Связь One-to-One с каскадным удалением: при удалении пользователя
     * аватар удаляется автоматически ({@code orphanRemoval = true}).
     *
     * <p>Аннотация {@code @JsonManagedReference} указывает, что это "родительская"
     * сторона связи при сериализации в JSON (избегает бесконечной рекурсии).
     *
     * @see Avatar
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Avatar avatar;

    /**
     * Статус студента в университете (жизненный цикл).
     *
     * <p>Отражает текущее положение студента и влияет на доступ к системе.
     * Отличается от {@link Role}, которая определяет права доступа (что можно делать).
     *
     * @see Status
     */
    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Номер телефона пользователя.
     * Опциональное поле, хранится в зашифрованном виде.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Курс обучения студента.
     * Используется для фильтрации и персональных предложений.
     */
    @Column(name = "course")
    private Integer course;

    /**
     * Баланс внутренней валюты студента.
     *
     * <p>Хранится в формате {@link BigDecimal} с точностью 10 цифр и 2 знаками
     * после запятой для избежания ошибок плавающей точки при финансовых операциях.
     *
     * <p>По умолчанию равен {@link BigDecimal#ZERO}. Используется для:
     * <ul>
     *   <li>Начисления валюты за выполнение квестов</li>
     *   <li>Списания валюты при покупке мерча</li>
     * </ul>
     *
     * <p><b>Важно:</b> операции с балансом должны выполняться в транзакции
     * с пессимистичной блокировкой для предотвращения race condition.
     */
    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Номер студенческого билета.
     *
     * <p>Уникальный идентификатор студента в университете.
     * Генерируется автоматически базой данных PostgreSQL при создании пользователя.
     */
    @Column(name = "student_ticket")
    private String studentTicket;

    /**
     * Роль пользователя в системе.
     *
     * <p>Определяет права доступа и видимость данных.
     * Роль зашивается в JWT при генерации токена и проверяется через
     * Spring Security аннотации {@code @PreAuthorize("hasRole('CURATOR')")}.
     *
     * @see Role
     */
    @Column (name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Email пользователя.
     *
     * <p>Используется как username для аутентификации (вместо отдельного поля username).
     * Должен быть уникальным (требование для корректной работы системы входа).
     *
     * <p><b>Важно:</b> email используется в {@link AppUserDetails} как логин,
     * в {@link UserDetailsServiceImpl} для загрузки
     * пользователя, и в {@link EmailService} для
     * отправки писем активации.
     */
    @Column (name = "email")
    private String email;

    /**
     * Хэш пароля пользователя.
     *
     * <p>Хранится в захэшированном виде (BCrypt). Никогда не хранится в открытом виде.
     *
     * <p>Пустое значение ({@code null}) означает, что пользователь еще не установил
     * пароль (первый вход не выполнен).
     *
     * <p>Устанавливается при первом входе через
     * {@link AccountActivationService#confirmActivation(String, String)}.
     */

    @Column (name = "password")
    private String password;

    /**
     * Токен для установки первого пароля (резервное хранилище).
     *
     * <p>Используется при первом входе пользователя в систему. Когда деканат
     * добавляет студента, пароль пуст. Студент получает email с одноразовой
     * ссылкой для установки первого пароля.
     *
     * <p>Flow:
     * <ol>
     *   <li>Деканат создает пользователя со статусом ACTIVE и пустым паролем</li>
     *   <li>Система генерирует токен и отправляет ссылку на email</li>
     *   <li>Токен сохраняется в Redis (основной) или в этом поле (fallback)</li>
     *   <li>Студент переходит по ссылке и устанавливает пароль</li>
     *   <li>После установки пароля токен удаляется</li>
     * </ol>
     *
     * @see AccountActivationService
     */

    @Column(name = "activation_token")
    private String activationToken;

    public User(Long id, String firstName, String lastName, Faculty faculty, Status status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty = faculty;
        this.status = status;
    }

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getFirstName(), user.getFirstName()) && Objects.equals(getLastName(), user.getLastName()) && getGender() == user.getGender() && Objects.equals(getBirthDate(), user.getBirthDate()) && Objects.equals(getFaculty(), user.getFaculty()) && Objects.equals(getAvatar(), user.getAvatar()) && getStatus() == user.getStatus() && Objects.equals(getPhoneNumber(), user.getPhoneNumber()) && Objects.equals(getCourse(), user.getCourse()) && Objects.equals(getBalance(), user.getBalance()) && Objects.equals(getStudentTicket(), user.getStudentTicket()) && getRole() == user.getRole() && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPassword(), user.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getGender(), getBirthDate(), getFaculty(), getAvatar(), getStatus(), getPhoneNumber(), getCourse(), getBalance(), getStudentTicket(), getRole(), getEmail(), getPassword());
    }

    @Override
    public String toString() {
        return "User{" +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                ", faculty=" + faculty.getId() +
                ", userStatus=" + status +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", course=" + course +
                ", balance=" + balance +
                ", studentTicket='" + studentTicket + '\'' +
                ", userRole=" + role +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
