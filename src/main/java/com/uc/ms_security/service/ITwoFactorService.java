package com.uc.ms_security.service;

import com.uc.ms_security.dto.auth.AuthResponseDTO;
import com.uc.ms_security.dto.auth.TwoFactorChallengeDTO;
import com.uc.ms_security.entity.User;

// Segundo factor durante el inicio de sesión (código de 6 dígitos por correo).
public interface ITwoFactorService {

    /** Crea la sesión pendiente y envía el código. Todavía no hay token. */
    TwoFactorChallengeDTO startChallenge(User user);

    /** Valida el código; si es correcto la sesión queda activa con su token. */
    AuthResponseDTO verify(String challengeId, String code);

    /** El usuario abandona el proceso: el intento queda anulado. */
    void cancel(String challengeId);
}
