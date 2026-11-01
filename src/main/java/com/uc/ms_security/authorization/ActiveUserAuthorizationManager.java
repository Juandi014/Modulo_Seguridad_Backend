package com.uc.ms_security.authorization;

import com.uc.ms_security.service.IAccessControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Autoriza rutas personales (ej. "Mi perfil") a cualquier usuario
 * autenticado, siempre que siga activo. No exige permisos de rol
 * porque cada usuario solo opera sobre sus propios datos.
 */
@Component
@RequiredArgsConstructor
public class ActiveUserAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    private final IAccessControlService accessControlService;

    @Override
    public AuthorizationResult authorize(
            Supplier<? extends Authentication> authenticationSupplier,
            RequestAuthorizationContext context) {
        boolean active = AuthenticatedUserId.from(authenticationSupplier.get())
                .map(accessControlService::isActiveUser)
                .orElse(false);
        return new AuthorizationDecision(active);
    }
}
