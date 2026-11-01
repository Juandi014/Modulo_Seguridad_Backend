package com.uc.ms_security.dto.auth;

import java.time.Instant;

// El login con contraseña fue correcto pero falta el segundo factor:
// identifica el intento de login y dónde se envió el código.
public record TwoFactorChallengeDTO(
        String challengeId,
        String maskedEmail,
        Instant codeExpiresAt) {
}
