package com.uc.ms_security.service.impl;

import com.uc.ms_security.service.IEmailService;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

// Envía los correos a través del microservicio ms-notifications (Python + Gmail API).
// Se activa en EmailConfig cuando existe NOTIFICATIONS_URL.
public class NotificationsEmailServiceImpl implements IEmailService {

    private static final Logger log = LoggerFactory.getLogger(NotificationsEmailServiceImpl.class);

    private final RestClient restClient;

    public NotificationsEmailServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void send(String to, String subject, String body) {
        try {
            restClient.post()
                    .uri("/send-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("to", to, "subject", subject, "body", body))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            // No se interrumpe la operación del usuario; queda registrado en el log
            log.error("ms-notifications no pudo enviar el correo a {}: {}", to, exception.getMessage());
        }
    }
}
