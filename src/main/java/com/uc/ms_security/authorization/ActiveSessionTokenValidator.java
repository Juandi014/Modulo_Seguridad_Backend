package com.uc.ms_security.authorization;

import com.uc.ms_security.service.ISessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Además de la firma y la expiración del JWT (que valida Spring), exige que
 * su sesión interna siga vigente. Así el logout invalida el token aunque
 * su fecha de expiración todavía no haya llegado.
 */
@Component
@RequiredArgsConstructor
public class ActiveSessionTokenValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_SESSION = new OAuth2Error(
            "invalid_token",
            "La sesión no existe, expiró o fue cerrada",
            null
    );

    private final ISessionService sessionService;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String tokenId = jwt.getId();
        boolean active = tokenId != null
                && sessionService.isActive(tokenId);
        return active
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(INVALID_SESSION);
    }
}
