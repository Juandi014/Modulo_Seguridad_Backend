package com.uc.ms_security.service.impl;

import com.uc.ms_security.service.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Modo desarrollo: si no hay SMTP configurado, el correo se escribe en la
// consola del backend para poder probar sin una cuenta de correo.
public class ConsoleEmailServiceImpl implements IEmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailServiceImpl.class);

    @Override
    public void send(String to, String subject, String body) {
        log.info("""

                ===================== CORREO (modo consola) =====================
                Para:   {}
                Asunto: {}

                {}
                =================================================================
                """, to, subject, body);
    }
}
