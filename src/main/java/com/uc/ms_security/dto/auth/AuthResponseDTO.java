package com.uc.ms_security.dto.auth;

import com.uc.ms_security.dto.user.UserResponseDTO;

import java.time.Instant;

// Respuesta de un inicio de sesión exitoso (credenciales u OAuth):
// el token de la sesión interna, su expiración y el usuario autenticado.
public record AuthResponseDTO(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        UserResponseDTO user) {
}
