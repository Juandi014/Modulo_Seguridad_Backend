package com.uc.ms_security.dto.auth;

import com.uc.ms_security.dto.auth.AuthResponseDTO;

// Resultado del login con credenciales. Solo uno de los dos viene lleno:
// - session: autenticación completa (sin 2FA).
// - twoFactor: falta validar el código; todavía no hay token.
public record LoginResponseDTO(
        boolean twoFactorRequired,
        AuthResponseDTO session,
        TwoFactorChallengeDTO twoFactor) {

    public static LoginResponseDTO completed(AuthResponseDTO session) {
        return new LoginResponseDTO(false, session, null);
    }

    public static LoginResponseDTO pendingTwoFactor(TwoFactorChallengeDTO challenge) {
        return new LoginResponseDTO(true, null, challenge);
    }
}
