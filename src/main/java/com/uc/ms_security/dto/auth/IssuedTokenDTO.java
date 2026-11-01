package com.uc.ms_security.dto.auth;

import java.time.Instant;

// Token recién emitido: su valor, su identificador (jti) y su expiración.
public record IssuedTokenDTO(
        String value,
        String id,
        Instant expiresAt) {
}
