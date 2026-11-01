package com.uc.ms_security.authorization;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

// Obtiene el id del usuario autenticado: el "sub" del JWT es el id del usuario.
public final class AuthenticatedUserId {

    private AuthenticatedUserId() {
    }

    /** Id del usuario autenticado; vacío si no hay autenticación o el sub no es numérico. */
    public static Optional<Long> from(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.valueOf(authentication.getName()));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    /** Id del usuario dueño del token (usado por los controladores). */
    public static Long from(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
