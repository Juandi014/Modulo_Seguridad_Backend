package com.uc.ms_security.dto.session;

import com.uc.ms_security.entity.enums.AuthProvider;

import java.time.LocalDateTime;

// No expone el token ni el código 2FA: solo datos para consultar sesiones.
// "current" indica si es la sesión desde la que se hace la consulta.
public record SessionResponseDTO(
        Long id,
        AuthProvider authMethod,
        LocalDateTime createdAt,
        LocalDateTime expiration,
        LocalDateTime revokedAt,
        boolean active,
        boolean current) {
}
