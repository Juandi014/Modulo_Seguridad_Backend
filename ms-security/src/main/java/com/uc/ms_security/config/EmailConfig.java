package com.uc.ms_security.config;

import com.uc.ms_security.service.IEmailService;
import com.uc.ms_security.service.impl.ConsoleEmailServiceImpl;
import com.uc.ms_security.service.impl.SmtpEmailServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

// Elige la implementación de IEmailService según la configuración:
// con MAIL_HOST -> SMTP real; sin MAIL_HOST -> consola (desarrollo).
@Configuration
public class EmailConfig {

    @Bean
    public IEmailService emailService(
            ObjectProvider<JavaMailSender> mailSender,
            @Value("${spring.mail.host:}") String host,
            @Value("${security.mail.from}") String from) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (host.isBlank() || sender == null) {
            return new ConsoleEmailServiceImpl();
        }
        return new SmtpEmailServiceImpl(sender, from);
    }
}
