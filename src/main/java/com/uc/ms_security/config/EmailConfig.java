package com.uc.ms_security.config;

import com.uc.ms_security.service.IEmailService;
import com.uc.ms_security.service.impl.ConsoleEmailServiceImpl;
import com.uc.ms_security.service.impl.NotificationsEmailServiceImpl;
import com.uc.ms_security.service.impl.SmtpEmailServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestClient;

import java.time.Duration;

// Elige la implementación de IEmailService según la configuración:
// con NOTIFICATIONS_URL -> ms-notifications (Gmail API);
// si no, con MAIL_HOST -> SMTP real; sin ninguno -> consola (desarrollo).
@Configuration
public class EmailConfig {

    @Bean
    public IEmailService emailService(
            ObjectProvider<JavaMailSender> mailSender,
            @Value("${security.notifications.url:}") String notificationsUrl,
            @Value("${security.notifications.api-key:}") String notificationsApiKey,
            @Value("${spring.mail.host:}") String host,
            @Value("${security.mail.from}") String from) {
        if (!notificationsUrl.isBlank()) {
            return new NotificationsEmailServiceImpl(
                    notificationsClient(notificationsUrl, notificationsApiKey));
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (host.isBlank() || sender == null) {
            return new ConsoleEmailServiceImpl();
        }
        return new SmtpEmailServiceImpl(sender, from);
    }

    private RestClient notificationsClient(String baseUrl, String apiKey) {
        // Tiempos límite cortos: si ms-notifications no responde, el login o
        // la recuperación no se quedan esperando indefinidamente
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(15));
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("X-Api-Key", apiKey)
                .build();
    }
}
