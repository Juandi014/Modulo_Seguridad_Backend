package com.uc.ms_security.service.impl;

import com.uc.ms_security.service.IEmailService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Envía correos reales por SMTP (Gmail, Mailtrap, etc.). Se activa en EmailConfig.
public class SmtpEmailServiceImpl implements IEmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpEmailServiceImpl(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            // No se interrumpe la operación del usuario; queda registrado en el log
            log.error("No se pudo enviar el correo a {}: {}", to, exception.getMessage());
        }
    }
}
