package com.uc.ms_security.dto.auth;

import com.uc.ms_security.entity.enums.AuthProvider;

// Datos de identidad entregados por el proveedor, ya normalizados.
public record OAuthProfileDTO(
        AuthProvider provider,
        String providerId,
        String email,
        String name) {
}
