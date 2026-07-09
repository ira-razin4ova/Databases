package ru.hogwarts.school.util;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Сервис отправки email-уведомлений через Spring Mail.
 *
 * <p>Использует {@link JavaMailSender} для отправки простых текстовых писем.
 * Конфигурация SMTP-сервера (хост, порт, логин, пароль) задается
 * в {@code application.properties} через свойства {@code spring.mail.*}.
 *
 * <p>Текущая реализация отправляет:
 * <ul>
 *   <li>Письма активации аккаунта со ссылкой и одноразовым токеном</li>
 * </ul>
 *
 * <p><b>Важно для продакшена:</b>
 * <ul>
 *   <li>Для production-окружения рекомендуется использовать шаблонизатор
 *       (Thymeleaf, FreeMarker) вместо конкатенации строк</li>
 *   <li>Отправку писем стоит делать асинхронной ({@code @Async}),
 *       чтобы не блокировать основной поток запроса</li>
 *   <li>Адрес отправителя ({@code fromEmail}) должен быть верифицирован
 *       у SMTP-провайдера</li>
 * </ul>
 *
 * * @see ru.hogwarts.school.service.AccountActivationService#initiateActivation(String)
 */

@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendActivationEmail(String toEmail, String studentName, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Активация аккаунта в университетском портале");

        String text = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Деканат добавил вас в систему. Чтобы установить пароль и активировать аккаунт, " +
                        "перейдите по следующей ссылке:\n%s\n\n" +
                        "Ссылка действительна в течение 24 часов.\n" +
                        "Если вы не понимаете, о чем речь, просто проигнорируйте это письмо.",
                studentName, activationLink
        );

        message.setText(text);

        mailSender.send(message);
    }
}




